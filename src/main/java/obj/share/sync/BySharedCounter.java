package obj.share.sync;

public class BySharedCounter extends PrimeCalculator {

    private static final UnSafeCounter counter = new UnSafeCounter(0);
    private static int n = 0;
    private final static int pow = (int) Math.pow(10, 5);

    public static void main(String[] args) {
        BySharedCounter bsc = new BySharedCounter();
        long s = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            bsc.threads.add(new Thread(BySharedCounter::execTask));
        }

        bsc.threads.forEach(Thread::start);
        try {
            for (Thread thread : bsc.threads) {
                thread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long e = System.currentTimeMillis();
        System.out.println("duration: " + (e - s));
        // SafeCounter: 321
        // UnSafeCounter: 303
   }

   protected static void execTask() {
       while (n < pow) {
           n = counter.getAndIncrement();
           System.out.println(n);
           if (PrimNumber.isPrime(n)) {
//               System.out.println(n);
           }
       }
   }
}
