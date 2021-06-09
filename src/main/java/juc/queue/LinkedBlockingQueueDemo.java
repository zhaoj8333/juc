package juc.queue;

import javax.swing.*;
import java.util.concurrent.LinkedBlockingQueue;

public class LinkedBlockingQueueDemo {
    public static void main(String[] args) {
//        node();
//        init();
//        demo();
//        lock();
        put();
    }

    private static void put() {
        LinkedBlockingQueue<Integer> lbq = new LinkedBlockingQueue<>();
        for (int i = 0; i < 2; i++) {
            int finalI = i;
            Thread t = new Thread(() -> {
                try {
                    lbq.put(finalI);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            t.start();
        }
    }

    /**
     * 加锁分析：两把锁和dummy节点
     *    用一把锁，同一时刻，最多只允许有一个线程（生产或消费者，二选一）执行
     *    用两把锁，同一时刻，允许两个线程同时（一个生产者和一个消费者）执行
     *      ：消费者与消费者线程串行执行
     *      ：生产者与生产者线程串行执行
     *
     *    ReentrantLock takeLock = new ReentrantLock();
     *      用于put（阻塞），offer（非阻塞）
     *
     *    ReentrantLock putLock  = new ReentrantLock();
     *      用于take（阻塞），poll（非阻塞）
     *
     *    节点总数大于2时（包括dummy节点），putLock保障last节点的线程安全，takeLock保障head节点的线程安全，两把锁保障入队和出队没有竞争
     *    节点总数等于2时，（一个dummy节点和一个正常节点），仍然是两把锁对象，不会竞争
     *    节点总数等于1时，（一个dummy节点），这是take会被notEmpty条件阻塞，有竞争，会阻塞
     *
     */
    private static void lock() {

    }

    /**
     * enqueue(): 队列尾添加
     *    last = last.next = node;
     *    两次赋值, 第二次将last指向最后一个node
     *
     *
     * dequeue(): 从头部移走
     *    Node<E> h = head;
     *    Node<E> first = h.next;
     *    h.next = h; // help GC
     *    head = first;
     *    E x = first.item;
     *    first.item = null;
     *    return x;
     *
     */
    private static void demo() {
        LinkedBlockingQueue<Integer> lbq = new LinkedBlockingQueue<>(10);
        for (int i = 0; i < 10; i++) {
            int finalI = i;
            Thread t = new Thread(() -> {
                try {
                    lbq.put(finalI);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            t.start();
        }
        System.out.println(lbq);

        for (int i = 0; i < 10; i++) {
            Thread t = new Thread(() -> {
                try {
                    lbq.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            t.start();
        }
        System.out.println(lbq);

    }

    /**
     * last = head = new Node<E>(null); 初始化时用Dummy节点占位
     */
    private static void init() {

    }

    /**
     *
     * One of:
     *  - the real successor Node                                           真正的后继节点
     *  - this Node, meaning the successor is head.next*                    自己，发生在出队时
     *  - null, meaning there is no successor (this is the last node)*      表示没有后继节点，是最后了
     *
     * static class Node<E> {
     *    E item;
     *
     *    Node<E> next;
     *
     *    Node(E x) { item = x; }
     *
     * }
     */
    private static void node() {

    }
}
