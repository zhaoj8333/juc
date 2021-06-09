package thrd.pool.locality;

import java.util.concurrent.CountDownLatch;

public class CachelineWithPadding {

    public static long COUNT = 1_0000_0000L;

    private static class T {
        public long p1, p2, p3, p4, p5, p6, p7;
        public volatile long x = 0L; // 8 Byte
        public long p11, p12, p13, p14, p15, p16, p17;
        /*   p1       p2       p3       p4       p5        p6      p7
         * -------- -------- -------- -------- -------- -------- --------
         *
         *    x
         * --------
         *
         *   p11       p12      p13      p14      p15      p16      p17
         * -------- -------- -------- -------- -------- -------- --------
         */
    }

    public static T[] arr = new T[2];

    static {
        arr[0] = new T();
        arr[1] = new T();
    }

    public static void main(String[] args) {
        test1();

    }

    private static void test1() {
        CountDownLatch cdl = new CountDownLatch(2);
        Thread t1 = new Thread(() -> {
            for (long i = 0; i < COUNT; i++) {
                arr[0].x = i;
            }
            cdl.countDown();
        });

        Thread t2 = new Thread(() -> {
            for (long i = 0; i < COUNT; i++) {
                arr[1].x = i;
            }
            cdl.countDown();
        });

        long s = System.nanoTime();
        t1.start();
        t2.start();
        try {
            cdl.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println((System.nanoTime() - s) / 100_0000); // 690 上下
    }

}
