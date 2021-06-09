package priciple.cs.two;

/**
 * 双线程锁算法：
 *
 * 约定：
 *     线程标识为1或0，弱当前调用者标识为i，则另一方为 j = 1 - i, 线程通过调用Thread.currentThread().getId()获取自己的标识
 *
 * LockOne 算法满足互斥特性
 */
public class LockOne {

    private final boolean[] flag = new boolean[2];

    public void lock() {
        long tid = Thread.currentThread().getId();
        int i = (int) (tid % 2);
        long j = 1 - tid;

        flag[i] = true;
        while (flag[(int) j]) {

        }
    }

    public void unlock() {
        long tid = Thread.currentThread().getId();
        int i = (int) (tid % 2);
        flag[i] = false;
    }

    public static void main(String[] args) {

    }
}
