package edu.unl.hcc.leetcode;


import java.util.LinkedList;
import java.util.List;

/**
 * 这里有一个clone的问题，没搞懂，不知道什么意思，clone的目的是为了防止，一个root的左右节点是同一个，
 * 但是没理解为什么会出现这种情况。因为做子树的上界和右子树的下界是不重合的。
 *
 * 感觉是说，在递归的过程中，我们遇到了某次递归，就是root节点，左子树的root，右子树的root，都是一样的，那么
 * 会不会导致java认为是同一个类？？？？
 *
 * 关于返回结果，是一个包含了所有树的list，这个list里面全是可能的树的root
 */

public class A95 {

    public List<TreeNode> generateTrees(int n) {
        if(n==0) return new LinkedList<TreeNode>();
        return genTree(1,n);
    }

    private List<TreeNode> genTree(int s, int e){
        List<TreeNode> result = new LinkedList();
        if(s>e){
            /**如果这里不加空, 下面的第41行就在是叶子结点的时候，无法检查右子树，所以这里必须配合
             * 第20行才能保证在n=0和n=其他数字的况下，都通过！！！
             */
            result.add(null);
            return result;
        }
        for(int i=s;i<=e;i++){
            /**
             * 在做题的过程中，多次把左子树的产生范围写错，分别写成（1，s-1), (1,i-1)，如此这般
             * 必然导致debug时间过分冗长！！！面试的时候一定死翘翘！
             */
            List<TreeNode> left=genTree(s, i-1);
            List<TreeNode> right=genTree(i+1,e);
            System.out.println("reach here:"+s +":"+i);
            System.out.println("left size:"+left.size());
            for(TreeNode l:left) {
                for(TreeNode r:right){
                    System.out.println("right size:"+left.size());
                    TreeNode n = new TreeNode(i);
                    n.left=l;
                    n.right=r;
                    result.add(n);
                }
            }
        }
        return result;
    }



}
