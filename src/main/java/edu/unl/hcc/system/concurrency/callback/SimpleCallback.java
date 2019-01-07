package edu.unl.hcc.system.concurrency.callback;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class SimpleCallback {

    ExecutorService executor;


    public SimpleCallback(){}

    public Future<?> runCallback() throws InterruptedException {
        executor = Executors.newCachedThreadPool();
        //当这个submit执行以后，jvm创建新的threads，它与主线程分离，独自运行，并可以自行输出到terminal
        Future<?> future = executor.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws InterruptedException, IOException {
                System.out.println("System starting...");
                Random ran = new Random();
                int generateInt = ran.nextInt(4000);
                Thread.sleep(generateInt);
                while(!Thread.currentThread().isInterrupted()) {
                    System.out.println("System waiting for interruption");
                    Thread.sleep(generateInt);
                }
                System.out.println("Interrupted!");
                return generateInt;
            }
        });
        //主线程sleep等待子线程进入while循环，然后在进行interrupt
        Thread.sleep(5000);
        executor.shutdownNow();

        executor.awaitTermination(1, TimeUnit.DAYS);

        System.out.println("Finished!");
        return future;
    }
}



