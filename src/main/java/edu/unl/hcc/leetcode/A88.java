package edu.unl.hcc.leetcode;

public class A88 {
    public int removeDuplicates(int[] nums) {
        if(nums==null) return 0;
        int count=0;
        for(int n:nums) {
            if(count<2||n>nums[count-2]) nums[count++]=n;
        }
        return count;
    }
}
