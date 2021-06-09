package jmm.mem.barrier;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.Arrays;

/**
 * JVM 对Java开发者所开放的权限非常有限。例如：如果要原子性地增加某个字段的值，到目前为止我们可以使用下面三种方式：
 *
 * 使用AtomicInteger来达到这种效果，这种间接管理方式增加了空间开销，还会导致额外的并发问题；
 * 使用原子性的FieldUpdaters，由于利用了反射机制，操作开销也会更大；
 * 使用sun.misc.Unsafe提供的JVM内置函数API，虽然这种方式比较快，但它会损害安全性和可移植性，当然在实际开发中也很少会这么做。
 * {@link sun.misc.Unsafe}所做的操作不符合java标准
 *
 * {@link java.lang.invoke.VarHandle}替代了java.util.concurrent.atomic和sun.misc.Unsafe的部分操作。
 * 官方推荐使用 {@link VarHandle}替代 {@link sun.misc.Unsafe}的大部分功能
 *
 * 并且提供了一系列标准的内存屏障操作，用于更加细粒度的控制内存排序。在安全性、可用性、性能上都要优于现有的API。
 * {@link VarHandle} 可以与任何字段、数组元素或静态变量关联，支持在不同访问模型下对这些类型变量的访问
 *
 * {@link VarHandle}是对变量或参数定义的变量系列的动态强类型引用，包括静态字段，非静态字段，数组元素或堆外数据结构的组件
 * {@link VarHandle}对这些变量进行绑定，对这些变量进行操作
 *
 * {@link VarHandle}的四种共享内存访问模式：
 *     plain
 *     opaque
 *     release/acquire
 *     volatile
 * 又细分为以下模式：
 *
 *     写入反问模式：获取指定内存排序效果下的变量值，get, getVolatile, getAcquire, getOpaque
 *     读取访问模式：set, setVolatile, setRlease, setOpaque
 *     原子更新模式：比较并设置 *CompareAndSet*
 *     数值更新模式：以原子方式获取和设置
 *     按位原子更新模式：getAndBitwise*
 *
 * 内存屏障：
 *     fullFence
 *     acquireFence
 *     releaseFence
 *     loadLoadFence
 *     storeStoreFence
 *
 */
public class VarHandleDemo {

    private Point[] points;

    static class Point {
        public int publicVar = 1;
        protected int protectedVar = 2;
        private int privateVar = 3;
        public int[] arr = new int[] {1, 2, 3};
        volatile int v = 4;

        @Override
        public String toString() {
            return "Point{" +
                    "publicVar=" + publicVar +
                    ", protectedVar=" + protectedVar +
                    ", privateVar=" + privateVar +
                    ", arr=" + Arrays.toString(arr) +
                    ", v=" + v +
                    '}';
        }
    }

    public static void main(String[] args) throws Exception {
        updatePoint();
    }

    /**
     * {@link MethodHandles}.privateLookupIn(class, {@link MethodHandles}.lookup())获取私有变量的 {@link java.lang.invoke.MethodHandles.Lookup}
     * {@link MethodHandles}.lookup()获取protected，public的Lookup
     */
    private static void updatePoint() throws NoSuchFieldException, IllegalAccessException {
        Point point = new Point();
        System.out.println(point);

        VarHandle varHandle = MethodHandles.privateLookupIn(Point.class, MethodHandles.lookup()).findVarHandle(
                Point.class,
                "privateVar",
                int.class
        );

        varHandle.set(point, 3333);
        System.out.println(point);
        System.out.println();

        varHandle = MethodHandles.lookup().in(Point.class).findVarHandle(Point.class, "protectedVar", int.class);
        varHandle.set(point, 11);
        System.out.println(point);
        System.out.println();

        varHandle = MethodHandles.arrayElementVarHandle(int[].class);
        varHandle.compareAndSet(point.arr, 2, 3, 33);
        System.out.println(point);
    }
}
