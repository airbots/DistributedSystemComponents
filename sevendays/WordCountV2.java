/*
package edu.unl.hcc.system.interview.concurrency.sevendays;

import java.util.HashMap;

/**
 * Created by chehe on 2017/4/28.

public class WordCountV2 {
  public static void main(String[] args) {
    ArrayBlockingQueue<Page> queue = new ArrayBlockingQueue<Page>(100);
    HashMap<String, Integer> counts = new HashMap<String, Integer>();

    Thread counter = new Thread(new Counter(queue, counts));
    Thread parser = new Thread(new ProducerParser(queue));


    counter.start();
    parser.start();
    parser.join();
    queue.put(new PoisonPill());
    counter.join();
  }
}
*/