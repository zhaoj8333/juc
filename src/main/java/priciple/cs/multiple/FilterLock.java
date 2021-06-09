package priciple.cs.multiple;

import java.util.Arrays;

/**
 * 过滤锁：
 *    {@link priciple.cs.two.Perterson}锁算法的在多线程上的一般化
 */
public class FilterLock {

    int[] level;
    int[] victim;

    public FilterLock(int n) {
        this.level = new int[n];
        this.victim = new int[n];
        for (int i = 0; i < n; i++) {
            this.level[i] = 0;
        }
    }

    public void lock() {
        int me = (int) Thread.currentThread().getId();
        int n = this.level.length;
        me = me % n;
        for (int i = 1; i < n; i++) {
            level[me] = i;
            victim[i] = me;

            int k = 1;
            while ((k != me) && (level[k] >= i && victim[i] == me)) { }
        }
    }

    public void unlock() {
        int id = (int) Thread.currentThread().getId();
        int n = this.level.length;
        id = id % n;
        this.level[id] = 0;
    }

    @Override
    public String toString() {
        return "FilterLock{\n\t" +
                "level=" + Arrays.toString(level) +
                ", \n\tvictim=" + Arrays.toString(victim) +
                "\n}";
    }

    public static void main(String[] args) {
        FilterLock fl = new FilterLock(10);
        fl.lock();
        System.out.println(fl);
    }
}
