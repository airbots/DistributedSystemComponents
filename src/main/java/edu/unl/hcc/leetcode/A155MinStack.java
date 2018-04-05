package edu.unl.hcc.leetcode;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class A155MinStack {

        Stack<Integer> stack;
        Stack<Integer> minStorage;
        /** initialize your data structure here. */
        public A155MinStack() {
            stack = new Stack();
            minStorage = new Stack();
        }

        public void push(int x) {
            if(stack.size()==0) {
                minStorage.push(x);
            } else {
                if(x <= minStorage.peek()) minStorage.push(x);
            }
            stack.push(x);
        }

        public void pop() {
            int topValue = stack.pop();
            if(topValue == minStorage.peek()) minStorage.pop();
        }

        public int top() {
            if(stack.size()!=0) return stack.peek();
            else return Integer.MAX_VALUE;
        }

        public int getMin() {
            if(minStorage.size()!=0) return minStorage.peek();
            else return Integer.MAX_VALUE;
        }
    }

/**
 * Your MinStack object will be instantiated and called as such:
 * MinStack obj = new MinStack();
 * obj.push(x);
 * obj.pop();
 * int param_3 = obj.top();
 * int param_4 = obj.getMin();
 */

