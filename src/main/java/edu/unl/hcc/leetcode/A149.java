package edu.unl.hcc.leetcode;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chehe on 2017/9/14.
 */
public class A149 {
	class Point {
		int x;
		int y;
		Point() { x = 0; y = 0; }
		Point(int a, int b) { x = a; y = b; }
	}
	class Solution {
		public int maxPoints(Point[] points) {
			if(points == null) return 0;
			if(points.length <=2) return points.length;

			double slope;
			int result=0;
			//对于某个点而言，水平线上的点
			int horizentalLine;
			//对于某个点而言，跟它重复的点
			int identicalPoint;
			//针对某一个点而言
			for(int i=0;i<points.length;i++){
				//map是针对某个点的，因为斜率一样的，可能有平行线但是不过点
				Map<Double, Integer> map = new HashMap();
				horizentalLine=1;
				//相同的点不包含自己
				identicalPoint=0;
				//遍历其他所有的点，所有俩俩点
				for(int j=i+1;j<points.length;j++){
					if(points[i].x == points[j].x && points[i].y == points[j].y) identicalPoint++;
					else if(points[i].x == points[j].x) {
						//不再考虑该点
						horizentalLine++;
						continue;
					}
					slope = (double)(points[j].y-points[i].y)/(double)(points[j].x-points[i].x);

					if (map.containsKey(slope)){
						map.put(slope, map.get(slope)+1);
					} else map.put(slope,2);
					//相同的点，是针对某一个点而言的
					result = Math.max(result, map.get(slope)+identicalPoint);
				}
				result = Math.max(result, horizentalLine);
			}
			return result;
		}
	}
}
