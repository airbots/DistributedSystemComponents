package edu.unl.hcc.leetcode;

/**
 * Created by chehe on 2017/9/18.
 */
public class A28 {
	class Solution {
		public int strStr(String haystack, String needle) {
			//这里只所以敢用无限循环，是因为string的长度，最长就是integer的大小，
			//因为string有stirng.length()方法，java为了让系统健康故而加入该条件
			for(int i=0;i<Integer.MAX_VALUE;i++){
				for(int j=0;j<Integer.MAX_VALUE;j++){
					//这一句必须放在第一个检测，否则可能导致下一个检测出错
					//用来检查是否满足条件
					if(j==needle.length()) return i;
					//如果长度超过了目标haystack，那么就不需要进行下去了。
					if(i+j==haystack.length()) return -1;
					//如何有不同，跳出本次循环，i来控制从haystack启动的位置
					if(needle.charAt(j)!=haystack.charAt(i+j)) break;
				}
			}
            return 0;
        }
	}
}
