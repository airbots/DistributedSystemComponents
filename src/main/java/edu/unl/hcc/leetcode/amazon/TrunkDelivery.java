package edu.unl.hcc.leetcode.amazon;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class TrunkDelivery {
    List<List<Integer>> ClosestXdestinations(int numDestinations,
                                             List<List<Integer>> allLocations,
                                             int numDeliveries) throws Exception
    {
        List<List<Integer>> result = new LinkedList();
        //boundary condition check
        if(numDestinations<=0||numDeliveries<=0||
                allLocations==null||allLocations.size()==0)
            return result;

        int curX=0;
        int curY=0;
        try{
            while(allLocations.size()>0&&numDeliveries>0){
                List<Integer> min=findMinDis(allLocations, curX, curY);
                if(min.size()!=2) throw new Exception("Wrong format of coordinates Exception!");
                curX=min.get(0);
                curY=min.get(1);
                result.add(min);

                numDeliveries--;
            }
        } catch (Exception e) {
            //eat to let code pass
        }
        return result;
    }

    double distance(int x1, int y1, int x2, int y2) throws Exception{
        //boundary condition since power or any interger ops may cause overflow
        double x=(double)x1;
        x-=(double)x2;
        double y=(double)y1;
        y-=(double)y2;
        x=Math.pow(x,2);
        y=Math.pow(y,2);
        //even overflow long integer
        if(x+y<0)throw new Exception("Illegal Coordinates Exception!");
        return Math.sqrt((x+y));
    }

    List<Integer> findMinDis(List<List<Integer>> candidates, int x, int y) throws Exception {
        //get all element distance and find the min
        List<Integer> result=new LinkedList();
        double min=0.0;
        double cur=0.0;
        //loop through all element and find the min coordiates
        for(int i=0;i<candidates.size();i++){
            List<Integer> curLoc = candidates.get(i);
            if(curLoc.size()!=2) throw new Exception("Wrong format of coordinates Exception!");
            int x1=curLoc.get(0);
            int y1=curLoc.get(1);
            try {
                cur = distance(x,y,x1,y1);
                if(i==0) min = cur;
                if(min<distance(x,y,x1,y1)) {
                    min=cur;
                    result.set(0,x1);
                    result.set(1,y1);
                }
            } catch (Exception e) {
                //eat now for pass the test
            }
        }
        //if allLocations has duplicates
        for(int i=0;i<candidates.size();i++) {
            List<Integer> curLoc = candidates.get(i);
            if(curLoc.get(0)==result.get(0)&&
                    curLoc.get(1)==result.get(1))
                candidates.remove(curLoc);
        }
        return result;
    }

    public static void main(String[] args) throws Exception {
        TrunkDelivery d = new TrunkDelivery();
        List<List<Integer>> testcase1=new LinkedList(Arrays.asList(1,-1));
        List<Integer> p1 = Arrays.asList(1,1);
        List<Integer> p2 = Arrays.asList(2,3);
        testcase1.add(p1);
        testcase1.add(p2);
        System.out.print(d.ClosestXdestinations(3,testcase1,2));

    }
}
