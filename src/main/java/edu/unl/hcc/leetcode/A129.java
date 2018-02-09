package edu.unl.hcc.leetcode;

public class A129 {
    //对应root是高数位的情况
    public int sumNumbers(TreeNode root) {
        if(root==null) return 0;
        return sum(root, 0);
    }
    int sum(TreeNode node, int x) {
        //主要是对付左子树或是右子树唯恐的节点
        if(node==null) return 0;
        //对付叶子结点，如果不加，则无法累计计算，因为叶子是终点各位
        if(node.left==null&&node.right==null) return 10*x+node.val;
        //一般节点情况
        return sum(node.left, 10*x+node.val) + sum(node.right, 10*x+node.val);
    }

    //对应root是低位的情况
    public int sumNumbers2(TreeNode root){
        if(root==null) return 0;
        return sum2(root, 0, 0);
    }
    int sum2(TreeNode node, int x, int dep){
        if(node==null) return 0;
        if(node.left==null&&node.right==null) return node.val*(int)Math.pow(10,dep)+x;
        return sum2(node.left, node.val*(int)Math.pow(10,dep)+x,dep+1)+
                sum2(node.right, node.val*(int)Math.pow(10,dep)+x, dep+1);
    }

}
