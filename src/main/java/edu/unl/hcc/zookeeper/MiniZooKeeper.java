package edu.unl.hcc.zookeeper;

import org.apache.commons.io.FileUtils;
import org.apache.zookeeper.server.NIOServerCnxnFactory;
import org.apache.zookeeper.server.ZooKeeperServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;

public class MiniZooKeeper {

  private static final Logger LOG = LoggerFactory.getLogger(MiniZooKeeper.class);
  NIOServerCnxnFactory factory;
  ZooKeeperServer zooKeeper;
  String zkhost;
  File zPath;
  int port;
  int tickTime = 500;
  int clientPort;
  String tmpDir = System.getProperty("java.io.tmpdir");

  //for unit tests
  public MiniZooKeeper() {
    this("127.0.0.1",3000);
  }

  public MiniZooKeeper(String host) {
    this(host,3000);
  }

  public MiniZooKeeper(String host, int zkport) {
    this(host, zkport, new File(System.getProperty("java.io.tmpdir")));
  }

  public MiniZooKeeper(String host, int zkport, File path) {
    if(null!=host && path!=null) {
      this.zkhost = host;
      zPath = path;
      this.port = zkport;
    }
  }

  public void start() throws IOException, InterruptedException {
    this.zooKeeper = new ZooKeeperServer(zPath, zPath, tickTime);
    this.clientPort = zooKeeper.getClientPort();
    factory = new NIOServerCnxnFactory();
    InetSocketAddress addr = new InetSocketAddress(zkhost, port);
    factory.configure(addr, 0);
    factory.startup(zooKeeper);
  }

  public void stop() throws IOException {
    try {
      if(null!=zooKeeper) {
        zooKeeper.shutdown();
      }
      if(null!=factory) {
        factory.shutdown();
      }

    } catch (Exception e) {
      LOG.warn("Exception encounter when shutdown mini-zookeeper", e.getMessage());
    } finally {
        FileUtils.deleteDirectory(new File(tmpDir));
    }
  }
}
