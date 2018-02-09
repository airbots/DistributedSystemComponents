package edu.unl.hcc.leetcode;

import java.util.LinkedList;
import java.util.List;

public class A113 {

    List<List<Integer>> result = new LinkedList();
    public List<List<Integer>> pathSum(TreeNode root, int sum) {
        if(root==null) return result;

        if(isLeaf(root)&&root.val==sum) {
            List<Integer> member = new LinkedList();
            member.add(root.val);
            result.add(member);
        } else {
            LinkedList<Integer> path = new LinkedList();
            helper(root, sum, path);
        }
        return result;
    }

    void helper(TreeNode node, int sum, LinkedList<Integer> path){
        if(node==null)return;

        //必须要实现添加
        path.add(node.val);
        if(isLeaf(node)&&node.val==sum) {
            //这里必须要新建一个list，否则原来的会随着遍历被清空！
            result.add(new LinkedList(path));
            path.removeLast();
            return;
        }

        helper(node.left, sum-node.val,path);
        helper(node.right, sum-node.val, path);
        //必须要拿掉最后一个，否则会导致不一样的结果，因为object是地址传递，不是值传递
        path.removeLast();
    }

    boolean isLeaf(TreeNode root){
        if(root==null)return false;
        else if(root.left==null&&root.right==null) return true;
        else return false;
    }


}
