package edu.unl.hcc.security;

import edu.unl.hcc.zookeeper.MiniZooKeeper;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

public class ZKJavaKeyStoreProviderTest {

    @Test
    public void testZKProvider() throws IOException {
        String provider = "zkjceks://zookeeper/keystore";
        MiniZooKeeper zookeeper = new MiniZooKeeper();
        ZKJavaKeyStoreProvider zkKeyStore;
        try {
            zookeeper.start();
            zkKeyStore = new ZKJavaKeyStoreProvider(new URI(provider),
                    "127.0.0.1:" + 2181);
            String credential = "Welcome1#";
            String alias = "kafka.connect.ocs.password";
            //test save credential
            zkKeyStore.createCredentialEntry(alias, credential.toCharArray());
            assertArrayEquals(zkKeyStore.getCredentialEntry(alias).getCredential(), credential.toCharArray());

            //test delete credential
            zkKeyStore.deleteCredentialEntry(alias);
            assertNull(zkKeyStore.getCredentialEntry(alias));

        } catch (Exception e) {
            fail(e.getMessage());
        } finally {
            zookeeper.stop();
        }
    }
}
