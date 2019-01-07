//package edu.unl.hcc.system.interview.concurrency;
//
///**
// * Created by chehe on 2017/9/8.
// */
//public class Consumer implements Runnable{
//
//	BlockingQueueMT queue;
//	public Status state;
//
//	public Consumer(BlockingQueueMT queue){
//		this.queue = queue;
//		state = Status.INIT;
//	}
//
//	public void stop(){
//		state = Status.STOP;
//	}
//
//	public void run(){
//
//		while(state != Status.STOP){
//			if(queue.isEmpty()) {
//				try {
//					Thread.sleep(1000);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//			} else {
//				//dequeue something and do ops on it
//			}
//		}
//	}
//}
