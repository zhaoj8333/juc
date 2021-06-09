package juc.queue;

/**
    java中的队列：
        使用加锁实现的类，虽然有界，但是有锁的存在，性能会受到影响，由于锁竞争，知道锁的释放，才能恢复
        {@link java.util.concurrent.ArrayBlockingQueue}
        {@link java.util.concurrent.LinkedBlockingDeque}

        cas的实现的类是无界的，但是可能导致内存溢出
        {@link java.util.concurrent.ConcurrentLinkedDeque}
        {@link java.util.concurrent.LinkedTransferQueue}
 */