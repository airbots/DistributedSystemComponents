package edu.unl.hcc.system.concurrency;

/**
 * Created by chehe on 2017/9/8.
 */

/*
public class BlockingQueueRL {

	Condition isFullCondition;
	Condition isEmptyCondition;
	Lock lock;
  int capacity;
	public BlockingQueueRL() {
		this(Integer.MAX_VALUE);
	}

	public BlockingQueueRL(int limit) {
		this.capacity = limit;
		lock = new ReentrantLock();
		isFullCondition = lock.newCondition();
		isEmptyCondition = lock.newCondition();
	}

	public void put (T t) {
		lock.lock();
		try {
			while (isFull()) {
				try {
					isFullCondition.await();
				} catch (InterruptedException ex) {}
			}
			q.add(t);
			isEmptyCondition.signalAll();
		} finally {
			lock.unlock();
		}
	}

	public T get() {
		T t = null;
		lock.lock();
		try {
			while (isEmpty()) {
				try {
					isEmptyCondition.await();
				} catch (InterruptedException ex) {}
			}
			t = q.poll();
			isFullCondition.signalAll();
		} finally {
			lock.unlock();
		}
		return t;
	}
}


*/