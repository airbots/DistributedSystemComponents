package edu.unl.hcc.system.concurrency;

import java.util.Scanner;

class TestThread extends Thread {

        private boolean running =true;
        public TestThread() {}

        @Override
        public void run(){
            while (running) {
                System.out.println("Hellp");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void shutDown(){
            running = false;
        }
    }

    class VThread extends Thread {


        private volatile boolean vRunning = true;
        public VThread() {}

        @Override
        public void run(){
            while(vRunning) {
                System.out.println("vHello!");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void shutdown(){
            vRunning= false;
        }
    }

    public class SimpleVolitale {

    public static void main(String[] args) {
        TestThread t1 = new TestThread();
        VThread t2 = new VThread();

        //t1.run();
        Scanner scanner = new Scanner(System.in);
       // scanner.next();
        //t1.shutDown();

        t2.run();
        scanner.nextLine();
        t2.shutdown();

    }
}
