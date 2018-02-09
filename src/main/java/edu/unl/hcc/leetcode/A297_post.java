package edu.unl.hcc.leetcode;


import java.util.Arrays;
import java.util.LinkedList;

public class A297_post {
//This is method that using post order to serilize and deserilize tree.
    //difference is we need to removeLast and find right child first
    StringBuilder sb = new StringBuilder();
    public String serialize(TreeNode root) {
        if(root==null)return sb.append("null,").toString();

        postOrder(root);
        return sb.toString();
    }

    void postOrder(TreeNode root) {
        if(root==null){
            sb.append("null,");
            return;
        }

        postOrder(root.left);
        postOrder(root.right);
        sb.append(root.val+",");
    }


    // Decodes your encoded data to tree.
    public TreeNode deserialize(String data) {
        if(data==null) return null;
        LinkedList<String> nodes = new LinkedList(Arrays.asList(data.split(",")));

        return postOrder(nodes);
    }

    TreeNode postOrder(LinkedList<String> nodes) {
        String val = nodes.removeLast();

        if(val.equals("null")) return null;

        TreeNode root=new TreeNode(Integer.parseInt(val));
        root.right=postOrder(nodes);
        root.left=postOrder(nodes);
        return root;
    }
}

