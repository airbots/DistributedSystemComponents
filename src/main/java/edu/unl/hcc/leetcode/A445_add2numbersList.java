package edu.unl.hcc.leetcode;

import java.util.Stack;

public class A445_add2numbersList {

    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        if(l1==null) return l2;
        else if(l2==null) return l1;
        Stack<Integer> s1= new Stack();
        Stack<Integer> s2 = new Stack();
        while(l2!=null){
            s2.push(l2.val);
            l2=l2.next;
        }
        while(l1!=null){
            s1.push(l1.val);
            l1=l1.next;
        }

        ListNode tmp = new ListNode(0);
        int sum=0;
        while(!s1.isEmpty() ||!s2.isEmpty()) {
            if(!s1.isEmpty()) sum+=s1.pop();
            if(!s2.isEmpty()) sum+=s2.pop();
            tmp.val=sum%10;
            ListNode newNode=new ListNode(sum/10);
            newNode.next=tmp;
            tmp=newNode;
            sum/=10;
        }
        return tmp.val==0?tmp.next:tmp;
    }

    //这其中，reverse linkedlist 要比用stack快的多。而且只有常数空间复杂度。
}
