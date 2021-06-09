package lang.invoke;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;

public class MethodHandleDemo {

    public static void main(String[] args) {
//        type();
        invokeMethod();
    }

    static class Varargs {
        public void normalMethod(String str, int start, int... numbers) {
            StringBuilder s = new StringBuilder();
            s.append(str);
            s.append(start);
            for (int number : numbers) {
                s.append(number);
            }
            s.append("啊加上看到了伐就快乐萨芬");
            System.out.println(s);
        }
    }

    /**
     * 方法句柄的使用类似于 {@link Method}，但是灵活性更高
     *
     * {@link java.lang.invoke.MethodHandles.Lookup#findVirtual(Class, String, MethodType)}
     * virtualMethod: java中的普通方法，动态绑定(动态分配)是java的默认行为，动态绑定是为了多态而出现的
     *
     * 方法重写形成了动态方法调度的基础，动态方法调度可以说是java中最强大的功能，保证在运行时，而不是编译时解析对方法的调用
     *
     * 超类引用变量可以指向子类对象，java利用这一点，在运行时解析对重写方法的调用：
     *     当超类引用调用重写的方法时，java根据在调用时所引用对象的类型来判断调用哪个版本的方法，该决定在运行时做出，
     *     是当前 正在引用的对象类型，而不是 引用变量类型决定了将要执行那个版本的重写方法
     */
    private static void invokeMethod() {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        MethodType type = MethodType.methodType(String.class, int.class, int.class);
        MethodHandle method;
        try {
            method = lookup.findVirtual(String.class, "substring", type);
            // System.out.println(method);
            /**
             * {@link MethodHandle#invokeExact(Object...)}  要求精确地类型匹配
             * {@link MethodHandle#invoke(Object...)} 相对于 {@link MethodHandle#invokeExact(Object...)}更加宽松
             *
             * 类型转换原则：
             * 1）可以通过Java的类型转换来完成，一般是从子类转换成父类，接口的实现类转换成接口，比如从String类转换到Object类。
             * 2）可以通过基本类型的转换来完成，只能进行类型范围的扩大，比如从int类型转换到long类型。
             * 3）可以通过基本类型的自动装箱和拆箱机制来完成，比如从int类型到Integer类型。
             * 4）如果S有返回值类型，而T的返回值是void，S的返回值会被丢弃。
             * 5）如果S的返回值是void，而T的返回值是引用类型，T的返回值会是null。
             * 6）如果S的返回值是void，而T的返回值是基本类型，T的返回值会是0。
             */
//            String str = (String) method.invokeExact("hello, world", 1, 3);
//            System.out.println(str);
//            String str1 = (String) method.invoke("hello, World", 0, 2);
//            System.out.println(str1);
//            String s = (String) method.invokeWithArguments("hello, world", 1, 3);
//            System.out.println(s);
            /**
             * 可变参数的方法句柄
             * normalMethodz最后一个参数是int类型的数组，引用他的方法句柄通过 asVarargsCollector 方法转换之后，得到新的方法句柄在调用时就可以使用长度可变参数的语法格式，而不需要原始的数组格式
             */
            MethodHandle normalMethod = lookup.findVirtual(
                    Varargs.class,
                    "normalMethod",
                    MethodType.methodType(void.class, String.class, int.class, int[].class)
            );
            MethodHandle methodHandle = normalMethod.asVarargsCollector(int[].class);
            methodHandle.invoke(new Varargs(), "Hello", 1, 2, 3, 4, 5, 6);

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 1. 类型：类型由参数类型和返回值决定，如 {@link java.lang.String#length()}与 {@link java.lang.Integer#intValue()}的返回值类型参数都一样，是同一个类型
     *    类型由 {@link java.lang.invoke.MethodType}定义， 是否相等：{@link java.lang.invoke.MethodType#equals(java.lang.Object)}
     */
    private static void type() {
        /**
         *  {@link String#length()}
         */
        MethodType m1 = MethodType.methodType(int.class);
//        System.out.println(m1);
        /**
         * {@link String#concat(String)}
         */
        MethodType m2 = MethodType.methodType(String.class, String.class);
//        System.out.println(m2);
        /**
         * {@link String#getChars(int, int, char[], int)}
         */
        MethodType m3 = MethodType.methodType(void.class, int.class, int.class, char[].class, int.class);
//        System.out.println(m3);
        /**
         * {@link String#startsWith(String)}
         */
        MethodType m4 = MethodType.methodType(boolean.class, m2);   // 使用m2的引用

        /**
         * 通用 {@link MethodType}类型,即返回值和类型都是 {@link Object}
         */
        MethodType m5 = MethodType.genericMethodType(3);    // Object类型参数个数
        MethodType m6 = MethodType.genericMethodType(2, true);  // 2个类型为Object的参数和后面的Object[]类型参数

        /**
         * {@link MethodType#fromMethodDescriptorString(String, ClassLoader)} 允许指定方法类型在字节码中的表示形式
         */
        ClassLoader cl = MethodHandleDemo.class.getClassLoader();
        // System.out.println(cl);
        String descriptor = "(Ljava/lang/String;)Ljava/lang/String;";
        MethodType m7 = MethodType.fromMethodDescriptorString(descriptor, cl);
        // 等同于
        // MethodType methodType = MethodType.methodType(String.class, String.class);
//        System.out.println(m7);


        ////////////////////////////////////////////////////////////////////////////
        /**
         * {@link MethodType}增删改
         */
        MethodType mt = MethodType.methodType(String.class, int.class, int.class);
        System.out.println(mt);
        mt = mt.appendParameterTypes(float.class);
        System.out.println(mt);
        mt = mt.insertParameterTypes(1, double.class, long.class);
        System.out.println(mt);
        mt = mt.dropParameterTypes(2, 4);
        System.out.println(mt);
        mt = mt.changeReturnType(void.class);
        System.out.println(mt);

        System.out.println();
        /**
         * {@link MethodType} 一次性修改
         */
        MethodType mt1 = MethodType.methodType(Integer.class, int.class, double.class);
        System.out.println(mt1);
//        mt1 = mt1.wrap();
//        System.out.println(mt1);
//        mt1 = mt1.unwrap();
//        System.out.println(mt1);
//        mt1 = mt1.generic();
//        System.out.println(mt1);
        mt1 = mt1.erase();
        System.out.println(mt1);


    }
}
