package edu.unl.hcc.service.rrd;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by chehe on 2017/6/7.
 */
public class TestRrdUtils {


    RrdUtils rrdUtils;
    String rrdPath;

    @Before
    public void setup(){
        rrdUtils = new RrdUtils();
        rrdPath = System.getProperty("java.io.tmp");
    }

    @Test
    public void test01CreateRrd(){

    }

    @Test
    public void test02UpdateRrd(){

    }

    @Test
    public void test03FetchRrd(){

    }

    @Test
    public void testScientificNumberToFloat(){
        float case1 = RrdUtils.scientificNumberToFloat("4.2000000000e+002");
        Assert.assertEquals((float)420, case1, 0.0001);

        //float case2 = RrdUtils.scientificNumberToFloat("4.2000000000 e+ 002");
        //Assert.assertEquals((float) 420, case2, 0.0001);

        //float case3 = RrdUtils.scientificNumberToFloat("4.2000000000E+002");
        //Assert.assertFalse((float)420 == case3);
    }
}
