package edu.unl.hcc.system.interview.concurrency.sevendays;

/**
 * Created by chehe on 2017/4/28.
 */
public class Counting {
  public static void main(String[] args) throws InterruptedException {
    class Counter{
      private int counter =0;
      public synchronized void increment() {++counter;}
      public int getCount() {
        return counter;
      }
    }
    final Counter counter = new Counter();
    class CountingThread extends Thread {
      public void run() {
        for(int i=0;i<10000;++i){
          counter.increment();
        }
      }
    }
    long start = System.currentTimeMillis();
    CountingThread t1 = new CountingThread();
    CountingThread t2 = new CountingThread();
    t1.start();
    t2.start();
    t1.join();
    t2.join();
    long stop = System.currentTimeMillis();
    System.out.println(counter.getCount() + "\nTakes: " + (stop-start));
  }
}
