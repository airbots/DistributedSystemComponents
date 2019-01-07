/*
package edu.unl.hcc.system.interview.concurrency;

*/
/**
 * Created by chehe on 2017/9/8.
 *//*

public class Producer {
	BlockingQueueMT queue;
	int capacity;
	Status state;

	public Producer(BlockingQueueMT q){
		if(q!=null) queue = q;
		state = Status.INIT;
	}

	public void stop(){
		state = Status.STOP;
	}


	public void run() throws InterruptedException {

		while(state != Status.STOP){
			if(queue.isFull()){
				Thread.sleep(5000);
			} else {
				queue.put(new Object());
			}
		}
	}
}
*/
