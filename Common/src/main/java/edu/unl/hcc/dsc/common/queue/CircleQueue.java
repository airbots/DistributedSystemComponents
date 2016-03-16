package edu.unl.hcc.dsc.common.queue;

import java.util.ArrayList;

/**
 * Created by chehe on 16/1/26.
 */

public abstract class CircleQueue {

  ArrayList<Object> elementQueue = new ArrayList<Object>();
  int front = 0;
  int end = 0;

  //put a element into queue, if full throws exception
  public void put(Object element) throws QueueException{

    //boundary condition
    if (element == null) {
      throw new QueueException("Can not put a null element into queue");
    }



  }

  public void put (Object[] elements ) throws QueueException {

    //boundary condition
    if (elements.length < 1) {throw new QueueException("Can not put a null element into queue!");}
    else if ( elements.length == 1) {this.put(elements[0]);}
    else {

    }
  }

  public Object get() throws QueueException {

    if( elementQueue.size() == 0) {return null;}
    else {
      return elementQueue.get(front);
    }
  }

  public Object[] get(int numElement) throws QueueException {

    Object[] results;


  }

  public Object peak() throws QueueException {

  }

  public int size() throws QueueException {
    return elementQueue.size();
  }

}

