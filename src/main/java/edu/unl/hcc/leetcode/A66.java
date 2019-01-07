package edu.unl.hcc.leetcode;

public class A66 {
    public int[] plusOne(int[] digits) {
        if(digits==null) return null;
        if(digits[digits.length-1]<9) {
            digits[digits.length-1]++;
            return digits;
        }
        return helper(digits, digits.length-1,1);
    }

    private int[] helper(int[] nums, int position, int carry) {
        if(position<0) return nums;
        //deal with last digit
        int sum=nums[position]+carry;
        nums[position]=sum%10;
        if(sum<10) return nums;
        else {
            if(position==0) {
                //create new array;
                int[] result = new int[nums.length+1];
                System.arraycopy(nums, 0, result, 1, nums.length);
                result[0]=sum/10;
                return result;
            } else {
                return helper(nums,position-1,1);
            }
        }
    }

    public ListNode plusOne(ListNode root) {
        if(root==null) return new ListNode(1);
        root=reverseList(root);
        if(root.val<9) {
            root.val++;
            return root;
        }
        ListNode current = root;
        int sum=0;
        while(current.val+1>10&&current.next!=null) {
            sum = current.val+1;
            current.val=sum%10;
            current=current.next;
        }
        if(current==null&& sum>10){
            root=reverseList(root);
            ListNode newRoot=new ListNode(1);
            newRoot.next=root;
            return newRoot;
        }
        return reverseList(root);
    }

    private ListNode reverseList(ListNode root) {
        ListNode pre = null;
        while(root!=null) {
            ListNode tmp = root.next;
            root.next = pre;
            pre = root;
            root = tmp;
        }
        return pre;
    }
}
