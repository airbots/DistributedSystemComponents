package edu.unl.hcc.leetcode;

public class A263_uglyNumber {

    public static boolean uglyNumber(int num) {
        if(num==0) return true;
        while(num%3==0) num/=3;
        while(num%2==0) num/=2;
        while(num%5==0) num/=5;
        if(num==1) return true;
        return false;
    }
}
