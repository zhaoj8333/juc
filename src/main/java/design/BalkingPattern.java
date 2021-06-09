package design;

import java.util.concurrent.TimeUnit;

/**
 * 希望某个方法只被调用一次: 双重检查机制
 */
public class BalkingPattern {

    private Thread monitorThread;

    private volatile boolean running = false;

    public void executeMonitor() throws InterruptedException {
        synchronized (this) {
            // 此处应该 做保护
            // 保证对共享变量读写的原子性
            if (running) {
                return;
            }
            running = true;
        }
        monitorThread = new Thread(() -> {
            while (true) {
//                if (Thread.currentThread().isInterrupted()) {
//                    System.err.println("已经在执行监控了");
//                    System.exit(1);
//                }
                System.out.println("执行监控中: " + Thread.currentThread().getName());
            }
        });
        monitorThread.start();

        TimeUnit.MILLISECONDS.sleep(2);
        this.stop();
    }

    public void stop() {
        monitorThread.interrupt();
    }

    public static void main(String[] args) throws InterruptedException {
        BalkingPattern bp = new BalkingPattern();
        new Thread(() -> {
            try {
                bp.executeMonitor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        new Thread(() -> {
            try {
                bp.executeMonitor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            try {
                bp.executeMonitor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}