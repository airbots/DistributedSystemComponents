package edu.unl.hcc.leetcode;

import java.util.Comparator;
import java.util.TreeMap;

public class TreeMapWithComparator {

    TreeMap<String, Double> tm = new TreeMap(new Comparator() {

        @Override
        public int compare(Object o1, Object o2) {
            return o1.toString().compareTo(o2.toString());
        }
    });
}
