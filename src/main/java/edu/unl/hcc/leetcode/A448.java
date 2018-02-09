package edu.unl.hcc.leetcode;

import java.util.LinkedList;
import java.util.List;

public class A448 {

    public List<Integer> findDisappearedNumbers(int[] nums) {
        List<Integer> result = new LinkedList();
        if(nums==null) return result;

        for(int i=0;i<nums.length;i++) {
            //找到第i个位置上放置的整数值，因为都是小于数组长度且大于0️的整数，所以
            //把对应的整数值作为index，并把index对应的value变为负数，这样起到标示的作用
            //那么第二个循环，
            int val = nums[Math.abs(nums[i])-1];
            if (val > 0) {
                nums[Math.abs(nums[i])-1]=-val;
            }
        }

        for(int i=0;i<nums.length;i++) {
            if(nums[i]>0) result.add(i+1);
        }

        return result;
    }
}
