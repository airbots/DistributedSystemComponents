package edu.unl.hcc.leetcode;

import java.util.ArrayList;
import java.util.List;

public class A22GenerateParentheses {

    ArrayList<String> re = new ArrayList();
    public List<String> generateParenthesis(int n) {
        helper(0,n,n,"");
        return re;
    }

    void helper(int sum, int left, int right, String path) {
        if(sum == 0 && left == 0 && right == 0) {
            re.add(path);
            return;
        }
        if(sum == 0 && left > 0) {
            helper(sum+1, left-1, right, path+"(");
        } else if(sum > 0) {
            if(left == 0) helper(sum-1,left, right-1,path+")");
            else if(left>0) {
                helper(sum+1,left-1,right, path+"(");
                helper(sum-1,left,right-1,path+")");
            }
        }
    }
}
