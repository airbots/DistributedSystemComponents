package edu.unl.hcc.leetcode;

import javax.swing.tree.TreeNode;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Created by chehe on 2017/9/15.
 */
public class A102 {
	class TreeNode {
		     int val;
		     TreeNode left;
		     TreeNode right;
		     TreeNode(int x) { val = x; }
		 }
	class Solution {
		public List<List<Integer>> levelOrder(TreeNode root) {

			Queue<TreeNode> queue = new LinkedList<TreeNode>();
			List<List<Integer>> result = new LinkedList<List<Integer>>();

			if(root == null) return result;

			int level;
			queue.offer(root);
			while(!queue.isEmpty()){
				level = queue.size();
				List<Integer> subList = new LinkedList<Integer>();
				for(int i=0;i<level;i++){
					TreeNode currentNode = queue.poll();
					if(currentNode.left!= null) queue.offer(currentNode.left);
					if(currentNode.right!=null) queue.offer(currentNode.right);
					subList.add(currentNode.val);
				}
				result.add(subList);
			}
			return result;
		}
	}
}
