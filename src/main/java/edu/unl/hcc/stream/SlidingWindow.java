package edu.unl.hcc.stream;

import java.util.LinkedList;

/**
 * Created by chehe on 2017/6/27.
 */
public class SlidingWindow {

    LinkedList queue;
    int windowSize;
    int counter;
    int currentSum;

    public SlidingWindow(){
        this.counter = 0;
    }

    public void setWindowSize(int size) throws Exception {
        if(size>=0) {
            this.windowSize = size;
        }
        else {
            throw new Exception("Can not create a window with negative size");
        }
    }

    public double average(int nextValue){
        //queue is not full
        if(queue.size() < this.windowSize){
            if(!queue.add(nextValue)){
                System.out.println("Error adding element to queue");
            }
            this.counter++;

        }

        //queue is full
        if(queue.size() == this.counter){

        }

			return 0;
		}

    public static void main(String[] args){

    }
}
