package io.bobmakhlin.reordering;

public class Main {
    static int x = 0;
    static int y = 0;
    static int r1 = -1;
    static int r2 = -1;

    public static void main(String[] args) throws InterruptedException {
        int iterations = 0;

        while (true) {
            iterations++;

            x = 0;
            y = 0;
            r1 = -1;
            r2 = -1;1

            Thread t1 = new Thread(() -> {
                x = 1;
                r1 = y;
            });

            Thread t2 = new Thread(() -> {
                y = 1;
                r2 = x;
            });

            /*
            Each thread has:
            1) write operation
            2) read operation

            1. Instruction reordering:
                From the perspective of one thread, they might be reordered,
                so that the read happens before write!
            2. Cache visibility / cache coherence latency
                x = 1 might be written in L1 cache of the core1 executing thread 1,
                while core2 executing the thread 2 has not yet received x = 1 in its L1
                (via coherence protocol - MESI)

             Sequential consistency violation due to data races.
             */

            t1.start();
            t2.start();

            t1.join();
            t2.join();

            if (r1 == 0 && r2 == 0) {
                System.out.printf("Found reordering after %,d iterations: r1 = %d, r2 = %d%n", iterations, r1, r2);
                break;
            }

            if (iterations % 1_000_000 == 0) {
                System.out.printf("Still running... %,d iterations%n", iterations);
            }
        }
    }
}