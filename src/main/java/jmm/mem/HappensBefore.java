package jmm.mem;

/**
 *
 * JSR-133规范，即java内存模型与线程规范，是JSR-176（java平台Tiger 5.0平台的特性）的一部分
 * 本规范java提供给开发者一些线程同步策略
 *
 * 线程同步三要素：
 *     可见性
 *     有序性
 *     原子性
 *
 * 如何理解一段程序是否被正确同步
 *     是否有访问冲突
 *     是否遵循happens-before规则：
 *
 * JMM与happends-before, JMM明确定义了以下三种问题
 *     1. 变量原子性问题:
 *         int等不大于32位的基本类型的操作都是原子操作
 *         32位的机器上，对long, double的数据的写入操作并非原子操作, 其他的原生数据类型以及引用类型的写入操作都是原子操作
 *         long, double被当成2个原子性的32位看待, 而不是原子性的64位值
 *
 *         volatile本身不保证原子性, 仅保障可见性
 *         但是JMM保证volatile的long和double变量的get和set是原子操作的
 *
 *         64位处理器能满足64位变量的原子性操作
 *
 *     2. 变量可见性问题
 *     3. 变量修改的时序性问题
 *
 * happens-before重要规则:
 *     1. 顺序执行规则:
 *         限定在单个线程中, 线程的每个动作都happens-before它的后面的动作, 但是跟指令重排序是没有关系的 （happens-before 先行发生于）
 *     2. 隐式锁(monitor)规则(管程锁定规则):
 *         针对于同一个monitor, 一个线程的unlock happens-before 另一个线程的lock,
 *         之前的线程同步代码块的所有执行结果对于后续获取锁的线程来说都是可见的
 *     3. volatile读写规则:
 *         对于一个volatile变量的写操作一定会happens-before后续对该变量的读操作
 *     4. 多线程的启动规则:
 *         Thread对象的start方法happens-before该线程run方法中的任何一个动作, 包括其中启动的任何子线程
 *         子线程执行自己的run方法时, 一定可以看到父线程在start之前所有的操作结果
 *     5. 多线程终止规则:
 *         当run方法返回或执行结束(join等等待结束), 线程都可以看到它所启动线程的执行结果
 *     6. 线程的中断规则:
 *         调用interrupt方法中断线程, 这个调用happens-before对该线程中断的检查(isInterrupted)
 *     7. 对象终结规则：对象初始化完成先行于finalize()方法的开始
 *     8. 传递性：操作a先行于操作b，操作b先行于操作c，则a先行于操作c
 */
public class HappensBefore {

    public static void main(String[] args) throws Exception {
//        ensureVisibilityBySynchronized();
        ensureVisibilityByVolatile();
//        ensureVisibilityFromOtherThreadsBeforeStart();
//        ensureVisibilityFromOtherThreadsAfterEnd();
//        ensureByInterrupt();
//        ensureByVolatileTransitivity();
    }

    /**
     * 8. 传递性
     *     配合volatile防止指令重排序
     *
     */
    static int c;
    volatile static int d;
    static int a1;
    public static void ensureByVolatileTransitivity() throws InterruptedException {
        new Thread(() -> {
            while (true) {
                if (d == 10 && c == 20 && a1 == 1) {  // 此处如果读取非voaltile的变量时才会进行内存同步，且同步的是所有的变量
//                if (c == 0 && d == 0) { // 此处如果先读取非voaltile的变量 c，不会进行工作内存和主内存的同步，读出的数据仍然是0
                    System.out.println("d = " + d + ", c = " + c + ", a1 = " + a1);
                    break;
                }
            }
        }, "t2").start();
        Thread.sleep(10);
        new Thread(() -> {
            c = 20;
            d = 10;
            a1 = 1;
            // 写屏障会将 之前的写操作同步到主存, 无论其写之前的变量是否为volatile
        }, "t1").start();
    }

    /**
     * 7. 对变量默认值(0, false, null)的写,对其他线程对该变量的读可见
     */
    public static void defaultVal() {

    }

    /**
     * 6. 线程t1打断t2前对变量的写,对于其他线程得知t2被打断后对变量的读可见
     */
    public static void ensureByInterrupt() {
        Thread t2 = new Thread(() -> {
            while (true) {
                if (Thread.currentThread().isInterrupted()) {
                    System.out.println(Thread.currentThread() + " is interrupted");
                    System.out.println("t2 read x: " + x);
                    break;
                }
            }
        }, "t2");
        t2.start();

        final Thread t1 = new Thread(()->{
            try {
                Thread.sleep(600);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            x = 10;
            t2.interrupt();
        }, "t1");
        t1.start();

        while (!t2.isInterrupted()) {
            Thread.yield(); // A hint to the scheduler that the current thread is willing to yield its current use of a processor
        }
        System.out.println("main: " + x);
    }

    /**
     * happens-before规则:
     *     规定了对共享变量的写操作对其他线程的读操作可见, 它是可见性与有序性的一套规则总结
     *     抛开happens-before规则, jvm并不保证一个线程对共享变量的写对其他线程对该共享变量的读可见
     */
    /**
     * 5. 线程t1打断t2前对变量的写,对于其他线程得知t2被打断后对变量的读可见
     */
    volatile static int y;
    static int z;
    static Object lock = new Object();

    /**
     * 4. 线程结束前,对变量的写,对其他线程得知结束后的读可见
     */
    static int b;
    public static void ensureVisibilityFromOtherThreadsAfterEnd() throws InterruptedException {
        final Thread t1 = new Thread(()->{
            b = 10;
        }, "t1");
        t1.start();
        t1.join();

        System.out.println(b);
    }

    /**
     * 3. 线程start前对变量的写, 对该线程开始对该变量的读可见
     *     线程结束后, 对共享变量的修改必须同步到主内存
     *     线程启动时, 必须从主内存读取共享变量
     */
    static int a;
    public static void ensureVisibilityFromOtherThreadsBeforeStart() {
        a = 10;
        new Thread(() -> {
            System.out.println(a);
        }, "t2").start();
    }

    /**
     * 2. 线程对volatile变量的写, 对接下来其他线程的对该变量的读可见
     *     volatile保证可见性
     */
    static volatile int v;
    public static void ensureVisibilityByVolatile() {
        new Thread(() -> {
            v = 10;
        }, "t1").start();
        new Thread(() -> {
            System.out.println(v);
        }, "t2").start();
    }

    /**
     * 1. 线程解锁m之前对变量的写, 对于接下来对m加锁的其他线程对改变量的读可见
     *      synchronized保证可见性
     */
    static int x;
    public static void ensureVisibilityBySynchronized() throws InterruptedException {
        final Thread t1 = new Thread(()->{
            synchronized (lock) {
                x = 10;
            }
        }, "t1");

        final Thread t2 = new Thread(()->{
            int test = 0;
            synchronized (lock) {
                test = x;
                System.out.println(x);
                System.out.println(test);
            }
        }, "t2");
        t1.start();
        Thread.sleep(1);
        t2.start();
    }


}
