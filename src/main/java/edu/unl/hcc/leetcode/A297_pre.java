package edu.unl.hcc.leetcode;

import java.util.Arrays;
import java.util.LinkedList;

class TreeNode {
      int val;
      TreeNode left;
      TreeNode right;
      TreeNode(int x) { val = x; }
 }

 //Pre order traverse is easy since we can do the same thing when we deserilize it.
//but can not use index for array since integer is value pass not object pass, using a list is a good choice

class A297 {
        StringBuilder sb = new StringBuilder();

        // Encodes a tree to a single string.
        public String serialize(TreeNode root) {
            if(root==null)return sb.append("null,").toString();

            preOrder(root);
            return sb.toString();
        }

        void preOrder(TreeNode root){
            if(root==null) {
                sb.append("null,");
                return;
            }
            sb.append(root.val+",");
            preOrder(root.left);
            preOrder(root.right);
        }

        // Decodes your encoded data to tree.
        public TreeNode deserialize(String data) {
            if(data==null) return null;
            LinkedList<String> nodes = new LinkedList(Arrays.asList(data.split(",")));
            return preOrder(nodes);
        }

        TreeNode preOrder(LinkedList<String> nodes){
            String val = nodes.poll();
            if(val.equals("null")) return null;

            TreeNode root = new TreeNode(Integer.parseInt(val));
            root.left=preOrder(nodes);
            root.right=preOrder(nodes);
            return root;
        }
    }

