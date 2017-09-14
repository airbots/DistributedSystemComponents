package edu.unl.hcc.leetcode;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chehe on 2017/9/12.
 */
public class A477 {
	static class Solution {
		public int totalHammingDistance(int[] nums) {
			int total = 0, n = nums.length;
			for (int j=0;j<32;j++) {
				int bitCount = 0;
				for (int i=0;i<n;i++)
					bitCount += (nums[i] >> j) & 1;
				total += bitCount*(n - bitCount);
			}
			return total;
		}
		public int totalHammingDistance2(int[] nums) {
			if(nums == null) throw new NullPointerException();
			Map<Integer, Integer> map = new HashMap();

			for(int i=0;i<nums.length;i++){
				if(map.containsKey(nums[i])) map.put(nums[i],map.get(nums[i])+1);
				else map.put(nums[i],1);
			}
			int result=0;
			int freq =0;
			//将keyset转换为给定类型array的方法。
			Integer[] keyArray = map.keySet().toArray(new Integer[0]);
			for(int i=0;i<keyArray.length;i++){
				freq = map.get(keyArray[i]);
				for(int j=i+1;j<keyArray.length;j++){
					result += freq*hammingDistance(keyArray[i],keyArray[j])*map.get(keyArray[j]);
				}
			}
			return result;
		}

		private int hammingDistance(int x, int y){
			int xor = x^y;
			int count = 0;
			for(int i=0;i<32;i++){
				count+=xor>>i&1;
			}
			return count;
		}
	}

	public static void main(String[] args){
		Solution s = new Solution();
		int[] testcase = {1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3};
		System.out.println(s.totalHammingDistance(testcase));
		System.out.println(s.totalHammingDistance2(testcase));
	}
}
