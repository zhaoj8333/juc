package thrd.pool.locality;

//import jdk.internal.vm.annotation.Contended;

/**
 * 处理伪共享的两种方式：
 *     增大数组元素的间隔，使得不同线程存取的元素位于不同的cache line上，典型的空间换时间, linux cache机制
 *
 *     每个线程中创建全局数组本地拷贝，结束后再写回全局数组
 */
public class FalseSharingHandling {

    /**
     * 如果Data对象所有的变量都加在到L1缓存的一个缓存行中，高并发下不作任何措施，
     * 其中任何一个变量被修改，其他CPU内核的L1缓存都被置为 Invalid，
     * 再次访问只能从内存重新读取
     */
    static class Data {
        int value;

        char key;
        long createTime; //

        long modifyTime;
        boolean flag;
    }

    /**
     * 将对象属性分组，将一起变化的放一组，与其他属性无关的放一组
     * jdk1.8之前，一般在属性之间增加长整形变量分隔属性
     */
    static class DataPadding {
        long a1, a2, a3, a4, a5, a6, a7, a8; // 防止与前一个对象产生伪共享
        int value;

        long modifyTime;
        long b1, b2, b3, b4, b5, b6, b7, b8;
        boolean flag;

        long c1, c2, c3, c4, c5, c6, c7, c8;
        long createTime; //
        char key;
        long d1, d2, d3, d4, d5, d6, d7, d8;
    }

    /**
//     * {@link Contended}
     */
//    @Contended  // 类加contended注解表名每个变量都在独立的cache line中
//    @SuppressWarnings("restriction")
//    static class ContendedData {
//
//        @Contended("group1")
//        int value;
//
//        @Contended("group1")
//        char key;
//
//        @Contended("group2")
//        long createTime; //
//
//        @Contended("group2")
//        long modifyTime;
//
//        @Contended("group3")
//        boolean flag;
//    }

    /**
     * 示例：
     *
     *  {@link java.util.concurrent.ConcurrentHashMap.CounterCell}
     *
     *  {@link Thread}:
     *      @jdk.internal.vm.annotation.Contended("tlr")
     *      long threadLocalRandomSeed;
     *
     *      @jdk.internal.vm.annotation.Contended("tlr")
     *      int threadLocalRandomProbe;
     *
     *      @jdk.internal.vm.annotation.Contended("tlr")
     *      int threadLocalRandomSecondarySeed;
     */
}
