package edu.unl.hcc.system.cache;

import java.util.HashMap;

/**
 * Created by chehe on 2017/8/31.
 */
public class LRU<T, S> {

	int capacity;
	HashMap<T, Node> cache = new HashMap();
	class Node {
		T key;
		S value;
		Node pre=null;
		Node next=null;

		public Node(T obj, S s){
			if (obj != null&& s!=null) {key=obj; value=s;}
		}
	}
	Node head;
	Node tail;
	int count;

	public LRU(int cap) {
		if (cap > 0) capacity = cap;
		else throw new IllegalArgumentException("Cache size can not be smaller than or equal to 0");
		head = null;
		tail=null;
		count=0;
	}

	public S get(T t){
            if(t==null) return null;
            Node cur = cache.get(t);
            if(cur==null) return null;
            moveToTail(cur);
            return cur.value;
	}

	private void setHead(Node n){}

	public void set(T t, S s){
            if(t==null||s==null) return;
            if(cache.containsKey(t)) {
                cache.get(t).value=s;
                moveToTail(cache.get(t));
            } else {
                Node newNode=new Node(t,s);
                if(head==null) head=newNode;
                if(tail==null) tail=newNode;
                cache.put(t,newNode);
                moveToTail(newNode);
            }
            count++;

            if(count>capacity){
                remove(head.key);
            }

        }

	public int size(){
	    return count;
        }

	public int getCapacity(){
	    return capacity;
        }

        private void moveToTail(Node n) {
	    if(n==null||tail==n) return;
	    if(head==n) {
	        head=n.next;
	        head.pre=null;
            }
	    n.pre=tail;
	    tail.next=n;
	    n.next=null;
	    tail=n;
        }

        public void remove(T t) {
            if(!cache.containsKey(t)) return;
            Node cur = cache.get(t);
            if(head==cur) {
                if(head.next!=null){
                    head=cur.next;
                    cur.next=null;
                    head.pre=null;
                } else {
                    head=null;
                    tail=null;
                }
            } else if (tail==cur){
                cur.pre.next=null;
            } else {
                cur.pre.next=cur.next;
                cur.next.pre=cur.pre;
            }
            cache.remove(t);
            count--;
        }


        /* leetcode passed result

        class Node{
    int key;
    int value;
    Node pre;
    Node next;

    public Node(int key, int value){
        this.key = key;
        this.value = value;
    }
}

public class LRUCache {
    int capacity;
    HashMap<Integer, Node> map = new HashMap<Integer, Node>();
    Node head=null;
    Node end=null;

    public LRUCache(int capacity) {
        this.capacity = capacity;
    }

    public int get(int key) {
        if(map.containsKey(key)){
            Node n = map.get(key);
            remove(n);
            setHead(n);
            return n.value;
        }

        return -1;
    }

    public void remove(Node n){
        if(n.pre!=null){
            n.pre.next = n.next;
        }else{
            head = n.next;
        }

        if(n.next!=null){
            n.next.pre = n.pre;
        }else{
            end = n.pre;
        }

    }

    public void setHead(Node n){
        n.next = head;
        n.pre = null;

        if(head!=null)
            head.pre = n;

        head = n;

        if(end ==null)
            end = head;
    }

    public void set(int key, int value) {
        if(map.containsKey(key)){
            Node old = map.get(key);
            old.value = value;
            remove(old);
            setHead(old);
        }else{
            Node created = new Node(key, value);
            if(map.size()>=capacity){
                map.remove(end.key);
                remove(end);
            }
            setHead(created);
            map.put(key, created);
        }
    }
}



         */

}
