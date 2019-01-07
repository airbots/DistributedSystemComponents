package edu.unl.hcc.leetcode;

public class A203_RemoveListElement {
    public ListNode removeElements(ListNode head, int val) {
        if(head==null) return head;
        ListNode fakeHead = new ListNode(0);
        ListNode current = head;
        ListNode pre = fakeHead;
        while(current!=null) {
            if(current.val==val){
                pre.next=current.next;
                current=current.next;
            } else {
                pre.next = current;
                pre = pre.next;
                current = current.next;
            }
        }
        return fakeHead.next;
    }

    public ListNode removeElements2(ListNode head, int val) {
        if(head==null) return head;
        head.next=removeElements2(head.next,val);
        return head.val==val?head.next:head;
    }
}
