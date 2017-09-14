package edu.unl.hcc.concurrency;

import java.util.LinkedList;
import java.util.List;

public abstract class Queue<T> {
  int capacity;
	List<T> storage;

  public Queue(int capacity){
    if(capacity > 0) this.capacity=capacity;
		storage = new LinkedList<T>();
  }

	public boolean isEmpty(){
		if (storage.isEmpty()) return true;
		return false;
	}

	public boolean isFull(){
		if(storage.size() == capacity) return true;
		return false;
	}

  private void enqueue(T obj) {
  }

  private T dequeue(){
    return null;
  }

  public void put(T obj){}

  public T get(T obj){
		return null;
	}

  public T peak() {
		return null;
	}
}
