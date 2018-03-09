package edu.unl.hcc.security;

import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public abstract class AbstractJavaKeyStoreProvider {

  public static final String CREDENTIAL_PASSWORD_DEFAULT = "oehpcs";
  protected KeyStore keyStore;
  protected boolean changed = false;
  protected String passwd;
  private String path;

  /**
   * The combination of both the alias and the actual credential value.
   */
  public static class CredentialEntry {
    private final String alias;
    private final char[] credential;

    protected CredentialEntry(String alias,
                              char[] credential) {
      this.alias = alias;
      this.credential = credential;
    }

    public String getAlias() {
      return alias;
    }

    public char[] getCredential() {
      return credential;
    }

    public String toString() {
      StringBuilder buf = new StringBuilder();
      buf.append("alias(");
      buf.append(alias);
      buf.append(")=");
      if (credential == null) {
        buf.append("null");
      } else {
        for(char c: credential) {
          buf.append(c);
        }
      }
      return buf.toString();
    }
  }

  public String getPath() {
    return path;
  }

  public void setPath(String p) {
    this.path = p;
  }
  /**
   * Indicates whether this provider represents a store
   * that is intended for transient use - such as the UserProvider
   * is. These providers are generally used to provide job access to
   * passwords rather than for long term storage.
   * @return true if transient, false otherwise
   */
  public boolean isTransient() {
    return false;
  }

  /**
   * Ensures that any changes to the credentials are written to persistent
   * store.
   * @throws IOException
   */
  public abstract void flush() throws IOException;

  /**
   * Get the credential entry for a specific alias.
   * @param alias the name of a specific credential
   * @return the credentialEntry
   * @throws IOException
   */
  public CredentialEntry getCredentialEntry(String alias)
      throws IOException {
    SecretKeySpec key;
    try {
      if (!keyStore.containsAlias(alias)) {
        return null;
      }
      key = (SecretKeySpec) keyStore.getKey(alias, passwd.toCharArray());
    } catch (KeyStoreException e) {
      throw new IOException("Can't get credential " + alias + " from "
          + getPath(), e);
    } catch (NoSuchAlgorithmException e) {
      throw new IOException("Can't get algorithm for credential " + alias
          + " from " + getPath(), e);
    } catch (UnrecoverableKeyException e) {
      throw new IOException("Can't recover credential " + alias + " from "
          + getPath(), e);
    }
    return new CredentialEntry(alias, bytesToChars(key.getEncoded()));
  }

  /**
   * Get the aliases for all credentials.
   * @return the list of alias names
   * @throws IOException
   */
  public List<String> getAliases() throws IOException {
    ArrayList<String> list = new ArrayList<String>();
    String alias = null;
    try {
      Enumeration<String> e = keyStore.aliases();
      while (e.hasMoreElements()) {
        alias = e.nextElement();
        list.add(alias);
      }
    } catch (KeyStoreException e) {
      throw new IOException("Can't get alias " + alias + " from "
          + getPath(), e);
    }
    return list;
  }

  /**
   * Create a new credential. The given alias must not already exist.
   * @param alias the alias of the credential
   * @param credential the credential value for the alias.
   * @throws IOException
   */
  public CredentialEntry createCredentialEntry(String alias,
                                               char[] credential) throws IOException {
    try {
      if (keyStore.containsAlias(alias)) {
        throw new IOException("Credential " + alias + " already exists in "
            + this);
      }
      return innerSetCredential(alias, credential);
    } catch (KeyStoreException e) {
      throw new IOException("Problem looking up credential " + alias + " in "
          + this, e);
    }
  }

  /**
   * Delete the given credential.
   * @param name the alias of the credential to delete
   * @throws IOException
   */
  public void deleteCredentialEntry(String name) throws IOException {
    try {
      if (keyStore.containsAlias(name)) {
        keyStore.deleteEntry(name);
      } else {
        throw new IOException("Credential " + name + " does not exist in "
            + this);
      }
    } catch (KeyStoreException e) {
      throw new IOException("Problem removing " + name + " from " + this, e);
    }
    changed = true;
  }

  /**
   * Does this provider require a password? This means that a password is
   * required for normal operation, and it has not been found through normal
   * means. If true, the password should be provided by the caller using
   * setPassword().
   * @return Whether or not the provider requires a password
   * @throws IOException
   */
  public boolean needsPassword() throws IOException {
    //find password from local or public places
    return true;
  }

  /**
   * If a password for the provider is needed, but is not provided, this will
   * return a warning and instructions for supplying said password to the
   * provider.
   * @return A warning and instructions for supplying the password
   */
  public String noPasswordWarning() {
    return null;
  }

  /**
   * If a password for the provider is needed, but is not provided, this will
   * return an error message and instructions for supplying said password to
   * the provider.
   * @return An error message and instructions for supplying the password
   */
  public String noPasswordError() {
    return null;
  }

  /**
   * Obtain the output stream from KeyStore
   * @return A outputstream obtained from KeyStore
   */
  protected ByteArrayOutputStream getOutputStreamForKeystore() { return null; }

  public static char[] bytesToChars(byte[] bytes) throws IOException {
    String pass;
    pass = new String(bytes, StandardCharsets.UTF_8);
    return pass.toCharArray();
  }

  CredentialEntry innerSetCredential(String alias, char[] material)
      throws IOException {
    try {
      keyStore.setKeyEntry(alias,
          new SecretKeySpec(new String(material).getBytes("UTF-8"), "AES"),
          passwd.toCharArray(), null);
    } catch (KeyStoreException e) {
      throw new IOException("Can't store credential " + alias + " in " + this,
          e);
    }
    changed = true;
    return new CredentialEntry(alias, material);
  }


    /**
     * Convert a nested URI to decode the underlying path. The translation takes
     * the authority and parses it into the underlying scheme and authority.
     * For example, "zkjceks://zookeeper/my/path" is converted to
     * "/my/path".
     *
     * @param nestedUri the URI from the nested URI
     * @return the unnested path
     */
    public String unnestUri(URI nestedUri) {
        StringBuilder result = new StringBuilder();
        String authority = nestedUri.getAuthority();
        if (authority != null) {
            String[] parts = nestedUri.getAuthority().split("@", 2);
            if (parts.length == 2) {
                result.append(parts[1]);
            }
        }
        result.append(nestedUri.getPath());
        if (nestedUri.getQuery() != null) {
            result.append("?");
            result.append(nestedUri.getQuery());
        }
        if (nestedUri.getFragment() != null) {
            result.append("#");
            result.append(nestedUri.getFragment());
        }
        return result.toString();
    }
}
