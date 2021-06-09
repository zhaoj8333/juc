package jmm.mem.barrier;

/**
 * 内存屏障：
 *
 *  MESI协议：
 *      Modified:
 *      Exclusive:
 *      Shared:
 *      Invalid:
 *
 * JSR内存屏障：
 *     LOADLOAD:   load1;loadload;load2，load2以及后续读取操作要读取的数据被访问前，保障load1要读取的数据被读取完毕
 *     STORESTORE: store1;storestore;store2，store2以及后续写操作执行前，保障store1的写入操作对其他处理器可见
 *     LOADSTORE:  load1;loadstore;store2,store2以及后续写入操作被刷出前，保证load1要读取的数据被读取完毕
 *     STORELOAD:  store1;storeload;load2,load2以后续操作执行前，保障store1的写入对所有处理器可见
 *
 * volatile底层原理 是 内存屏障
 *
 *     ---- storestore ----     ---- loadload ----
 *          VOLATILE 写              VOLATILE 读
 *     ---- storeload  ----     ---- loadstore ----
 *
 *     对volatile变量的写指令后加入写屏障
 *     对volatile的读指令前会加入读屏障
 *
 * intel lock指令
 *     原子指令，指令是一个full barrier，执行时会锁住内存子系统确保执行顺序，甚至跨多个cpu
 *     软件lock通常使用内存屏障或原子指令实现变量可见性保障执行顺序
 *
 * X86内存屏障：
 *     sfence：sfence之前的写操作必须在sfence指令后的写操作前完成
 *     lfence：lfence指令前的读操作必须在lfence指令后的读操作前完成
 *     mfence：mfence指令前的读写操作必须在mfence指令后的读写操作前完成
 *
 * volatile在不同层级的实现：
 *     java源码 volatile int i
 *     bytecode ACC_VOLATILE
 *     jsr: jvm内存屏障
 *     hotspot实现：汇编语言调用
 *     cpu硬件：MESI原语支持，总线锁
 *
 * volatile不能解决指令交错:
 *     写屏障仅仅保证之后的读能读到最新结果,但不能保证读跑到它前面去
 *     而有序性的保证也只是保证了 本线程内部 相关代码不被重排序,不能保证线程依次执行
 *
 *
 * volatile对内存屏障的使用：
 *     对volatile变量的写指令后会加入写屏障 sfence
 *     对volatile变量的读指令前会加入读屏障 lfence
 */
public class MemoryFence {
    static int num = 0;
    static volatile boolean ready = false;

    public static void main(String[] args) {

        sfence();
        lfence();

    }

    private static void lfence() {
        // 读屏障
        // 读屏障保证在该屏障之后, 对共享变量的读取加载的是主存中的最新数据, 保证可见性
        // 读屏障确保指令重排序时, 不会将读屏障之后的代码排在读屏障之前
        if (ready) {
            num *= 20;
//            System.out.println(num);
            if (num != 40) {
                System.err.println("error");
            }
        }
        System.out.println(num);
    }

    private static void sfence() {
        num = 2;
        ready = true;
        // 写屏障：volatile的写操作之后会将所有的改动同步到主存，包括非volatile变量
        // 写屏障保证在该屏障之前的, 对共享变量的改动,都同步到主存, 保证可见性
        // 写屏障会确保指令重排序时, 不会将写屏障之前的代码排在写屏障之后
    }

}


