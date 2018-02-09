package edu.unl.hcc.leetcode;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Created by chehe on 2017/9/15.
 */
public class A103 {
	class TreeNode {
		int val;
		TreeNode left;
		TreeNode right;

		TreeNode(int x) {
			val = x;
		}
	}

	class Solution {
		public List<List<Integer>> zigzagLevelOrder(TreeNode root) {
			Queue<TreeNode> queue = new LinkedList<TreeNode>();
			List<List<Integer>> result = new LinkedList<List<Integer>>();

			if(queue == null) return result;

			//level跟后面的for结合，是用来控制本层操作的
			int level;
			boolean reverse=false;
			queue.offer(root);
			while(!queue.isEmpty()){
				level = queue.size();
				List<Integer> subList = new LinkedList<Integer>();
				//
				for(int i=0;i<level;i++){
					TreeNode node = queue.poll();
					if(node.left!=null)queue.offer(node.left);
					if(node.right!=null)queue.offer(node.right);
					//需要reverse，就从最前面加入新node
					if(reverse)subList.add(0,node.val);
					//不需要reverse，往后面加
					else subList.add(node.val);
				}
				//升级reverse
				reverse = !reverse;
				result.add(subList);
			}
			return result;
		}

	}
}
