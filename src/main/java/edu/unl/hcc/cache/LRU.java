package edu.unl.hcc.cache;

import java.util.HashMap;

/**
 * Created by chehe on 2017/8/31.
 */
public class LRU<T> {

	int capacity;
	HashMap<Integer, Node> cache = new HashMap();
	class Node {
		T content;
		Node pre=null;
		Node next=null;

		public Node(T obj){
			if (obj != null) content=obj;
		}
	}

	public LRU(int size) {
		if (size > 0) capacity = size;
		else throw new IllegalArgumentException("Cache size can not be smaller than or equal to 0");
	}

	public T get(){
        return null;
	}

	private void setHead(Node n){}

	public void set(){}
}
