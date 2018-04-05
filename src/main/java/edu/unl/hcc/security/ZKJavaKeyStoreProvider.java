package edu.unl.hcc.security;


import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkNoNodeException;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ZKJavaKeyStoreProvider extends AbstractJavaKeyStoreProvider {
    public static final Logger LOG = LoggerFactory.getLogger(
            ZooKeeperJavaKeyStoreProvider.class);

    private static final String SCHEME_NAME = "zkjceks";
    private static final String CREDENTIAL_PASSWORD_DEFAULT = "oehpcs";

    private String tmpFile =
            "SwapFile_" + System.currentTimeMillis() + ".jceks";

    private String zPath;
    private String zkHost;
    private ZkClient client;
    private char[] passwd;
    private URI zkUri;
    private KeyStore keyStore;
    private char[] password = null;
    private boolean changed = false;
    private Lock readLock;
    private Lock writeLock;

    public ZKJavaKeyStoreProvider(URI uri, String hostWithPort)
            throws IOException {
        zkUri = uri;
        zkHost = hostWithPort;
        passwd = getPassword();
        client = new ZkClient(zkHost);
        ReadWriteLock lock = new ReentrantReadWriteLock(true);
        readLock = lock.readLock();
        writeLock = lock.writeLock();
        init(uri);
        loadKeyStore();
    }

    public String getPath() {
        return zPath;
    }

    public void setPath(String p) {
        this.zPath = p;
    }

    public char[] getPassword() {
        if (password == null) {
            LOG.warn("Password is null, please setPassword() first");
        }
        return password;
    }

    public void setPassword(char[] pass) {
        this.password = pass;
    }

    public boolean isChanged() {
        return changed;
    }

    public void setChanged(boolean chg) {
        this.changed = chg;
    }

    public Lock getReadLock() {
        return readLock;
    }

    public Lock getWriteLock() {
        return writeLock;
    }

    public URI getUri() {
        return zkUri;
    }

    public KeyStore getKeyStore() {
        return keyStore;
    }


    class StringOutputStream extends OutputStream {
        StringBuilder sb;

        public void write(int bytes) throws IOException {
            sb.append((char) bytes);
        }

        public String getString() {
            return sb.toString();
        }
    }

    protected String getSchemeName() {
        return SCHEME_NAME;
    }

    protected boolean keystoreExists() throws IOException {
        /* The keystore loader doesn't handle zero length files. */
        if (client.exists(zPath)) {
            Object data = client.readData(zPath);
            if (data != null) return true;
        }
        return false;
    }

    protected InputStream getInputStreamForFile() throws IOException {
        String str = client.readData(zPath, true).toString();
        return new ByteArrayInputStream(str.getBytes("ISO-8859-1"));
    }

    @Override
    public void flush() throws IOException {
        writeLock.lock();
        try {
            if (!changed) {
                LOG.debug("Keystore hasn't changed, returning.");
                return;
            }
            LOG.debug("Writing out keystore.");
            try {
                getKeyStore().store(createSwapFileOutStream(), getPassword());
                storeKeyStore();
            } catch (KeyStoreException e) {
                throw new IOException("Can't store keystore " + this, e);
            } catch (NoSuchAlgorithmException e) {
                throw new IOException("No such algorithm storing keystore " + this, e);
            } catch (CertificateException e) {
                throw new IOException("Certificate exception storing keystore " + this, e);
            }
            changed = false;
            //flush node into the cluster
        } finally {
            writeLock.unlock();
        }
    }

    protected void init(URI uri) throws IOException {
        zPath = unnestUri(uri);
        if (LOG.isDebugEnabled()) {
            LOG.debug("backing jks path initialized to " + zPath);
        }
        //create znode according to the given path
        try {
            if (client.exists(zPath)) {
                LOG.debug("ZNode already exists", zPath);
                getKeyStore().load(getInputStreamForFile(), getPassword());
            } else {
                client.createPersistent(zPath);
            }
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private void storeKeyStore() throws KeyStoreException,
            NoSuchAlgorithmException, CertificateException, IOException {

        try {
            byte[] data = new byte[(int) createSwapFile().length()];
            FileInputStream fis = createFIS();
            fis.read(data);
            client.writeData(zPath, new String(data, "ISO-8859-1"));

        } catch (ZkNoNodeException nne) {
            String parentDir = zPath.substring(0, zPath.lastIndexOf('/'));
            if (parentDir.length() != 0) {
                client.createPersistent(parentDir, true);
            }
            try {
                client.createPersistent(zPath, getFileContent(createFIS(), "ISO-8859-1"));
            } catch (ZkNodeExistsException nee) {
                client.writeData(zPath, getFileContent(createFIS(), "ISO-8859-1"));
            }
        } finally {
            deleteSwapFile(tmpFile);
        }
    }

    public AbstractJavaKeyStoreProvider.CredentialEntry getCredentialEntry(String alias)
            throws IOException {
        readLock.lock();
        try {
            SecretKeySpec key;
            try {
                if (!keyStore.containsAlias(alias)) {
                    return null;
                }
                key = (SecretKeySpec) keyStore.getKey(alias, getPassword());
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
            return new AbstractJavaKeyStoreProvider.CredentialEntry(alias,
                    bytesToChars(key.getEncoded()));
        } finally {
            readLock.unlock();
        }
    }

    public static char[] bytesToChars(byte[] bytes) throws IOException {
        String pass;
        pass = new String(bytes, Charsets.UTF_8);
        return pass.toCharArray();
    }

    public List<String> getAliases() throws IOException {
        readLock.lock();
        try {
            ArrayList<String> list = new ArrayList();
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
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public AbstractJavaKeyStoreProvider.CredentialEntry createCredentialEntry(String alias,
                                                                              char[] credential)
            throws IOException {
        writeLock.lock();
        try {
            if (keyStore.containsAlias(alias)) {
                throw new IOException("Credential " + alias + " already exists in "
                        + this);
            }
            return innerSetCredential(alias, credential);
        } catch (KeyStoreException e) {
            throw new IOException("Problem looking up credential " + alias + " in "
                    + this, e);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void deleteCredentialEntry(String name) throws IOException {
        writeLock.lock();
        try {
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
        } finally {
            writeLock.unlock();
        }
    }

    AbstractJavaKeyStoreProvider.CredentialEntry innerSetCredential(String alias,
                                                                    char[] material)
            throws IOException {
        writeLock.lock();
        try {
            keyStore.setKeyEntry(alias,
                    new SecretKeySpec(new String(material).getBytes("UTF-8"), "AES"),
                    password, null);
        } catch (KeyStoreException e) {
            throw new IOException("Can't store credential " + alias + " in " + this,
                    e);
        } finally {
            writeLock.unlock();
        }
        changed = true;
        return new AbstractJavaKeyStoreProvider.CredentialEntry(alias, material);
    }

    /**
     * Open up and initialize the keyStore.
     *
     * @throws IOException If there is a problem reading the password file
     *                     or a problem reading the keystore.
     */
    private void loadKeyStore() throws IOException {
        try {
            if (password == null) {
                password = CREDENTIAL_PASSWORD_DEFAULT.toCharArray();
            }
            KeyStore ks;
            ks = KeyStore.getInstance("jceks");
            if (keystoreExists()) {
                try (InputStream in = getInputStreamForFile()) {
                    ks.load(in, password);
                }
            } else {
                ks.load(null, password);
            }
            keyStore = ks;
        } catch (KeyStoreException e) {
            throw new IOException("Can't create keystore", e);
        } catch (GeneralSecurityException e) {
            throw new IOException("Can't load keystore " + getPath(), e);
        }
    }

    private String getFileContent(FileInputStream fis, String encoding) throws IOException {
        return IOUtils.toString(fis, encoding);
    }

    private FileOutputStream createSwapFileOutStream() throws FileNotFoundException {
        File swapFile = createSwapFile();
        if (swapFile.exists()) swapFile.delete();
        return new FileOutputStream(swapFile);
    }

    private FileInputStream createFIS() throws FileNotFoundException {
        File swapFile = new File(System.getProperty("java.io.tmpdir") + "/" + tmpFile);
        return new FileInputStream(swapFile);
    }

    private void deleteSwapFile(String path) {
        File swapFile = new File(path);
        if (swapFile.exists()) swapFile.delete();
    }

    private File createSwapFile() {
        return new File(System.getProperty("java.io.tmpdir") + "/" + tmpFile);
    }
}
