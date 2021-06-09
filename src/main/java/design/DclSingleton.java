package design;

import java.io.InvalidObjectException;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.Objects;

@SuppressWarnings("all")
/**
 * 1. final: 防止子类破坏单例
 *
 * 2. Serializable: 如果实现了序列化,反序列化时会生成单例对象,此时的对象与原对象不是一样的对象了
 *      解决方法是 添加Object readResolve()方法
 *
 * 3. 设置为私有, 能否防止反射创建新的实例?
 *      不能防止反射创建新的实例
 *      可以在构造方法中抛出异常
 *
 * 4. 静态初始化能够保证单例对象创建时的线程安全(饿汉式)
 *      可以, 静态成员变量的初始化是类加载时进行的, 类加载时由jvm保证线程安全性
 *
 * 5. 为何要提供public的静态方法获取单利而不是直接将instance设置为public
 *      更好的封装性, 实现懒惰的初始化, 对创建单例对象进行更多的控制, 提供对泛型的支持, 成员变无法提供泛型支持
 *
 * 内部静态类实现单例:
 *  属于饿汉式单例, 因为类只有用到后才会加载
 *
 *  public final class Singleton {
 *
 *      private static class LazyHolder {
 *          static final Singleton instance = new Singleton();
 *      }
 *
 *      public static Singleton getInstance() {
 *          return LazyHolder.instance;
 *      }
 *  }
 */
public final class DclSingleton implements Serializable {

    private DclSingleton() throws InvalidObjectException {
        final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

        boolean methodRight = false;
        for (StackTraceElement stackTraceElement : stackTrace) {
            String signature = stackTraceElement.getClassName() + "." + stackTraceElement.getMethodName();
            if ("design.DclSingleton.getInstance".equals(signature)) {
                methodRight = true;
                break;
            }
        }
        if (!methodRight) {
            throw new IllegalAccessError("非法访问");
        }
    }

    private static volatile DclSingleton instance;

    public Object readResolve() {
        return instance;
    }

    public static DclSingleton getInstance() throws InvalidObjectException {
        // if (instance == null) 在monitor之外,可以越过monitor读取值,
        // 如果t1还没用将新实例化的对象构造方法执行完毕,那么t2读取到的是一个未初始化完毕的实例
        if (Objects.isNull(instance)) {
            // 首次访问会同步, 之后的使用不会使用synchronized
            synchronized (DclSingleton.class) {
                // synchronized不能阻止指令重排序
                // 只是synchronized代码块中 的代码不会有多线程下的 有序性问题
                // 防止两个线程实例化出 两个 实例
                if (Objects.isNull(instance)) {
                    instance = new DclSingleton();      // new 和 赋值 操作并非原子操作，可能发生指令重排序, synchronized 不能阻止指令重排序
                }
            }
        }
        return instance;
        /*
            读写屏障:
                更底层是读写变量时用lock指令来保证多核cpu之间的可见性与有序性

            -------------------------------------------------------> 加入对instance的读屏障
             0 getstatic #2 <design/DclSingleton.instance>   此行代码在monitor之外,因此可以越过monitor读取instance的值
             3 ifnonnull 37 (+34)                            此时t1还没有将构造方法执行完毕, 那么t2拿到的是一个未初始化的单例
             6 ldc #3 <mp/jmm/DclSingleton>   获得类对象
             8 dup
             9 astore_0
            10 monitorenter ---------------------------------------> 保证原子性,可见性
            11 getstatic #2 <design/DclSingleton.instance>
            14 ifnonnull 27 (+13)

            17 new #3 <design/DclSingleton>   创建对象,将对象引用入栈
            20 dup                            复制对象引用
            21 invokespecial #4 <design/DclSingleton.<init>>   利用对象引用,调用构造方法
            24 putstatic #2 <design/DclSingleton.instance>     利用对象引用,复制给静态instance
            --------------------------------------------------------> 加入对instance的写屏障

            27 aload_0
            28 monitorexit -----------------------------------------> 保证原子性,可见性
            29 goto 37 (+8)
            32 astore_1
            33 aload_0
            34 monitorexit
            35 aload_1
            36 athrow
            37 getstatic #2 <design/DclSingleton.instance>
            40 areturn

         */
    }

    public static void main(String[] args) throws InvalidObjectException {
        final DclSingleton instance = DclSingleton.getInstance();
        System.out.println(instance);
    }
}


class ReflectionTest {
    public static void main(String[] args) throws NoSuchMethodException {
        final Constructor<DclSingleton> declaredConstructor = DclSingleton.class.getDeclaredConstructor();
        declaredConstructor.setAccessible(true);
        try {
            final DclSingleton dclSingleton = declaredConstructor.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}