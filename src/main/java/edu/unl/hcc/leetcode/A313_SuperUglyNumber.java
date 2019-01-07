package edu.unl.hcc.leetcode;

import java.util.Arrays;

public class A313_SuperUglyNumber {

    public int nthSuperUglyNumber(int n, int[] primes) {
        if(n<=0||null==primes||primes.length==0) return 0;
        int[] factors=new int[primes.length];
        for(int i=0;i<primes.length;i++) {
            factors[i]=primes[i];
        }
        int[] indies=new int[primes.length];
        int[] ugly=new int[n];
        ugly[0]=1;
        for(int i=1;i<n;i++){
            int min=Integer.MAX_VALUE;
            int min_index=0;
            for(int j=0;j<primes.length;j++) {
                if(min>factors[j]) {
                    min=factors[j];
                    min_index=j;
                }
            }
            ugly[i]=min;
            factors[min_index]=primes[min_index]*ugly[++indies[min_index]];
        }
        return ugly[n-1];
    }
}
