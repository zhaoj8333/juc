package design;

import java.util.concurrent.TimeUnit;

/**
 * 两阶段终止:
 *     一个线程如何 优雅的 终止另一线程, 优雅指的是 给另一线程一个料理后事的机会
 *
 * Thread.stop()方法会真正杀死线程,按时此时如果线程锁住了共享资源,那么该线程被杀死后再也没有机会释放锁
 * System.exit()会终止整个程序
 *
 */
@SuppressWarnings("all")
public class TwoPhaseTermination {

    public static void main(String[] args) throws InterruptedException {
        final TwoPhaseTermination tpt = new TwoPhaseTermination();
//        tpt.byInterrupt();
        tpt.byVolatile();
    }

    private Thread mThread;

    public void byInterrupt() throws InterruptedException {
        mThread = new Thread(() -> {
            while (true) {
                final Thread current = Thread.currentThread();
                if (current.isInterrupted()) {
                    System.out.println("料理后事");
                    break;
                }
                try {
                    Thread.sleep(100);
                    System.out.println("执行监控");
                } catch (InterruptedException e) {
                    // sleep时被打断, 就会进入cache块, 清除打断标记
                    // sleep出现异常后, 会清除打断标记
                    // 需要重置打断标记为 true
                    // 下次循环后, 在以上的current.isInterrupted 退出
                    // 设置打断 stop后,以下都可以不要了
                    System.out.println("清除打断标记");
                    current.interrupt();
                }
            }
        }, "monitor");

        mThread.start();
        TimeUnit.MILLISECONDS.sleep(500);
        this.stopByInterrupt();
    }

    // 停止监控线程 mThread
    public void stopByInterrupt() {
        // interrupt可以打断sleep
        mThread.interrupt();
    }

    ///////////////////////////////////////////////////////////////////////////////

    private volatile boolean stop = false;

    public void byVolatile() throws InterruptedException {
        mThread = new Thread(() -> {
            while (true) {
                final Thread current = Thread.currentThread();
                if (stop) {
                    System.out.println("被打断, exit...");
                    break;
                }
                try {
                    Thread.sleep(100);
                    System.out.println("执行监控");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "monitor");

        mThread.start();
        TimeUnit.MILLISECONDS.sleep(500);
        this.stopByVolatile();
    }

    // 停止监控线程 mThread
    public void stopByVolatile() {
        // 设置标记变量的话无法打断sleep
        stop = true;
    }
}
