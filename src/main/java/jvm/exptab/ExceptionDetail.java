package jvm.exptab;

import java.util.concurrent.TimeUnit;

/**
 * {@link RuntimeException}需要try-cache-finally捕获
 *
 * try {
 *    待监控异常代码
 * } catch {
 *    指定异常类型，用来捕获该类型的异常
 *    多个catch块之间是互斥的，与switch类似，优先级是从上到下，只能选择其中一个
 * } finally {
 *    声明一段必须运行的代码
 * }
 *
 * 异常发生
 *     显示异常：throw关键字，主动将异常实例抛出
 *     隐式异常：不受开发者控制，触发主体是jvm，jvm在运行时遇到了无法继续执行的异常状态，需要显示捕获和捕获处理
 *
 *
 * 捕获并处理
 *
 * 1. try块未发生异常：如果有finally块，会在try块之后运行，catch块用不执行
 * 2. try块发生异常：检测异常类型，是否存在于catch块，存在则捕获，finally紧随其后，执行完毕，抛出异常
 * 3. catch或finally都发生异常：
 *      catch块发生异常，此时表现取决于finally代码块是否存在return语句，
 *          如果存在，则finally块代码块执行完毕直接返回
 *          如果不存在，finally执行完毕后，将catch中的异常抛出
 *      finally中有异常，中断finally块执行，抛出异常
 *
 * jvm如何处理异常：
 *     异常实例的构建，是非常耗费性能的。因为构造异常实例时，jvm必须要生成该异常的 异常栈
 *
 *     异常栈会逐一访问当前线程的java栈帧，以及各种调试信息，包括栈帧指向的方法名，方法所在类名，文件名以及代码位置，还得判断是否命中异常
 *
 * 异常表：
 *     每个方法都会有一个异常表
 *
 *     命中异常：
 *          异常一经发生，jvm会从上到下遍历异常表所有记录，当发现触发异常的字节码的索引值，在某个异常表中某个异常的监控范围，jvm会
 *          判断所抛出的异常和监听的异常类型，是否匹配，能匹配上，jvm将控制流转向至处理此异常的target
 *
 *          如果没有找到异常处理器，则弹出当前方法对应的java栈帧，回到调用者，并重复此过程
 *
 *          最坏情况下，jvm会遍历当前线程java栈上所有的方法异常
 *
 * finally块：
 *     编译器会将finally块复制几份，分别放在所有可能执行的代码路径的出口
 */
public class ExceptionDetail {
    public static void main(String[] args) {
//        int i = tryCacheFinallyReturn();
//        System.out.println(i);

//        throwable();

        exceptionTable();

//        types();

//        performance();

//        async();
    }

    /**
     *  如果try中的方法运行在另一线程，其中的异常无法通过try - cache - finally捕获
     */
    private static void async() {
        int sum = 123;
        try {
            new Thread(() -> {
                int a = sum / 0;
                System.out.println(a);
            }, "t").start();
        } catch (Exception e) {
            System.out.println("计算异常");     // 此处无法捕捉到线程t中的 ArithmeticException
            /**
             * Exception in thread "t" java.lang.ArithmeticException: / by zero
             *                      * 此处是线程 t，不是main
             * 	at jvm.exptab.ExceptionDetail.lambda$async$0(ExceptionDetail.java:57)
             * 	at java.base/java.lang.Thread.run(Thread.java:832)
             */
//            e.printStackTrace();
        }
        try {
            TimeUnit.MILLISECONDS.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("main");
    }

    /**
     * 异常的性能：具有争议性
     * 没有异常时，try - cache对性能几乎没影响，但是一旦发生异常，会大大影响性能
     *
     * 爬栈是异常开销大的主要原因：
     *    有的项目有大量的基于业务定义的Exception类，throw Exception非常常见，如果要提高性能，
     *    此时应该重载 {@link Throwable#fillInStackTrace()}方法，但是重载该方法带来很多弊端
     *    可以动态决定是否需要异常栈，{@link Throwable#} writableStackTrace
     *
     * 滥用异常来实现某些特殊控制流，如果是异常本身没用，只有类型和抛出的控制流有用，应该重载 {@link Throwable#fillInStackTrace()}
     *
     * HotSpot优化： fast throw,如果某些隐式异常被抛出多次，C2编译器会使用fast throw优化该异常，提高性能
     */
    private static int count = 1000000000;
    private static void performance() {
        long c = System.currentTimeMillis();
        noException();
        long e = System.currentTimeMillis();
        System.out.println("no: " + (e - c));   // 3

        c = System.currentTimeMillis();
        withException();
        e = System.currentTimeMillis();
        System.out.println("yes: " + (e - c));  // 832
    }

    private static void noException() {
        int sum = 0;
        for (int i = 0; i < count; i++) {
            sum++;
            try {
                int a = sum / 1;
            } catch (Exception e) { }
        }
    }

    private static void withException() {
        int sum = 0;
        for (int i = 0; i < count; i++) {
            sum++;
            try {
                int a = sum / 0;
            } catch (Exception e) { }
        }
    }

    /**
     * 异常类型：
     *
     *  {@link Throwable}:
     *      {@link Error}: 表名执行状态已经无法恢复，需要终止线程甚至虚拟机，这种错误不应该被捕获
     *      {@link Exception}：需要捕获的异常
     *      {@link RuntimeException}：运行时错误
     *
     *  {@link Error}和 {@link RuntimeException}属于非检查异常
     *
     *  检查异常如 {@link java.io.IOException}需要显示捕获，这些异常是在编译期间检查的，如果不按照此规范处理，无法通过编译
     */
    private static void types() {

    }


    private static int test() {
        return 1 / 0;
    }

    /**
     * 异常表:
     *
     * Code:
     *  0 invokestatic #12 <jvm/exptab/ExceptionDetail.test>
     *  3 istore_0
     *  4 getstatic #16 <java/lang/System.out>
     *  7 iload_0
     *  8 invokevirtual #22 <java/io/PrintStream.println>
     * 11 getstatic #16 <java/lang/System.out>
     * 14 ldc #28 <finally>
     * 16 invokevirtual #30 <java/io/PrintStream.println>
     * 19 goto 49 (+30)
     * 22 astore_0
     * 23 aload_0
     * 24 invokevirtual #35 <java/lang/Exception.printStackTrace>
     * 27 getstatic #16 <java/lang/System.out>
     * 30 ldc #28 <finally>
     * 32 invokevirtual #30 <java/io/PrintStream.println>
     * 35 goto 49 (+14)
     * 38 astore_1
     * 39 getstatic #16 <java/lang/System.out>
     * 42 ldc #28 <finally>
     * 44 invokevirtual #30 <java/io/PrintStream.println>
     * 47 aload_1
     * 48 athrow
     * 49 return
     *
     * Exception table:
     *
     *      Start PC    End PC    Handler PC            Catch Type
     *  0	  0	        11	        22	                cp_info #33 java/lang/Exception
     *  1  	  0	        11	        38	                cp_info #0  any     命中any后，因为没有对应的异常处理器，继续向上抛出，交由该方的调用者
     *  2	  22	    27	        38	                cp_info #0  any
     *
     *  异常表：
     *      1. jvm会在当前的异常表中，查找出是否有合适的处理者
     *      2. 如果当前方法异常表不为空，并且异常符合form和to节点，且type也匹配，则jvm调用位于target的调用者来处理
     *      3. 未找到则继续查找
     *      4. 如果当前异常表无法处理，则向上查找（弹栈）本方法的调用方法的调用处，重复上面的操作
     *      5. 如果所有栈帧被弹出，仍然没有处理，则抛给当前Thread，thread终止
     *      6. 如果当前Thread为最后一个非守护进程，且未处理异常，则导致jvm终止
     */
    private static void exceptionTable() {
        try {
            int test = test();
            System.out.println(test);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("finally");
        }
    }

    /**
     * {@link Throwable}： 是 {@link Error}和 {@link Exception}的接口
     * {@link Exception}：
     *     CheckedException:
     *         {@link java.io.IOException}，需要显示处理
     *     UncheckedException:
     *         {@link Error} 或 {@link RuntimeException}, 不强制在调用处进行处理，但也可以
     */
    private static void throwable() {

    }

    /**
     * finally 中的return优先级最高，finally中的return会破坏并阻止异常抛出，导致不已排查的崩溃
     */
    private static int tryCacheFinallyReturn() {
        try {
//            int i = 1 / 0;  // 无论是否有一场都返回 3
            return 1;           // 没有异常，此处优先级次之     ②
        } catch (Exception e) {
            return 2;           // 有异常，此处优先级次之
        } finally {
            int i = 1 / 0;
            return 3;           // 此处优先级最高             ①
        }
    }
}

