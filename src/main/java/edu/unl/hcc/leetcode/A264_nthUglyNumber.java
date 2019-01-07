package edu.unl.hcc.leetcode;

import java.util.Arrays;

public class A264_nthUglyNumber {


    //Space O(1), Time O(n)
    public int nthUglyNumber(int n) {
        if(n<0) return 0;
        if(n<=6&&n>-1) return n;
        int steps=n-6;
        int initial=6;
        while(steps>0) {
            initial++;
            if(A263_uglyNumber.uglyNumber(initial)) steps--;
            if(steps==0) return initial;
        }
        return 0;
    }


    //Space O(n), Time O(n)
    public int nthUglyNumber2(int num) {
        if(num<=0) return 0;
        int[] ugly=new int[num];
        ugly[0]=1;
        int factor2=2, factor3=3, factor5=5;
        int times2=0, times3=0, times5=0;
        for(int i=1;i<num;i++) {
            int min = Math.min(Math.min(factor2,factor3),factor5);
            ugly[i]=min;
            if(min==factor2)
                factor2=2*ugly[(++times2)];
            if(min==factor3)
                factor3=3*ugly[(++times3)];
            if(min==factor5)
                factor5=5*ugly[(++times5)];

        }
        return ugly[num-1];
    }
}
