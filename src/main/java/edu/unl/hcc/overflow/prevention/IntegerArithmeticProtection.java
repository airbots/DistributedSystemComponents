package edu.unl.hcc.overflow.prevention;

public class IntegerArithmeticProtection {

    public double distance(int x1, int y1, int x2, int y2) {
        double x=(double)x1;
        x-=(double)x2;
        double y=(double)y1;
        y-=(double)y2;

        return Math.sqrt(Math.pow(x,2)+Math.pow(y,2));
    }

    public long arithmetic(int x, int y){
        long x1=(long)x;
        x1+=(long)y;
        return x1;
    }

    public static void main(String[] args) {
       IntegerArithmeticProtection iap = new IntegerArithmeticProtection();
        System.out.println("overflow distance: "+
               iap.distance(Integer.MAX_VALUE, 0, Integer.MIN_VALUE, 0));
        System.out.println("overflow distance: "+
                iap.distance(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE));
    }
}
