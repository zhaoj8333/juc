package thrd.java;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * java若某个现场抛出异常没有捕获，该线程崩溃，但是对应的jvm进程不会崩溃，
 * 这样，其他线程不会受到影响，但是坏处是若没有外围监控，很难察觉到对应线程是否崩溃
 *
 * 严格来说，没有线程崩溃，只是触发了SIGSEGV，如果没有设置对应的signal handler，OS就自动
 * 终止进程，如果设置了，可以恢复进程继续
 *
 *
 */
public class ThreadCrash implements Runnable {
    private Thread t;
    private String name;

    public ThreadCrash(String name) {
        this.name = name;
        System.out.println("creating " + name);
    }

    @Override
    public void run() {
        try {
            for (int i = 4; i > 0; i--) {
                System.out.println("Thread : " + name + ", " + i);
                TimeUnit.MILLISECONDS.sleep(500);
            }
        } catch (Exception e) {
            System.out.println("Thread " + name + " interrupted");
        }
        int a = 100;
        int b = a / 0;
    }

    public void start() {
        if (Objects.isNull(t)) {
            t = new Thread(name);
            t.start();
        }
    }

    public static void main(String[] args) {
        ThreadCrash t1 = new ThreadCrash("t1");
        t1.start();
        try {
            while (true) {
                System.out.println("continue ...");
                TimeUnit.MILLISECONDS.sleep(500);
            }
        } catch (Exception e) {
            System.out.println("cache an exception");
        }
    }
}
