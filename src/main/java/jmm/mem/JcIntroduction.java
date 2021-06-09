package jmm.mem;

/**
 * Memory shared between threads called Heap Memory
 *
 * shared variables:
 *  instance fields
 *  static fields
 *  array elements
 *
 * private variables:
 *  Local variables
 *  formal method parameters 形参
 *  exception-handler parameters  异常处理的参数
 *
 * modern CPUs has more levels of cache
 */
@SuppressWarnings("all")
public class JcIntroduction {
    public int a = 0;
    public int b = 0;
    public int r1 = 0;
    public int r2 = 0;

    public static void main(String[] args) {
        while (true) {
            exec();
        }

    }

    private static void exec() {
        JcIntroduction m = new JcIntroduction();
        Thread t1 = new Thread() {
            @Override
            public void run() {
//                System.out.println("t1");
                m.a = 10;
                m.r1 = m.b;
            }
        };
        Thread t2 = new Thread() {
            @Override
            public void run() {
//                System.out.println("t2");
                m.b = 20;
                m.r2 = m.a;
            }
        };
        t1.start();
        t2.start();
        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (m.r1 != 0 || m.r2 != 10) {
            System.out.println(m.r1 + " " + m.r2);
        }
    }
}


