package edu.unl.hcc.leetcode;

public class A328_OddEvenLinkedList {
    public ListNode oddEvenList(ListNode head) {
        //boundary condition
        if(head==null||head.next==null||head.next.next==null) return head;
        ListNode evenHead=head.next;
        ListNode odd=head,even=head.next;
        ListNode oddCur=odd,evenCur=even;
        while(oddCur!=null&&evenCur!=null&&evenCur.next!=null) {
            oddCur = evenCur.next;
            evenCur = oddCur.next;
            odd.next=oddCur;
            even.next=evenCur;
            odd=odd.next;
            even=even.next;
        }
        odd.next=evenHead;
        return head;
    }
}
