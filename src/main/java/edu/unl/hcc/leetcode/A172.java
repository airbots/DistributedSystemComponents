package edu.unl.hcc.leetcode;

public class A172 {

    /*

    Given an integer n, return the number of trailing zeroes in n!.
    说白了就是找到给定的n里面有多少个5
     */
    public int trailingZeroes(int n) {
        if(n<=4||n>Integer.MAX_VALUE) return 0;
        int r=0;
        while(n>0) {
            n/=5;
            r+=n;
        }
        return r;
    }
}
