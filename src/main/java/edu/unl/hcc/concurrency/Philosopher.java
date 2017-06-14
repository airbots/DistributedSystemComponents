
/*
package edu.unl.hcc.concurrency;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by chehe on 2017/4/20.

public class Philosopher extends Thread{
  private boolean eating;
  private Philosopher left;
  private Philosopher right;
  private ReentrantLock table;
  private Condition condition;
  private Random random;

  public Philosopher(ReentrantLock table){
    eating = false;
    this.table = table;
    condition = table.newCondition();
    random = new Random();
  }

  public void setLeft(Philosopher left){
    if(null != left)this.left = left;
  }

  public void setRight(Philosopher right){
    if(null!=right)this.right = right;

  }

  public void run(){
    try{
      while(true){
        think();
        eat();
      }
    } catch(InterruptedException e){}
  }

  private void think() throws InterruptedException {
    table.lock();
    try{
      eating=false;
      left.condition.signal();
      right.condition.signal();
    } finally{
      table.unlock();
    }
    Thread.sleep(1000);
  }

  private void eat() throws InterruptedException {
    table.lock();
    try{
      while(left.eating || right.eating){
        condition.wait();
      }
      System.out.println("Philosoper " + Thread.getId() + " is eating");
      eating = true;
    } finally{table.unlock();}
    Thread.sleep(1000);
  }

  public static void main(String[] args){
    //create a philosoper table
    ReentrantLock table1=new ReentrantLock();
    ArrayList<Philosopher> philosopherPool = new ArrayList<Philosopher>();
    for(int i=0;i<10;i++){
      Philosopher philosopher = new Philosopher(table1);
      philosopherPool.add(philosopher);
    }


  }
}


*/