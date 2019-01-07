package edu.unl.hcc.leetcode;

public class A19_removeNodeFromTail {
    public ListNode removeNthFromEnd(ListNode head, int n) {
        if(head==null||(head.next==null&&n==1)) return null;
        ListNode fast=head;
        while(n>0&&fast!=null){
            fast=fast.next;
            n--;
        }
        ListNode slow = head;
        ListNode pre = null;
        while(fast!=null){
            fast=fast.next;
            pre=slow;
            slow=slow.next;
        }
        if(pre==null){
            head=head.next;
        } else {
            pre.next=slow.next;
        }
        return head;
    }
}
