/*
package edu.unl.hcc.concurrency.sevendays;

/**
 * Created by chehe on 2017/4/28.

public class ProducerParser {
  private BlockingQueue<Page> queue;
  public ProducerParser(BlockingQueue<Page> queue) {
    this.queue=queue;
  }

  public void run(){
    try{
      iterable<Page> pages = new Pages(100000, "enwiki.xml");
      for(Page page: pages){
        queue.put(page);
      }
    }catch (Exception e) {
      e.printStackTrace();
    }
  }
}
*/