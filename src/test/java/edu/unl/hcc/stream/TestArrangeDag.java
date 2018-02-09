package edu.unl.hcc.stream;

import org.junit.Test;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by chehe on 2017/9/25.
 */
public class TestArrangeDag {

	@Test
	public void TestCreateSequence(){
		ArrangeDag ad = new ArrangeDag();

		ad.blueprint.setBP(createDag(5));

		LinkedList<Integer> verify = new LinkedList();
		for(int i=0;i<5;i++) verify.add((Integer)i);
		System.out.println(ad.createSequence(ad.blueprint));
	}

	HashMap<Integer, Integer[]> createDag(int size){
		HashMap<Integer, Integer[]> map = new HashMap();
		if(size<0) return map;

		for(int i=0;i<size;i++){
			if(i+1<size){
				Integer[] val = {i};
				map.put(i+1,val);
			} else {
				Integer[] val = {i-1};
				map.put(i,val);
			}
		}
		return map;
	}
}
