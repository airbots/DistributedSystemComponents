package edu.unl.hcc.leetcode;

public class A92_ReverseLinkedListII {

    public ListNode reverseBetween(ListNode head, int m, int n) {
        if(head==null||head.next==null||m==n) return head;

        //find position m and n as head and tail
        ListNode start,next,cur;
        ListNode dummyHead=new ListNode(-1);
        dummyHead.next=head;
        ListNode lockStart=dummyHead;
        for(int i=1;i<m;i++) lockStart=lockStart.next;
        start=lockStart;
        cur=start.next;
        next=cur;
        //need to process "=" case !!!
        while(m<=n){
            //find start and then do the reverse
            next=cur.next;
            cur.next=start;
            start=cur;
            cur=next;
            n--;
        }
        //need to change downstream link first then upstream link!!!
        lockStart.next.next=cur;
        lockStart.next=start;
        //can not use head since it may reverse from head, then head becomes tail!
        return dummyHead.next;
    }
}
