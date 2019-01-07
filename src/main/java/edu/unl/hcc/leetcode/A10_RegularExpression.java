package edu.unl.hcc.leetcode;

public class A10_RegularExpression {
        public boolean isMatch(String s, String p) {
            if(p.isEmpty()) return s.isEmpty();
            //这个特殊情况可能会增加时间复杂度
            //if(p.contains(".*")) return true;

            //match first char in normal case
            boolean firstMatch=false;
            if(!s.isEmpty()) {
                if(s.charAt(0)==p.charAt(0)||p.charAt(0)=='.') firstMatch=true;
            }
            //处理有*的两种情况
            if(p.length()>=2&&p.charAt(1)=='*') {
                       //把当前*当作空字符
                return isMatch(s,p.substring(2))
                        //把当前字符作为1次到多次出现的字符
                        || (firstMatch&&isMatch(s.substring(1),p));
            } else {
                       //正常的字符一对一match
                return firstMatch&&isMatch(s.substring(1),p.substring(1));
            }
        }
}
