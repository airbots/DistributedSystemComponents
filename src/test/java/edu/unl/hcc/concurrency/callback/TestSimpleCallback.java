package edu.unl.hcc.concurrency.callback;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class TestSimpleCallback {

    SimpleCallback scb;

    @Before
    public void setup(){
        scb = new SimpleCallback();
    }

    @Test
    public void testRunCallback() throws InterruptedException {
        Future future =scb.runCallback();
        try {
            //future.cancel(true);
            System.out.println(future.get());
        } catch (InterruptedException interruptE) {
            interruptE.printStackTrace();
        } catch (ExecutionException executionE) {
            System.out.println(executionE.getCause());
            executionE.printStackTrace();
        }
    }

    @Test
    public void testInterruption() throws InterruptedException {
        Future fu=scb.runCallback();

        fu.cancel(true);

        //Give interrupt, 2 ways, shutdownNow will give interruption to all running threads in the pull
        //future.cancel() only gives the interruption to
    }

}
