package edu.unl.hcc.leetcode;

public class A24_SwapNodeInPairs {

    public ListNode swapPairs(ListNode head) {
        if(head==null||head.next==null) return head;
        ListNode slow = head;
        ListNode fast = head.next;
        while(fast!=null){
            int tmp = slow.val;
            slow.val=fast.val;
            fast.val=tmp;
            if(fast.next!=null){
                slow=fast.next;
                if(fast.next.next!=null) {
                    fast=fast.next.next;
                } else break;
            } else break;
        }
        return head;
    }
}
