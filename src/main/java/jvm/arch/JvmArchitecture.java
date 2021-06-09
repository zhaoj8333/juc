package jvm.arch;

import jdk.internal.org.objectweb.asm.Opcodes;
import lang.jmx.JmxUtil;

import java.util.*;
import java.util.concurrent.TimeUnit;
import jdk.internal.org.objectweb.asm.ClassWriter;

/**
 * JVM体系结构
 *
 * Java Class -------------> {@link ClassLoader}
 *                                 |    |
 *                                \/   \/
 * +=====================================================================================================+
 * || +---------------+  +---------------+ +---------------+ +---------------+ +----------------------+ ||
 * || | Method Area   |  |     Heap      | |  JVM stacks   | | PC register   | | Native Method Stacks | || JVM内存结构
 * || |   方法区       |  |      堆       |  |  虚拟机栈      | |  程序计数器     | |       本地方法栈       | ||
 * || +---------------+  +---------------+ +---------------+ +---------------+ +----------------------+ ||
 * +=====================================================================================================+
 *                  |   /\                                                      |  /\
 *                 \/   |                                                       |  |
 * +===========================================================+                |  |
 * || +---------------+ +---------------+ +---------------+   ||               \/  |
 * || |  Interpreter  | | JIT Compiler  | |     GC        |   || -------->  本地方法接口
 * || |    解释器      | |   即时编译器    | |   垃圾回收     |    || <--------
 * || +---------------+ +---------------+ +---------------+   ||  JVM执行引擎
 * +==========================================================+
 *
 * 线程私有内存区域：
 *     程序计数器，java虚拟机栈，本地方法栈 是线程私有的，与java线程同时创建，与线程有相同的生命周期
 *
 * 虚拟机栈描述的是 java方法  == 执行的内存模型： 每一个方法在执行的时候同时都会创建一个栈帧
 *
 * 虚拟机栈帧存储：
 *     栈帧的分配存在于方法表的code属性中，栈帧的内存分配不会受到运行期变量数据的影响，而仅仅取决于具体虚拟机的表现
 *     对于执行引擎来说，活动线程中虚拟机栈顶的栈帧才是有效的，成为当前栈帧，对应的方法为当前方法。
 *
 *     局部变量表：
 *         基本类型：八种基本数据类型
 *         对象引用：reference，可能是指向起始对象地址的指针，也有可能是指向一个代表对象的句柄
 *         returnAddress类型：指向字节码地址
 *         其中，long，double占用两个局部变量槽Slot的空间，
 *         64长度类型占2个，jvm会以高位对齐方式为其分配两个 连续 的Slot空间，相当于把long和double数据类型读写分隔成两次32位读写
 *         reference类型虚拟机规范没有确切说明长度，但虚拟机实现至少应该能从次引用中直接或间接找到查找对象在java堆中的起始地址和方法中的对象类型数据
 *         局部变量表所需内存在编译期间完成分配，进入方法时，该方法在栈中分配多大空间是完全确定的，运行期间不会改变
 *         Slot可重用，当Slot变量超出作用域，下一次分配slot时将会被覆盖，Slot引用的对象会影响GC
 *         局部变量不会被赋予初始值，不同于类变量，不存在类变量那样的准备阶段
 *
 *             byte[] holder = new byte[1024 * 1024 * 10];
 *             System.gc();
 *             // holder在gc后未被回收，原因在于局部变量表还有holder的引用
 *
 *         方法参数，内部定义的局部变量，bytecode中code属性中有一个max_locals确定该方法需要分配的最大局部变量表的容量
 *
 *     操作数栈：
 *         操作数栈在编译期就已经确定
 *         操作数栈每个元素是任意的java类型，32位占用栈容量为1，64位为2
 *         执行过程中，有不断的出栈，入栈操作
 *         栈帧之间是独立的，但是实现上会有重叠，公用参数，不做额外的参数复制操作
 *         Class的Code属性中max_stacks指定了执行中的最大栈深度，jvm的执行引擎是基于栈的执行引擎，这里的栈指操作数栈
 *         操作数栈的每个位置上可以保存一个jvm定义的任意数值类型的值
 *         iload_0  // 将局部变量表0号索引的值入操作数栈
 *         iload_1
 *         iadd     // 操作数栈去除前两位相加，放入栈顶
 *         istore_2 // 操作数栈顶元素出栈，放入局部变量表2号索引
 *
 *         * 操作数栈是方法执行算数运算或调用其他方法进行参数传递的时的媒介，这就是 --- 基于栈的执行引擎
 *         * 操作数栈的元素类型必须与字节码指令序列严格匹配，如不能用iad指令去加两个long类型的数据，这些基本数据类型的校验会在编译期完成
 *
 *     动态链接：
 *         每个栈帧都包含一个指向当前方法所在类型的运行时常量池的引用，持有这个引用是为了支持方法调用过程中的动态链接。
 *         指向常量池中栈帧所属方法的引用，字节码中的方法调用指令就以常量池中的方法符号作为参数。
 *         这些引用一部分在类加载阶段或第一次使用时就化为直接引用，成为静态解析。
 *         另一部分在运行期间转化为直接饮用，成为动态链接。
 *
 *     方法出口：
 *         方法执行完后，有两种方式退出方法：
 *             方法正常返回：传递给上层的调用者，是否具有返回值和返回值类型将根据遇到何种方法来返回指令决定。
 *             方法遇到异常：无论是jvm中的异常还是代码异常，只要在方法的异常表中没有搜索到异常处理器，就会导致方法退出，不会给调用者任何返回值
 *                        无论是何种方式退出方法，都要返回到方法被调用的位置，程序才能继续执行。
 *                        方法返回时会在栈帧中保存一些信息，用来恢复上层方法的执行状态，
 *             方法返回的操作：
 *                        恢复上层方法的局部变量表和操作数栈
 *                        把返回值压入调用者栈帧的操作数栈
 *                        调整PC计数器的值以指向方法调用指令的后一条指令
 *
 * HotSpot虚拟机直接就把本地方法栈和虚拟机栈合二为一。
 */
public class JvmArchitecture extends ClassLoader {

    public static void main(String[] args) throws InterruptedException {
//        StringBuilder sb = new StringBuilder();
//        sb.append(4); sb.append(5); sb.append(6);
//        new Thread(()-> m2(sb)).start();
//        try {
//            stackOverFlowError();
//        } catch (Error e) {
//            System.out.println("deep of calling: " + i);
//        }
//        detectDeadlock();
//        detectCpuHigh();
//        heapOverflow();
//        methodArea();
        metaspaceOverflow();
//        constantPool();
//        stackGc();
//        stackGc1();
    }

    /**
     *
     *
     * reference:
     *     对象实例引用，jvm没有规定 长度：32或64，但是需要保证 从此引用中查找到对象在java堆中的数据存放地址索引；
     *     引用中查找到对象所属数据类型在方法中的存储类型信息，否则无法实现java语言规范中定义的语法约束
     *
     * returnAddress: 指向一条字节码指令的地址
     *     jsr, jsr_w,ret
     *     异常处理，现已用 异常表 代替
     *
     * 对于64位的数据，jvm会以高位对齐的方式分配两个连续的Slot空间
     *     局部变量表建立在线程的堆栈上，是线程私有的数据，无论读写两个连续的Slot是否为原子操作，都不会引起线程安全问题
     *
     * jvm使用索引定位的方式使用局部变量表(0-最大Slot)
     *     32位 n
     *     64位 n,n+1,不允许采用任何方式单独访问其中的某一个，否则在类加载时抛异常
     *
     * 方法执行时，jvm使用局部变量表完成参数值到参数变量列表的传递过程
     *     非static方法：
     *          0：this
     *          其余按照参数表顺序排列
     *          再分配方法内的变量（顺序，作用域）
     *
     * 
     */

    /**
     * 成员变量可以不进行初始化，jvm会确保它有一个默认值
     */
    private int globalOne;
    private static int staticOne;

    private static void stackGc() {
        //
        System.out.println(staticOne); // 全局变量无需初始

        ////////////////////////////////////////////////////////////
        /**
         * 局部变量为何必须初始化:
         *     jvm类加载：
         *         静态成员变量：会初始化两次，第一次在准备阶段，进行初始化，赋默认值，放入方法区；第二次在初始化阶段，根据赋值情况再次进行初始化；
         *         非静态成员变量：仅在初始化阶段赋值，根据赋值情况，代码不赋值就赋值默认值，有则等于代码中的值，该变量会分配到 堆 中；
         *              新建对象时，java在heap中申请内存以存放类数据，成员变量就是类数据，只需jvm在申请内存时把整个位置置为零即可完成初始化
         *
         *     局部变量，是在字节码执行时，才会被运行到，此时局部变量在虚拟机栈 局部变量表 中，局部变量定义但是没有赋值是不能使用的
         *              局部变量位于栈中，当然栈也可以初始化，但是对于某些变量一开始是没有的，有些在循环中的局部变量要反复声明多次，有一些局部变量
         *              作用域结束后，另一个局部变量会占用这个局部变量的位置，那初始化放在何时呢，可以初始化多次，但是会带来性能问题。
         *              编译器当然可以为局部变量赋一个默认值，但是未初始化的局部变量更有可能是程序员的疏忽，所以采用默认值会掩盖这种错误。强制提供初始值，能帮助找出程序里面的缺陷。
         *              从技术上讲：局部变量总量达，生命周期短，jvm进行初始化开销很大；
         *              业务上讲：局部变量一般用于实际运算，很少用到默认值，赋值意义不大
         *              编程思想上讲：局部变量不初始化，而是报错题型，有助于开发者减少开发中出现的缺陷
         */
//        Object obj;
//        System.out.println(obj);
//        int a;
//        System.out.println(a);
        ////////////////////////////////////////////////////////////
        byte[] holder = new byte[1024 * 1024 * 1023];
        System.gc();
        // 此处holder及其引用不会被回收
        try {
            TimeUnit.SECONDS.sleep(15);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void stackGc1() {
        // 控制恰当的作用域来控制变量回收时间
        // 如果经过jit优化，无序手动赋值null就可以被回收
        {
            byte[] holder = new byte[1024 * 1024 * 1023];
            holder = null;
        }
        System.gc();
        try {
            TimeUnit.SECONDS.sleep(15);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    /**
     * 程序计数器：
     *     记住下一条JVM指令的执行地址，程序计数器的实现通过寄存器来进行
     * 线程私有
     */
    private static void pc() {}

    /**
     * 虚拟机栈：
     *     后进先出
     * 线程私有：
     *     虚拟机栈即线程运行所需内存，栈由多个栈帧组成，对应着每次方法调用所占据的内存
     *     每个线程只能有一个活动栈帧，对应着当前正在执行的那个方法（栈顶）
     * gc不涉及栈帧
     * 栈内存大小：-Xss size,占内存较大一般是可以进行较多次的方法递归调用，栈内存较大可能会使线程数量减少，同时容易造成 {@link StackOverflowError}
     * 栈的线程安全问题：共享的变量要考虑线程安全问题，局部变量也不能保证是线程安全的，要看此变量是否逃离了方法作用的范围
     *
     * {@link StackOverflowError}:
     *     递归调用，栈帧过大导致
     *
     * -Xss越大，每个线程大小越大，占用内存越多，容纳线程数越小
     * -Xss越小，递归深度越小，容易出现栈溢出
     * 减少局部变量声明，可以节省栈帧大小，增加调用深度
     */
    static int x = 0; // 此时线程不安全
    private static void stack() {
        int x = 0;  // x线程安全
        for (int i = 0; i < 5000; i++) x++;
        System.out.println(x);
    }

    /**
     * 堆和方法区：是线程公用的区域
     * 通过new关键字创建的对象除了由于逃逸分析而在栈上分配或TLB分配的外，会分配到heap空间
     * 堆必须考虑线程安全问题
     * 堆有垃圾回收机制，不再被引用的对象会被回收
     *
     * heap诊断工具：
     *  jps,
     *  jmap,
     *  jconsole
     */
    private static void heap() {}

    /**
     * java.lang.OutOfMemoryError: Java heap space
     */
    private static void heapOverflow() {
        String runtime = JmxUtil.getRuntime();
        System.out.println(runtime);

        try {
            List<String> list = new ArrayList<>();
            String a = "hello";
            while (true) {
                list.add(a);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
         * 本地方法栈
         */
    private static void nativeStack() {

    }

    private static int i = 0;

    /**
     * 不同平台的栈大小：
     * Linux IA32       128 KB
     * Windows x86_64   128 KB
     * Linux x86_64     256 KB
     * Windows IA64     320 KB
     * Linux IA64       1024 KB (1 MB)
     * Solaris Sparc    512 KB
     *
     * 正常栈大小情况下为21882次
     *
     * -Xss144k 543
     * -Xss288k 3056
     * -Xss512k 9212
     *
     * java -XX:+PrintFlagsFinal  | grep -i stack
     *      intx CompilerThreadStackSize                  = 1024                                   {pd product} {default}
     *      intx ThreadStackSize                          = 1024                                   {pd product} {default}
     *      intx VMThreadStackSize                        = 1024                                   {pd product} {default}
     */
    private static void stackOverFlowError() {
        i++;
        stackOverFlowError();
    }

    private static void constantPool() {
        String laugh = "哈哈哈哈哈哈";
        System.out.println(laugh);
    }

    /**
     * -XX: MaxMetaspaceSize = 20 m
     * Error occurred during initialization of VM
     * MaxMetaspaceSize is too small.
     *
     * 1.8以前报错为:
     *     OutOfMemoryError: PermGen space
     *
     * 动态加载类 场景:
     *     spring, mybatis 会大量使用cglib动态代理类
     *
     * Exception in thread "main" java.lang.OutOfMemoryError: Metaspace
     * 	at java.base/java.lang.ClassLoader.defineClass1(Native Method)
     * 	at java.base/java.lang.ClassLoader.defineClass(ClassLoader.java:1016)
     * 	at java.base/java.lang.ClassLoader.defineClass(ClassLoader.java:877)
     * 	at jvm.JvmArchitecture.metaspaceOverflow(JvmArchitecture.java:167)
     * 	at jvm.JvmArchitecture.main(JvmArchitecture.java:144)
     *
     * 元空间与永久代：
     *     元空间使用本地内存：当遇到 {@link OutOfMemoryError}:PermGen Space，将不会存在，此时类的元数据分配只受到本地内存的限制
     *         字符串常量池存于永久代，容易出现性能问题和内存溢出
     *         类方法信息大小难以确定，给永久代的大小指定很困难，
     *     永久代使用的是jvm内存：
     */
    private static void metaspaceOverflow() {
        int j = 0;
        try {
            JvmArchitecture mo = new JvmArchitecture();
            for (int i = 0; i < 100000; i++, j++) {
                final ClassWriter cw = new ClassWriter(0);
                cw.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC, "Class" + i, null, "java/lang/Object", null);
                final byte[] code = cw.toByteArray();
                // 执行类加载
                mo.defineClass("Class" + i, code, 0, code.length);
            }
        } finally {
            System.out.println(j);  // 20M 22456
        }
    }

    /**
     * 方法区(永久代)，存储已被虚拟机加载的：
     *     类信息:
     *
     *     常量:
     *     静态常量:
     *     jit编译数据:
     *
     * 方法区是一片连续的堆空间，1.8之前超过了-XX:PermSize所设置的大小时会报OOM:PermGen错误
     *
     * 被元空间代替：
     *     随着类加载越来越多，这块内存变得不可控，设置小了容易导致内存溢出，设置大了又浪费内存
     *
     * 方法区类似于OS进程概念中的 text区域，用于存储class结构，runtime常量池，字段，方法数据，方法代码，构造方法，特殊方法
     * 方法区的创建是在jvm启东时进行的，
     *
     * 方法区与永久代：
     *     本质上，两者不等价，仅仅是因为hotspot把GC分代收集扩展至方法区，或者使用永久代实现方法区，这样Hotspot可以像管理堆一样管理这一块内存，省去额外工作。
     *     对于其他虚拟机如J9，JRocket不存在永久代的概念
     *     如何实现方法区属于虚拟机实现，不受虚拟机规范约束
     *
     *     使用永久代来实现方法区现在看来并不是一个好主意，因为这样更容易遭受内存溢出
     *
     *     对于Hotspot，官方路线图中已经放弃永久代逐步采用Native Memory来实现方法区，jdk1.8后成为元空间，用的是OS内存
     *
     *     和堆一样，不需要连续内存，可扩展，还可以不实现垃圾收集，相对而言，垃圾收集在这个区域较少出现，但并不是"永久"存在，
     *
     *     方法区的垃圾收集目标主要是对常量池的回收和对类的卸载，但是回收成绩难以令人满意，尤其是类的卸载条件相当苛刻，但是回收也非常必要。
     *     方法区无法满足内存分配需要时，将抛出 {@link OutOfMemoryError}
     *
     * -XX:MetaspaceSize
     *
     */
    private static void methodArea() {

    }

    static JvmArchitecture a = new JvmArchitecture();
    static JvmArchitecture b = new JvmArchitecture();

    /**
     * 死锁检测：
     *  jconsole
     *
     *  jstack
     *       Found one Java-level deadlock:
     *       =============================
     *       "t1":
     *       waiting to lock monitor 0x00007fb14000ff00 (object 0x000000061fdda9c8, a jvm.JvmArchitecture), which is held by "t2"
     *
     *       "t2":
     *       waiting to lock monitor 0x00007fb14000fe00 (object 0x000000061fdda9b8, a jvm.JvmArchitecture), which is held by "t1"
     *
     *       Java stack information for the threads listed above:
     *       ===================================================
     *       "t1":
     *       at jvm.JvmArchitecture.lambda$detectDeadlock$0(JvmArchitecture.java:144)
     *       - waiting to lock <0x000000061fdda9c8> (a jvm.JvmArchitecture)
     *       - locked <0x000000061fdda9b8> (a jvm.JvmArchitecture)
     *       at jvm.JvmArchitecture$$Lambda$14/0x0000000800b8d248.run(Unknown Source)
     *       at java.lang.Thread.run(java.base@15.0.1/Thread.java:832)
     *       "t2":
     *       at jvm.JvmArchitecture.lambda$detectDeadlock$1(JvmArchitecture.java:152)
     *       - waiting to lock <0x000000061fdda9b8> (a jvm.JvmArchitecture)
     *       - locked <0x000000061fdda9c8> (a jvm.JvmArchitecture)
     *       at jvm.JvmArchitecture$$Lambda$17/0x0000000800b8d468.run(Unknown Source)
     *       at java.lang.Thread.run(java.base@15.0.1/Thread.java:832)
     *
     *       Found 1 deadlock.
     */
    private static void detectDeadlock() throws InterruptedException {
        new Thread(() -> {
            synchronized (a) {
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (b) {
                    System.out.println("a & b");
                }
            }
        }, "t1").start();
        TimeUnit.SECONDS.sleep(1);
        new Thread(() -> {
            synchronized (b) {
                synchronized (a) {
                    System.out.println("a & b");
                }
            }
        }, "t2").start();
    }

    /**
     * cpu占用高的问题：
     *  top命令 找到 pid
     *  jstack pid 输出可查看到
     *      java.lang.Thread.State: RUNNABLE
     *      cpu=xxxx ms, 值越大占用cpu时间越长，可以看到具体的代码位置和行号
     */
    private static void detectCpuHigh() {
        while (true) {}
    }

    public static void m1() {
        StringBuilder sb = new StringBuilder(); // 线程安全，因为是私有变量
        sb.append(1); sb.append(2); sb.append(3);
        System.out.println(sb.toString());
    }

    public static void m2(StringBuilder sb) {   // 线程不安全，因为是方法参数，其他线程也会访问到它
        sb.append(1); sb.append(2); sb.append(3);
        System.out.println(sb.toString());
    }

    public static StringBuilder m3() {
        StringBuilder sb = new StringBuilder(); sb.append(1); sb.append(2); sb.append(3);
        return sb;      // 线程不安全，返回后其他线程也会拿到该变量
    }

}
