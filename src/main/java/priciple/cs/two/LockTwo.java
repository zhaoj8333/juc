package priciple.cs.two;

/**
 * 如果线程并发执行，lock方法是成功的
 * 但是一个线程先执行，另一线程迟迟不执行，则第一个线程无法执行
 */
public class LockTwo {
    private volatile int victim;

    public void lock() {
        int i = (int) Thread.currentThread().getId();
        victim = i;
        while (victim == i) {

        }
    }

    public void unclock() {

    }

}
