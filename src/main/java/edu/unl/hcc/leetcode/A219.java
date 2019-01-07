package edu.unl.hcc.leetcode;

import java.util.HashSet;

public class A219 {
    public boolean containsNearbyDuplicate(int[] nums, int k) {
        if(k==0||nums==null)return false;
        HashSet<Integer> set = new HashSet();
        for(int i=0;i<nums.length;i++) {
            if(i>k)set.remove(nums[i-k-1]);
            if(!set.add(nums[i])) return true;
        }
        return false;
    }
}
