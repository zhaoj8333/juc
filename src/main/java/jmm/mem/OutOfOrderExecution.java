package jmm.mem;

public class OutOfOrderExecution {

    private static int a1, a2, b1, b2;

    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; ; i++) {
            a1 = a2 = b1 = b2 = 0;
            Thread t1 = new Thread(() -> {
                b1 = 1;
                a1 = b2;
            });
            Thread t2 = new Thread(() -> {
                b2 = 1;
                a2 = b1;
            });
            t1.start();
            t2.start();

            t1.join();
            t2.join();
            if (a1 == 0 && a2 == 0) {
                System.out.println("i: " + i);
                break;
            }
        }
    }

}
