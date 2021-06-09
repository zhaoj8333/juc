package jmm.mem;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;

public class VolatileDemo {
    final static Object obj = new Object();

    public static void main(String[] args) throws Exception {
//        testVisibility();
//        testReorder();
//        testSync();
        alternative();
    }

    /**
     * 滥用volatile的危害：volatile的实现是通过MESI实现的，会使用到CPU的嗅探机制，过多的交互会引发 总线风暴，CAS也会引发这个问题
     *
     * {@link sun.misc.Unsafe} 有插入屏障的方法
     *
     * 乱序执行：
     *     cpu核心拥有特殊的排序缓冲记忆区域来帮助乱序执行的执行，将加载和存储分开，分为：
     *      LOB:loading-order buffer: 加载顺序缓冲区
     *      SOB:store-order buffer: 存储顺序缓冲区
     *
     *     从软件的角度，执行以下指令：
     *      清空LOB(loadFence):
     *          LOB用于存储失效请求，以便失效可以异步执行，减少接收端停顿，希望在那里执行代码实际上不需要该值
     *          装入防护，处理完LOB的所有条目之前，没有其他指令将在该内核上执行
     *      清空SOB(storeFence):
     *          存储防护，SOB用于存储输出值(从处理器到cache，MESI用于获取写入高速缓存的权限)
     *          处理完SO的所有条目之前，不会有其他指令在此内核上运行
     *      清空LOB,SOB: 清空LOB,SOB
     *
     *      // 例:
     *      //     failure最初有 CPU 1拥有， shutdown由CPU 0 拥有
     *      // CPU 0:
     *      void shutdownWithFailure(void) {
     *          failure = 1;        // must use SOB as this is owned by CPU 1
     *          shutdown = 1;       // can execute immediately as it is owned by CPU 0
     *      }
     *      // CPU 1:
     *      void workLoop(void) {
     *          while (shutdown == 0) { ... }
     *          if (failure) { ... }
     *      }
     *      如果没有存储屏障，cpu 0可能会由于故障而发出关闭信号，但cpu 1将退出循环，且如果阻塞不会进入故障处理
     *      这是因为cpu 0将1写入failure存储顺序缓冲区，同时还会发出缓存一致性消息以获取相对缓存行的独占访问权，然后继续下一条指令并shutdown立即更新标志（此时无序与其他cpu内核协商）
     *      稍后从cpu 1接收到无效确认消息时，将继续处理SOB failureb并将其写入高速缓存。
     *
     *      @TODO
     *      插入storeFence()将解决问题：
     *      // CPU 0: shutdown
     *      void shutdownWithFailure(void) {
     *          failure = 1;        // must use SOB as this is owned by CPU 1
     *          shutdown = 1;       // can execute immediately as it is owned by CPU 0
     *      }
     *      // CPU 1: failure
     *      void workLoop(void) {
     *          while (shutdown == 0) { ... }
     *          if (failure) { ... }
     *      }
     *
     *
     *  X_Y_Fence(): all instructions of type X before the barrier completed before any operation of type Y after the barrier is started
     *
     * {@link sun.misc.Unsafe}.loadFence(): load_loadstoreFence() 该方法之前的所有load操作在内存屏障之前完成
     * {@link sun.misc.Unsafe}.storeFence(): store_loadStoreFence() 该方法之前的store操作在内存屏障之前完成
     * {@link sun.misc.Unsafe}.fullFence(): loadstore_loadstoreFence() 该方法之前的所有load，store操作在内存屏障之前完成
     */
    private static void alternative() throws Exception {
        Field field = Unsafe.class.getDeclaredField("theUnsafe");
        field.setAccessible(true);
        Unsafe unsafe = (Unsafe) field.get(null);
        while (true) {
            a = b = 0;
            x = y = 0;
            Thread t1 = new Thread(() -> {
                a = 1;
                unsafe.storeFence();
                x = b;
            });
            Thread t2 = new Thread(() -> {
                b = 1;
                unsafe.storeFence();
                y = a;
            });
            /**
             * 如果不出现指令重排序，x,y只能有三种情况：
             *  x = 1, y = 1
             *  x = 1, y = 0
             *  x = 0, y = 1
             */
            t1.start();
            t2.start();
            t1.join();
            t2.join();
            if (x == 0 && y == 0) {
                System.err.println("a: " + a + ", b: " + b + ", x: " + x + ", y: " + y);
                break;
            }
        }
    }

    boolean syncRun = true;
    // 使用synchronized保证变量可见性

    /**
     * synchronized会创建Monitor, 相对于volatile消耗更大
     *
     * JMM对synchronized可见性的规定: (1), (2)
     */
    private static void testSync() {
        final VolatileDemo v = new VolatileDemo();
        final Thread t2 = new Thread(()->{
            while (true) {
                // 2. 线程加锁时,将清空工作内存中共享变量的值,从而使用共享变量时需要从主内存重新读取最新的值
                synchronized (obj) {
                    System.out.println("running ...");
                    // getstatic run
                    if (! v.syncRun) {
                        break;
                    }
                }
            }
        }, "t2");
        t2.start();
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        synchronized (obj) {
            // putstatic run
            v.syncRun = false;
            // 1. 线程解锁前,必须把共享变量的最新值刷新到主内存
        }
    }

    /**
     * 禁止指令冲排序：
     *     jvm规范规定jvm线程内部维持顺序化语义，只要程序结果与顺序化情况相同，指令执行顺序可以与代码顺序不一致
     *
     * as-if-serial原则：无论如何重排序，单线程下编译器和处理器不能对存在依赖关系的操作做重排序
     *     但是如果没有，则可以。
     *
     * JMM针对重排序问题的规则表
     */
    private static int a, b, x, y;
    private static void testReorder() throws InterruptedException {
        while (true) {
            a = b = 0;
            x = y = 0;
            Thread t1 = new Thread(() -> {
                a = 1;
                x = b;
            });
            Thread t2 = new Thread(() -> {
                b = 1;
                y = a;
            });
            /**
             * 如果不出现指令重排序，x,y只能有三种情况：
             *  x = 1, y = 1
             *  x = 1, y = 0
             *  x = 0, y = 1
             */
            t1.start();
            t2.start();
            t1.join();
            t2.join();
            if (x == 0 && y == 0) {
                System.err.println("a: " + a + ", b: " + b + ", x: " + x + ", y: " + y);
                break;
            }
        }
    }

    /**
     * java线程 内存模型
     *     线程每次要频繁读取主内存中读取run的值,jit会将run的值缓存至自己工作内存中的高速缓存中,减少对主存run的访问,提高效率
     *
     * lock addl指令：
     *     锁总线：其他cpu对内存的读写会被阻塞，直到锁释放，
     *     lock后的写操作会回写已修改数据，让其他cpu相关缓存行失效，从而重新从主存加载最新数据
     */
    // volatile保证各个线程共享变量的可见性
//    volatile boolean run = false;
    boolean run = false;
    private int index = 0;
    // happens-before规则 第3条
    private static void testVisibility() {
        final VolatileDemo v = new VolatileDemo();
        // volatile
        Thread t1 = new Thread(()->{
            while (!v.run) {
                v.index++;
            }
        }, "t1");
        t1.start();

        try {
            TimeUnit.MILLISECONDS.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        for (int i = 0; i < 10_0000_0000; i++) {
//            v.run = true;
//        }
        v.run = true;
        System.out.println(v.index);
    }
}

