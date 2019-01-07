package edu.unl.hcc.leetcode;

public class A829 {
    //假设连续数从i开始，一共有k个数字，那么
    // N -（1+2+...k）=i*k
    //2i*k + k(k+1)= 2N => k^2 + (2i+1)k - 2N = 0
    //其中k>0,i>0, 那么k^2 + (2i+1)k = 2N, 既然都是大于0的数字，那么只需要循环到sqrt(k)就可以知道结果
    //根据中学一元二次方程的解法， 可以简化为(k-x)(k+y)=0，其中y-x=2i+1, y*x=2n
    public int consecutiveNumbersSum(int N) {
        int result = 0;
        for(int i=0;i<Math.sqrt((double)N);i++) {
               //找到x必须是整数，而且能被N*2整除，同时y-x-1要是个偶数, 这条件足矣满足等式成立
            if(N*2%i==0 && ((N*2/i-i)-1)%2==0) result++;
        }
        return result;
    }
}
