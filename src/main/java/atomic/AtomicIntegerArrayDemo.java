package atomic;

import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicLong;

public class AtomicIntegerArrayDemo {
    public static void main(String[] args) {

        testAtomicArray();
    }

    private static void testAtomicArray() {
        final int[] ints = {1, 2, 3, 4, 5};
        final AtomicIntegerArray ai = new AtomicIntegerArray(ints);
//        System.out.println(ai.getAndSet(1, 10));
//        System.out.println(ai.addAndGet(1, 10));
//        System.out.println(ai.compareAndSet(1, 2, 20));
//        System.out.println(ai.getAndIncrement(1));
        System.out.println(ai.decrementAndGet(1));
        show(ai);
    }

    private static void show(AtomicIntegerArray ints) {
        System.out.println(ints);
    }

    /**
     * long 在32位CPU中, 要取两次
     */
    private static void testAtomicLong() {
        final AtomicLong l = new AtomicLong(100L);


    }

}
