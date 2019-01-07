package edu.unl.hcc.leetcode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class A15_3sum {

    public List<List<Integer>> threeSum(int[] nums) {

        List<List<Integer>> result = new ArrayList();

        if(nums==null||nums.length<3) return result;
        Arrays.sort(nums);
        for(int i=0;i<nums.length-2;i++) {
            //如果nums里的元素有重复，这个if的第二个条件组合就排除了相应的结果
            if(i==0||(i>0&&nums[i]!=nums[i-1])) {
                int left=i+1,right=nums.length-1,target=-nums[i];
                while(left<right) {
                    if(nums[left]+nums[right]==target){
                        result.add(new ArrayList(Arrays.asList(nums[i],nums[left],nums[right])));
                        //需要排除重复的元素因为已经sort过
                        while(left<right&&nums[left]==nums[left+1])left++;
                        while(right>left&&nums[right]==nums[right-1])right--;
                        //这个操作必须在两个while之后，如果在之前可能丢结果，保证挪到了不同的元素上！！！
                        left++;
                        right--;
                    } else if(nums[left]+nums[right]<target)left++;
                    else right--;
                }
            }
        }
        return result;
    }

    /*



     */
}
