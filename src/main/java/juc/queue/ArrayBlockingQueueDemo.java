package juc.queue;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * 数组阻塞队列，通过枷锁的方式，保障多线程安全：
 *    静态数组，容量固定且必须指定长度，没有扩容机制，没有元素的下标位置null占位
 */
public class ArrayBlockingQueueDemo {
    public static void main(String[] args) {
    }

    private void init() {
        ArrayBlockingQueue<Integer> abq = new ArrayBlockingQueue<Integer>(10);
    }

}
