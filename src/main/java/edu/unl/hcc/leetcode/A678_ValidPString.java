package edu.unl.hcc.leetcode;

public class A678_ValidPString {

    public boolean checkValidString(String s) {
        //boundary condition
        if(s==null) return false;
        if(s.length()==1) {
            if(s.equals("*")) return true;
            else return false;
        }
        //helper function that recursively execute till end of string
        return match(s.toCharArray(), 0, 0);
    }

    private boolean match(char[] str, int position, int sum) {
        if(sum<0) return false;
        if(position==str.length){
            if(sum==0) return true;
            else return false;
        } else if(position<= str.length-1) {
            if(str[position]=='(') {
                return match(str, position+1,sum+1);
            } else if(str[position]==')') {
                return match(str, position+1,sum-1);
            } else if(str[position]=='*') {
                return match(str,position+1,sum)
                        ||match(str,position+1,sum+1)||match(str,position+1,sum-1);
            } else return false;
        } else return false;
    }

    public boolean checkValidString2(String s) {
        int lo = 0;
        int hi = 0;
        for (char c : s.toCharArray()) {
            //所有的*都看作负值
            lo += c == '(' ? 1 : -1;
            //所有的*都看作正值，避免）出现在左边
            hi += c == ')' ? -1 : 1;
            //检查"）"不能出现在前面，因为会导致hi为负值
            if (hi < 0) return false;
            //对*做处理，看能否match左括号
            lo = Math.max(lo, 0);
        }
        return lo == 0;
    }
}
