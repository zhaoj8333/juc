package obj.share.sync;

/**
 * 相同的输入范围并不意味着相同的工作量
 */
public class ByEqualInput extends PrimeCalculator {

    public static void main(String[] args) {

        ByEqualInput pnei = new ByEqualInput();
        int pow = (int) Math.pow(10, 5);

        long s = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            int finalI = i;
            pnei.threads.add(new Thread(() -> execTask(finalI * pow + 1, (finalI + 1) * pow)));
        }

        pnei.threads.forEach(Thread::start);
        try {
            for (Thread thread : pnei.threads) {
                thread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long e = System.currentTimeMillis();
        System.out.println("duration: " + (e - s));
    }

    protected static void execTask(int min, int max) {
        System.out.println(min + " " + max);
        long s = System.currentTimeMillis();
        for (int i = min; i <= max; i++) {
            if (PrimNumber.isPrime(i)) {
//                System.out.println(Thread.currentThread().getId() + ": " + i);
            }
        }
        long e = System.currentTimeMillis();
        System.out.println(Thread.currentThread().getId() + " : (" + min + "-" + max + ") : " + (e - s));
    }
}
