package edu.unl.hcc.leetcode.priorityqueue;

public class A88 {


    public void merge(int[] nums1, int m, int[] nums2, int n) {
        while(n>0) nums1[m+n-1]=(m==0||nums2[n-1]>nums1[m-1])?nums2[n--]:nums1[m--];
    }

    public static void main(String[] args){
        A88 s = new A88();
        int[] nums1={2,4,5,9,10,30,0,0,0,0,0,0};
        int[] nums2={1,4,8,11,12,32};
        s.merge(nums1,6, nums2, 6);
        System.out.println(nums1);
    }

}
