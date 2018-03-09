package edu.unl.hcc.security;

import org.apache.commons.io.IOUtils;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.ProviderUtils;
import org.apache.hadoop.security.alias.AbstractJavaKeyStoreProvider;
import org.apache.hadoop.security.alias.CredentialProvider;
import org.apache.hadoop.security.alias.CredentialProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.UUID;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkNoNodeException;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;


/**
 * CredentialProvider based on Java's KeyStore file format. The file may be
 * stored only on the local filesystem using the following name mangling:
 * zkjceks://root/zknode -> zookeeper znode under /root/zknode
 * Since AbstractJavaKeyStoreProvider's initFileSystem has problem
 */
@InterfaceAudience.Private
public final class ZooKeeperJavaKeyStoreProvider extends
    AbstractJavaKeyStoreProvider {
  public static final Logger LOG = LoggerFactory.getLogger(
      ZooKeeperJavaKeyStoreProvider.class);

  public static final String SCHEME_NAME = "zkjceks";
  public static final String KEYSTORE_ZOOKEEPER_PATH_DEFAULT =
      "zkjceks://zookeeper/oehpcs-connect";
  public static final String ZOOKEEPER_SERVER_DEFAULT = "127.0.0.1:2181";

  public static String output;
  public static String input;
  private static final String uniqueID = UUID.randomUUID().toString();
  private static final String tmpFile =
      System.getProperty("java.io.tmpdir")+"/swapFile-"+uniqueID+".jceks";

  private String zPath;
  private String zkHost;
  private ZkClient client;


  private ZooKeeperJavaKeyStoreProvider(URI uri, Configuration conf)
      throws IOException {
    super(uri, conf);
    deleteSwapFile(tmpFile);
  }


  class StringOutputStream extends OutputStream {
    StringBuilder sb =  new StringBuilder();
    public void write(int bytes) throws IOException {
      sb.append((byte) bytes);
    }

    public String getString(){
      return sb.toString();
    }
  }

  @Override
  protected String getSchemeName() {
    return SCHEME_NAME;
  }

  @Override
  protected boolean keystoreExists() throws IOException {
    /* The keystore loader doesn't handle zero length files. */
    if(client.exists(zPath)) {
      Object data = client.readData(zPath);
      if(data != null) return true;
    }
    return false;
  }

  @Override
  protected OutputStream getOutputStreamForKeystore() throws IOException {
    return createSwapFileOutStream();
  }

  @Override
  protected InputStream getInputStreamForFile() throws IOException {
    String str = client.readData(zPath,true).toString();
    return new ByteArrayInputStream(str.getBytes("ISO-8859-1"));
  }

  @Override
  protected void createPermissions(String s) throws IOException {}

  @Override
  protected void stashOriginalFilePermissions() throws IOException {}

  @Override
  public void flush() throws IOException {
    super.flush();
    LOG.debug("Writing out keystore.");

    try {
      storeKeyStore();
    } catch (KeyStoreException e) {
      throw new IOException("Can't store keystore " + this, e);
    } catch (NoSuchAlgorithmException e) {
      throw new IOException("No such algorithm storing keystore " + this, e);
    } catch (CertificateException e) {
      throw new IOException("Certificate exception storing keystore " + this, e);
    }
    //flush node into the cluster
  }

  protected void initFileSystem(URI uri) throws IOException {
    String[] path = ProviderUtils.unnestUri(uri).toString().split(":");
    zPath = path[1];

    //create client
    //zkHost = getConf().get("zookeeper.server", ZOOKEEPER_SERVER_DEFAULT);
    client = new ZkClient(zkHost);

    if (LOG.isDebugEnabled()) {
      LOG.debug("backing jks path initialized to " + zPath);
    }
    //create znode according to the given path

    if(client.exists(zPath)) {
      LOG.debug("ZNode already exists", zPath);
    } else {
      client.createPersistent(zPath);
    }
  }

  /**
   * The factory to create JksProviders, which is used by the ServiceLoader.
   */
  public static class Factory extends CredentialProviderFactory {
    @Override
    public CredentialProvider createProvider(URI providerName,
                                             Configuration conf) throws IOException {
      if (SCHEME_NAME.equals(providerName.getScheme())) {
        return new ZooKeeperJavaKeyStoreProvider(providerName, conf);
      }
      return null;
    }
  }

  private void storeKeyStore() throws KeyStoreException,
      NoSuchAlgorithmException, CertificateException, IOException {

    getKeyStore().store(createSwapFileOutStream(),getPassword());
    try {
      byte[] data = new byte[(int) createSwapFile().length()];
      FileInputStream fis = createFIS();
      fis.read(data);
      client.writeData(zPath,new String(data,"ISO-8859-1"));

    } catch (ZkNoNodeException nne) {
      String parentDir = zPath.substring(0, zPath.lastIndexOf('/'));
      if (parentDir.length() != 0) {
        client.createPersistent(parentDir, true);
      }
      try {
        client.createPersistent(zPath, getFileContent(createFIS(),"ISO-8859-1"));
      } catch (ZkNodeExistsException nee) {
        client.writeData(zPath, getFileContent(createFIS(),"ISO-8859-1"));
      }
    } finally {
      deleteSwapFile(tmpFile);
    }
  }

  private String getFileContent(FileInputStream fis, String encoding ) throws IOException
  {
    return IOUtils.toString(fis, encoding);
  }

  private FileOutputStream createSwapFileOutStream() throws FileNotFoundException {
    File swapFile = createSwapFile();
    if(swapFile.exists()) swapFile.delete();
    return new FileOutputStream(swapFile);
  }

  private FileInputStream createFIS() throws FileNotFoundException {
    File swapFile = new File(System.getProperty("java.io.tmpdir")+"/swapFile-"+uniqueID+".jceks");
    return new FileInputStream(swapFile);
  }

  private void deleteSwapFile(String path){
    File swapFile = new File(path);
    if(swapFile.exists()) swapFile.delete();
  }

  private File createSwapFile(){
    return new File(System.getProperty("java.io.tmpdir")+"/swapFile-"+uniqueID+".jceks");
  }

}

