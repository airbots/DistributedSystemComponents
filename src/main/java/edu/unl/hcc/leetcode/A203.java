package edu.unl.hcc.leetcode;

public class A203 {
    public ListNode removeElements(ListNode head, int val) {
        ListNode fakeHead=new ListNode(1);
        ListNode root=head;
        ListNode pre=fakeHead;
        fakeHead.next=head;
        while(root!=null){
            if(root.val==val) {
                ListNode tmp =root.next;
                pre.next=tmp;
                root.next=null;
                root=tmp;
            }
            else {
                pre=root;
                root=root.next;
            }
        }
        return fakeHead.next;
    }
}
