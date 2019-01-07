package edu.unl.hcc.system.concurrency;

/**
 * Created by chehe on 2017/9/8.
 */

/*
public class BlockingQueueMT<E> {

	int capacity;
	int takeIndex;
	int putIndex;
	int count;
	ReentrantLock lock;
	Condition notEmpty;
	Condition notFull;
	private E[] elements;


	public BlockingQueueMT(int capacity) {
		if (capacity >0 && capacity <= Integer.MAX_VALUE){
			lock = new ReentrantLock();
			notEmpty = lock.newCondition();
			notFull = lock.newCondition();
			elements = new E[];
		} else {
			throw new IllegalArgumentException("Capacity of Queue is illegal");
		}

	}

	//Circularly increment i. Since it is increment 1 by 1, set an equal check is enough
	private int inc(int i){
		return i++==elements.length?0:i;
	}

	public void put(E element) throws InterruptedException{
		if(element == null) throw new NullPointerException();
		final E[] items = this.elements;
		this.lock.lockInterruptibly();
		try {
			try{
				while(count == items.length){
					notFull.await();
				}
			} catch (InterruptedException ie){
				notFull.signal(); //proagate to non-interrupted thread
				throw ie;
			}
			insert(element);
		} finally {
			lock.unlock();
		}
	}

	public boolean offer(E element){
		if ( element == null) throw new NullPointerException();
		final E[] elements = this.elements;
		this.lock.lockInterruptibly();
	}
	private void insert(E element){
		elements[putIndex] = element;
		putIndex = inc(putIndex);
		++count;
		notEmpty.signal();
	}

	public T remove(){

	}
}


*/