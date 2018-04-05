package edu.unl.hcc.leetcode;

public class DoubleLinkedList {

    Node head, tail;

    public DoubleLinkedList(int v) {
        head = new Node(v);
        head.next = null;
        head.pre = null;
        tail = head;
    }

    public DoubleLinkedList() {
        head = null;
        tail = null;
    }

    public Node add(int val) {
        Node node = new Node(val);
        if(head == null) {
            head=node;
            head.next = null;
            head.pre = null;
            tail = head;
        } else {
            tail.next = node;
            node.pre = tail;
            node.next = null;
        }
        return node;
    }

    public int size() {
        if(head == null) return 0;
        int length=1;
        Node current = head;
        while(current!=tail) {
            length++;
            current=current.next;
        }
        return length;
    }

    public void addFirst(int val) {
        Node node = new Node(val);
        if(head == null) {
            head = node;
            head.pre = null;
            head.next = null;
            tail = head;
            return;
        }
        node.next = head;
        head.pre = node;
        head = node;
    }

    public void removeTail() {
        if(tail == null) return;
        tail = tail.pre;
        if(tail != null) tail.next = null;
        else head = null;
    }

    public void removeFirst() {
        if(head==null) return;
        head = head.next;
        if(head != null) head.pre = null;
        else tail = null;
    }

    public void push(int val) {
        addFirst(val);
    }

    public int pop(){
        int value = head.value;
        removeFirst();
        return value;
    }

    public int peek() {
        return head.value;
    }

    public void unlink(Node node) {
        if(node == null) return;
        if(node == tail && node == head) {
            head = null;
            tail = null;
        } else if(node == tail ) {
            tail = tail.pre;
        } else if(node == head ) {
            head = head.next;
        } else {
            node.pre.next = node.next;
            node.next.pre = node.pre;
        }
    }

    class Node {
        int value;
        Node pre, next;
        public Node(int v) {value = v;}
    }
}
