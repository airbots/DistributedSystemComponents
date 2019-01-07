package edu.unl.hcc.leetcode;

public class A725_SplitLinkedList {
    public ListNode[] splitListToParts(ListNode root, int k) {
        ListNode[] result = new ListNode[k];
        //boundary conditions;
        if(root==null||k<=0){
            return result;
        }
        //get list length and find remainer
        int len=1;
        ListNode tmp=root;
        while(tmp.next!=null){
            len++;
            tmp=tmp.next;
        }
        int seg=len/k;
        int r=len%k;
        //pre 是涌来切断尾部的，因为root一直指向的是下一个结果链表的开头节点，只能使用pre来做隔断
        ListNode pre=null;
        //create output results
        for(int i=0;i<k;i++,r--) {
            result[i]=root;
            for(int j=0;j<seg+(r>0?1:0);j++){
                pre=root;
                root=root.next;
            }
            pre.next=null;
        }
        return result;
    }
}
