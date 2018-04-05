package edu.unl.hcc.leetcode;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import static edu.unl.hcc.leetcode.DoubleLinkedList.*;

public class A716MaxStack {

    TreeMap<Integer, List<DoubleLinkedList.Node>> map;
    DoubleLinkedList dll;

    public A716MaxStack() {
        map = new TreeMap();
        dll = new DoubleLinkedList();
    }

    public void push(int x) {
        DoubleLinkedList.Node node = dll.add(x);
        if(!map.containsKey(x))
            map.put(x, new ArrayList<DoubleLinkedList.Node>());
        map.get(x).add(node);
    }

    public int pop() {
        int val = dll.pop();
        List<DoubleLinkedList.Node> L = map.get(val);
        L.remove(L.size() - 1);
        if (L.isEmpty()) map.remove(val);
        return val;
    }

    public int top() {
        return dll.peek();
    }

    public int peekMax() {
        return map.lastKey();
    }

    public int popMax() {
        int max = peekMax();
        List<DoubleLinkedList.Node> L = map.get(max);
        DoubleLinkedList.Node node = L.remove(L.size() - 1);
        dll.unlink(node);
        if (L.isEmpty()) map.remove(max);
        return max;
    }
}
