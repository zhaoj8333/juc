package design;

/**
 * 内部静态类 实现的单例
 *   1. 属于懒汉式
 *      只有外部类被使用时,才会触发内部类的加载, 也才会触发内部对外部类的实例化
 *
 *   2. 是否有并发问题:
 *      没有并发问题
 *
 */
public class InnerClassSingleton {

    private InnerClassSingleton() { }

    private static class LazyHolder {
        static final InnerClassSingleton instance = new InnerClassSingleton();
    }

    public static InnerClassSingleton getInstance() {
        return LazyHolder.instance;
    }

    public static void main(String[] args) {
        final InnerClassSingleton instance = InnerClassSingleton.getInstance();
        System.out.println(instance);
    }
}