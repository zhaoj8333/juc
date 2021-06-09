package jvm.arch;

import java.lang.management.ManagementFactory;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Java VirtualMachine 内存结构
 *
 * 栈：
 *  hotspot的java线程栈使用宿主操作系统的栈和线程模型表示的，java方法和native方法共享相同的栈，
 *  所以Hotspot中栈和本地方法是一回事。
 *
 *  java线层内存：不是指 {@link Thread}对象的内存，其Thread对象本身在heap中分配，调用start之后，jvm
 *      会创建执行单元，最终创建一个os的本地线层来执行，而这个执行单元或native thread是使用stack内存空间的
 *
 * Heap:
 * +------------------+----------+
 * |       Young      |    Old   |
 * +------------------+----------+
 * | Eden | From | To |          | Eden:From:To，三者默认的堆大小分配比例是 8:1:1
 * +------+------+----+----------+
 *
 * 堆的划分是根据对象存活时间的不同来做的划分
 */
public class JvmMemStructure {
    public static void main(String[] args) throws InterruptedException {
        heap();
//        nativeThread();
//        permgen();
//        arraysizeExceeds();
//        gc();
//        swap();
//        oomAndThread();
//        directBuffer();
//        gcOverhead();
    }

    /**
     * {@link OutOfMemoryError}
     * 当gc为释放很小空间而占用大量时间时抛出
     */
    private static void gcOverhead() {
    }

    /**
     * {@link java.nio.ByteBuffer} 是在直接内存分配，不受GC管理，但是 {@link java.nio.ByteBuffer}的java对象归GC管理
     * 只要gc了该对象，OS才会释放DC空间，通过 -XX:MaxDirectMemorySize设置
     */
    private static void directBuffer() {
        while (true) {
            ArrayList<ByteBuffer> list = new ArrayList<>();
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(100000000);
            list.add(byteBuffer);
        }
    }

    /**
     * {@link OutOfMemoryError}, request bytes for .Out of swap space
     * 物理内存以及交换分区都用完后，再次从本地分配内存时也会抛出该异常
     */
    private static void swap() {
    }

    /**
     * GC花费了大量时间，却只回收了少量内存
     *
     * The parallel(concurrent) collector will throw an OutOfMemoryError if too much time is being spent in garbage collection:
     *  if more than 98% of the total time is spent in garbage collection and
     *  less than 2% of the heap is recovered, an OutOfMemoryError will be thrown.
     */
    private static void gc() {}

    /**
     * {@link OutOfMemoryError} : Requested array size exceeds VM limit
     *
     * 比较少见，详细信息表示应用申请的数组大小已经超过堆大小。
     * 如应用程序申请512M大小的数组，但堆大小只有256M，这里会抛出OutOfMemoryError，因为此时无法突破虚拟机限制分配新的数组。
     * 在大多少情况下是堆内存分配的过小，或是应用尝试分配一个超大的数组，如应用使用的算法计算了错误的大小。
     */
    private static void arraysizeExceeds() {
    }

    /**
     * {@link OutOfMemoryError} : PermGen space
     */
    private static void permgen() {
        ArrayList<Class<?>> list = new ArrayList<>();
        while (true) {
        }
    }

    /**
     * {@link OutOfMemoryError} : Unable to create new native thread
     *      原因：Stack空间不足以创建额外线程，要么线程数过多，要么栈空间太小
     *      解决：-Xss减少单个线程栈大小，但容易出现 {@link StackOverflowError}
     *           -Xms -Xmx 减少Heap大小，让给Stack
     *
     * 在JVM中每启动一个线程都会分配一块本地内存，用于存放线程的调用栈，该空间仅在线程结束时释放。
     * 当没有足够本地内存创建线程时就会出现该错误。通过以下代码可以很容易再现该问题：
     *
     */
    private static void nativeThread() {
        while (true) {
            new Thread(() -> {
                try {
                    Thread.sleep(60 * 60 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
        /**
         * [2.258s][warning][os,thread] Failed to start thread - pthread_create failed (EAGAIN) for attributes: stacksize: 1024k, guardsize: 4k, detached.
         * Exception in thread "main" java.lang.OutOfMemoryError: unable to create native thread: possibly out of memory or process/resource limits reached
         * 	at java.base/java.lang.Thread.start0(Native Method)
         * 	at java.base/java.lang.Thread.start(Thread.java:801)
         * 	at jvm.JvmMemStructure.nativeThread(JvmMemStructure.java:54)
         * 	at jvm.JvmMemStructure.main(JvmMemStructure.java:32)
         */
    }

    /**
     * {@link OutOfMemoryError} : Java heap space
     *      原因：heap溢出，意味着young或old代内存不够
     *      解决：-Xms -Xmx增加堆内存
     *
     * 堆内存溢出时，首先判断当前最大内存是多少（参数：-Xmx 或 -XX:MaxHeapSize=），可以通过命令 jinfo -flag MaxHeapSize 查看运行中的JVM的配置，
     *  jinfo -flag MaxHeapSize 13276
     *  -XX:MaxHeapSize = 8589934592
     *
     * 如果该值已经较大则应通过 mat 之类的工具查找问题，或 jmap -histo查找哪个或哪些类占用了比较多的内存。
     *
     * 参数-verbose:gc(-XX:+PrintGC) -XX:+PrintGCDetails可以打印GC相关的一些数据。
     *
     * 如果问题比较难排查也可以通过参数 -XX:+HeapDumpOnOutOfMemoryError 在OOM之前Dump内存数据再进行分析。
     *
     * 此问题也可以通过histodiff打印多次内存histogram之前的差值，有助于查看哪些类过多被实例化，如果过多被实例化的类被定位到后可以通过btrace再跟踪。
     *
     * -Xms10M -Xmx10M -Xlog:gc* -XX:+UseSerialGC
     *
     * [0.007s][info][gc] Using Serial
     * [0.175s][info][gc,heap,exit   ] Heap
     * [0.175s][info][gc,heap,exit   ]  def new generation   total 3072K, used 69K [0x00000007ff600000, 0x00000007ff950000, 0x00000007ff950000)
     * [0.175s][info][gc,heap,exit   ]   eden space 2752K,   2% used [0x00000007ff600000, 0x00000007ff611698, 0x00000007ff8b0000)
     * [0.175s][info][gc,heap,exit   ]   from space 320K,   0% used [0x00000007ff900000, 0x00000007ff900000, 0x00000007ff950000)
     * [0.175s][info][gc,heap,exit   ]   to   space 320K,   0% used [0x00000007ff8b0000, 0x00000007ff8b0000, 0x00000007ff900000)
     * [0.175s][info][gc,heap,exit   ]  tenured generation   total 6848K, used 4364K [0x00000007ff950000, 0x0000000800000000, 0x0000000800000000)
     * [0.175s][info][gc,heap,exit   ]    the space 6848K,  63% used [0x00000007ff950000, 0x00000007ffd93320, 0x00000007ffd93400, 0x0000000800000000)
     * [0.175s][info][gc,heap,exit   ]  Metaspace       used 1337K, capacity 4534K, committed 4864K, reserved 1056768K
     * [0.175s][info][gc,heap,exit   ]   class space    used 141K, capacity 399K, committed 512K, reserved 1048576K
     *
     * SurvivorRatio: 比例为 8:1:1
     *
     * -Xms10M -Xmx10M -Xlog:gc* -XX:+UseSerialGC -XX:SurvivorRatio=4
     * [0.184s][info][gc,heap,exit   ] Heap
     * [0.184s][info][gc,heap,exit   ]  def new generation   total 2880K, used 51K [0x00000007ff600000, 0x00000007ff950000, 0x00000007ff950000)
     * [0.184s][info][gc,heap,exit   ]   eden space 2368K,   2% used [0x00000007ff600000, 0x00000007ff60cf30, 0x00000007ff850000)
     * [0.184s][info][gc,heap,exit   ]   from space 512K,   0% used [0x00000007ff8d0000, 0x00000007ff8d0000, 0x00000007ff950000)
     * [0.184s][info][gc,heap,exit   ]   to   space 512K,   0% used [0x00000007ff850000, 0x00000007ff850000, 0x00000007ff8d0000)
     * [0.184s][info][gc,heap,exit   ]  tenured generation   total 6848K, used 4364K [0x00000007ff950000, 0x0000000800000000, 0x0000000800000000)
     * [0.184s][info][gc,heap,exit   ]    the space 6848K,  63% used [0x00000007ff950000, 0x00000007ffd930d0, 0x00000007ffd93200, 0x0000000800000000)
     * [0.184s][info][gc,heap,exit   ]  Metaspace       used 1336K, capacity 4534K, committed 4864K, reserved 1056768K
     * [0.184s][info][gc,heap,exit   ]   class space    used 141K, capacity 399K, committed 512K, reserved 1048576K
     *
     * 堆内存溢出后，其他线程是否可以正常工作：
     *     发生OOM的线程都会终结,该线程引用的对象heap都会被gc,释放内存,因为OOM之前都会gc，就算其他线程正常工作，也会因为gc产生较大影响
     */
    private static void heap() throws InterruptedException {
        String name = ManagementFactory.getRuntimeMXBean().getName();
        System.out.println(name);

        Thread t1 = new Thread(() -> {
            System.out.println("thread t1 continue to run");
        });

        Thread t2 = new Thread(() -> {
            ArrayList<String> list = new ArrayList<>();
            for (int i = 0; i < 10000000; i++) {
                list.add("consume more memory");
            }
        });

        t2.start();
//        t2.join();  // t2结束后才运行t1，t1可以正常运行，但是如果同时运行，则t1无法运行或者在t2结束完之前正常运行完毕
        t1.start();
    }

    /**
     * t2: running ...
     * t1: running ...
     * t2: running ...
     * Exception in thread "t1" java.lang.OutOfMemoryError: Java heap space
     * 	at jvm.JvmMemStructure.lambda$oomAndThread$3(JvmMemStructure.java:217)
     * 	at jvm.JvmMemStructure$$Lambda$14/0x0000000800b8d248.run(Unknown Source)
     * 	at java.base/java.lang.Thread.run(Thread.java:832)
     * t2: running ...
     * t2: running ...
     * t2: running ...
     */
    private static ArrayList<byte[]> list = new ArrayList<>();

    private static void oomAndThread() {
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        new Thread(() -> {

            while (true) {
                list.add(new byte[1024 * 1024 * 10]);
                System.out.println(Thread.currentThread().getName() + ": running ...");
                try {
                    TimeUnit.MILLISECONDS.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "t1").start();

        new Thread(() -> {
            while (true) {
                try {
                    System.out.println("list new size: " + list.size());
                    System.out.println(Thread.currentThread().getName() + ": running ... list new size: " + list.size() + " " + System.currentTimeMillis());
                    TimeUnit.MILLISECONDS.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "t2").start();
    }
}
