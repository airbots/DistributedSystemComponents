package edu.unl.hcc.leetcode;

/**
 * Created by chehe on 2017/9/14.
 */
public class A48 {
	class Solution {
		public void rotate(int[][] matrix) {
			if(matrix==null || matrix.length<1 || matrix[0].length<1) return;
			transpose(matrix);
			reverse(matrix);
		}

		void transpose(int[][] matrix){
			for(int i=0;i<matrix.length;i++){
				//只换对角线，否则就相当于没做
				for(int j=i+1;j<matrix.length;j++){
					int tmp = matrix[i][j];
					matrix[i][j]=matrix[j][i];
					matrix[j][i]=tmp;
				}
			}
		}

		void reverse(int[][] matrix){
			for(int i=0;i<matrix.length;i++){
				//只换前半部分，否则也相当于没做
				for(int j=0;j<matrix.length/2;j++){
					int tmp = matrix[i][j];
					matrix[i][j]=matrix[i][matrix.length-j-1];
					matrix[i][matrix.length-j-1]=tmp;
				}
			}
		}
	}
}
