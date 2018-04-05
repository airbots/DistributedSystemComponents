package edu.unl.hcc.leetcode;

public class A61_RotateList {

    public Node rotateRight(Node head, int k) {
        if(head == null || head.next==null || k<1) return head;
        Node fast=head;
        ListNode slow=head;

        int l=1;
        while(fast.next!=null) {
            fast = fast.next;
            l++;
        }
        for(int i=1;i<l-k%l;i++){
            slow=slow.next;
            System.out.println(slow.val);
            System.out.println(fast.val);
        }

        fast.next=head;
        head = slow.next;
        slow.next=null;
        return head;
    }
}
