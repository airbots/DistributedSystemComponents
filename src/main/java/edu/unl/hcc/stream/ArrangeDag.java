package edu.unl.hcc.stream;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by chehe on 2017/9/25.
 */
public class ArrangeDag {
	Blueprint blueprint;

	public ArrangeDag(){
		blueprint = new Blueprint();
	}



	protected class Blueprint {
		public HashMap<Integer,Integer[]> bp;

		Blueprint(){
			bp = new HashMap<Integer, Integer[]>();
		}

		protected void setBP(HashMap<Integer, Integer[]> map){
			if(map!=null) bp = map;
		}
	}

	public List createSequence(Blueprint bp){
		List<Integer> result = new LinkedList<Integer>();

		if(bp == null) return result;
		HashSet keys = new HashSet<Integer>();
		HashSet values = new HashSet<Integer>();

		for(int key: bp.bp.keySet()){
			keys.add(key);
			Integer[] val= bp.bp.get(key);
			for(int i: val) values.add(i);
		}

		while(!keys.isEmpty()){
			while(!values.isEmpty()){
				for(Object value: values.toArray()){
					if(!keys.contains(value)){
						result.add((Integer)value);
						keys.remove(value);
						values.remove(value);
					} else {
						boolean dependency=false;
						Integer[] val = bp.bp.get(value);
						for(Integer i: val){
							if(keys.contains(i)){
								dependency=true;
								break;
							}
						}
						if(!dependency){
							result.add((Integer)value);
							keys.remove(value);
							values.remove(value);
						}
					}
				}
			}
			if(values.isEmpty()){
				for(Object value: values.toArray()){
					result.add((Integer)value);
					keys.remove(value);
				}
			}
		}
		return result;
	}
}
