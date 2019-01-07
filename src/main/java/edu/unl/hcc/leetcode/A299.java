package edu.unl.hcc.leetcode;

public class A299 {
    public String getHint(String secret, String guess) {
        if(secret==null||guess==null) return "0A0B";
        int[] sa= new int[10];
        int[] ga= new int[10];
        int bull=0;
        int cow=0;
        int len = secret.length()>guess.length()?guess.length():secret.length();
        for(int i=0;i<len;i++) {
            if(secret.charAt(i)==guess.charAt(i)) {
                bull++;
            } else {
                sa[secret.charAt(i)-'0']++;
                ga[guess.charAt(i)-'0']++;
            }
        }
        for(int i =0;i<10;i++){
            cow+=Math.min(sa[i],ga[i]);
        }
        return bull+"A"+cow+"B";
    }
}
