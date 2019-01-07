package edu.unl.hcc.leetcode;

public class A11_ContainerWithMostWater {

        public int maxArea(int[] height) {
            if(height==null||height.length==0) return 0;
            int l=0,r=height.length-1,vol=0;
            while(l<r) {
                vol=Math.max(vol,(r-l)*Math.min(height[l],height[r]));
                //这里是tricky的地方，通过下面运算可以得到只用O（n）的时间，遍历完备选的candidate
                //而不会有遗漏。
                if(height[l]<height[r]) l++;
                else r--;
            }
            return vol;
        }
}
