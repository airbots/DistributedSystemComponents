package edu.unl.hcc.leetcode;

/**
 * Leetcode 把二叉树转换为双链表
 */


public class A114_1 {

    private TreeNode pre = null;

    public void flatTreeToDoubleLL(TreeNode root) {
        if(root==null) return;

        TreeNode left = root.left;
        TreeNode right = root.right;

        root.left = pre;

        pre = root;

        flatTreeToDoubleLL(root.left);
        flatTreeToDoubleLL(root.right);

        root.right = left;


    }


}
