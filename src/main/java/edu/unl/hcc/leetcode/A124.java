package edu.unl.hcc.leetcode;

public class A124 {
    int max=Integer.MIN_VALUE;
    public int maxPathSum(TreeNode root) {
        if(root==null) return 0;
        if(root.left==null&&root.right==null) return root.val;

        helper(root);
        return max;
    }

    int helper(TreeNode root){
        if(root==null) return 0;

        int left=Math.max(0,helper(root.left));
        int right=Math.max(0,helper(root.right));
        max = Math.max(max, root.val+left+right);
        return Math.max(left, right)+root.val;
    }
}
