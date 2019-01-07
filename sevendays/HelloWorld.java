package edu.unl.hcc.system.interview.concurrency.sevendays;

import java.util.concurrent.Callable;

/**
 * Created by chehe on 2017/4/28.
 */
public class HelloWorld {
  class Task implements Callable {

    public Object call() throws Exception {
      System.out.println("Hello from callable thread");
      return null;
    }
    public Task(){}
  }
  public static void main(String[] args) throws InterruptedException{
    Thread myThread = new Thread(){
      public void run(){
        //Thread.yield();
        try {
          Thread.sleep(1);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        System.out.println("Hello From new thread");
      }
    };

    myThread.start();
    System.out.println("Hello from main thread");
    myThread.join();

    //Task task = new Task();
    //Task.run();
  }
}
