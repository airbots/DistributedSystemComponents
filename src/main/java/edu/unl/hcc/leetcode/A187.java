package edu.unl.hcc.leetcode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class A187 {
    public List<String> findRepeatedDnaSequencesOptimal(String s) {
        List<String> res = new ArrayList<>();
        if (s == null || s.length() < 10) {
            return res;
        }
        //对字母进行编码
        char[] map = new char[256];
        map['A'] = 0;
        map['C'] = 1;
        map['G'] = 2;
        map['T'] = 3;
        int mask = 0xfffff;//20bit,10个字母，每个字母占2bit
        int val = 0;
        char[] schar = s.toCharArray();
        for (int i = 0; i < 9; i++) {//对前9位进行编码
            val = (val << 2) | (map[schar[i]] & 3);
        }
        byte[] bytes = new byte[1 << 20];
        for (int i = 9; i < schar.length; i++) {
            val = ((val << 2) & mask) | ((map[schar[i]]) & 3);//编码
            if (bytes[val] == 1) {
                res.add(String.valueOf(schar, i - 9, 10));
            }
            if (bytes[val] < 2) {
                bytes[val]++;
            }
        }
        return res;
    }

    public List<String> findRepeatedDnaSequences(String s) {
        List<String> result = new LinkedList();
        if(s==null||s.length()<=10)return result;

        HashMap<String,Integer> map = new HashMap();
        for(int i=0;i<=s.length()-10;i++) {
            String str=s.substring(i,i+10);
            if(map.containsKey(str)) {
                if(map.get(str)==1)result.add(str);
                map.put(str, map.get(str)+1);
            } else map.put(str,1);
        }
        return result;
    }
}
