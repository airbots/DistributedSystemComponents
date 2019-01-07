package edu.unl.hcc.leetcode;

import java.util.Arrays;

public class A16_3sumClosest {
    public int threeSumClosest(int[] nums, int target) {
        if(nums==null||nums.length<3) return target;
        int result = nums[0] + nums[1] + nums[nums.length - 1];
        Arrays.sort(nums);
        for(int i=0;i<nums.length-2;i++){
            int left=i+1, right=nums.length-1;
            if(i==0||(i>0&&nums[i]!=nums[i-1])) {
                while (left < right) {
                    int tmp = nums[i] + nums[left] + nums[right];
                    if (target>tmp) {
                        while(left<right&&(nums[left]==nums[left+1])) left++;
                        left++;
                    //这里如果加上if可能导致超时，循环内的if需要非常小心
                    } else {
                        while(left<right&&(nums[right]==nums[right-1])) right--;
                        right--;
                    }
                    if(Math.abs(tmp-target)<Math.abs(result-target)) result=tmp;
                }
            }
        }
        return result;
    }


}
