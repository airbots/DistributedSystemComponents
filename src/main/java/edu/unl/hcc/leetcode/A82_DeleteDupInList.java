package edu.unl.hcc.leetcode;

public class A82_DeleteDupInList {
    public ListNode deleteDuplicates(ListNode head) {
        //boundary condition
        if (head == null || head.next == null) return head;
        ListNode preHead = new ListNode(Integer.MAX_VALUE);
        preHead.next = head;
        ListNode pre = preHead, cur = head;
        while (cur != null) {
            //pass all dup
            while (cur.next != null && cur.val == cur.next.val) {
                cur = cur.next;
            }
            //if no dup
            if (pre.next == cur && pre.val != cur.val) {
                pre = pre.next;
            } else {
                pre.next = cur.next;
            }
            cur = cur.next;
        }
        return preHead.next;
    }
}
