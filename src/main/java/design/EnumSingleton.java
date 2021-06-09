package design;

import java.io.*;
import java.lang.reflect.Constructor;

/**
 * 枚举单例:
 *
 *     1. 枚举单例如何限制实例个数
 *          枚举定义的对象,有几个对象以后就有几个对象, 相当于枚举类的静态成员变量
 *
 *     2. 枚举单例创建时是否有并发问题
 *          没有. 枚举单例属于静态成员, 类加载时即初始化
 *
 *     3. 枚举单例能够被反射破坏单例
 *          不能, 不支持
 *
 *     4. 枚举单例能不能被反序列化破坏单例
 *          枚举类默认都是实现了序列化接口的, 但在设计时考虑到了该问题, 因此无需额外担心被反序列化破坏
 *
 *     5. 枚举单例属于懒汉式还是饿汉式
 *          饿汉式
 *
 *     6. 枚举单例如果希望加入一些单例对象时初始化逻辑如何做
 *          加一个构造方法即可
 */
@SuppressWarnings("all")
public enum EnumSingleton implements Serializable {
    INSTANCE1, INSTANCE2, INSTANCE3;

    private EnumSingleton() {
    }
//     相当于
//    public static final EnumSingleton INSTANCE1;
//    public static final EnumSingleton INSTANCE2;
//    public static final EnumSingleton INSTANCE3;
//    public static EnumSingleton[] values();
//    public static EnumSingleton valueOf(String s);
//    static {}

    public static EnumSingleton getInstance() {
//        System.out.println("enum singleton");
        return INSTANCE1;
    }

    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public static void main(String[] args) throws Exception {
//        testReflection();
        testEnum();
    }

    private static void testEnum() throws IOException, ClassNotFoundException {
        final EnumSingleton instance1 = EnumSingleton.INSTANCE1;
        instance1.setContent("枚举序列化");
        System.out.println(instance1.getContent());

        final ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("EnumSerializable.obj"));
        oos.writeObject(instance1);
        oos.flush();
        oos.close();

        final FileInputStream fis = new FileInputStream("EnumSerializable.obj");
        final ObjectInputStream ois = new ObjectInputStream(fis);
        final EnumSingleton o = (EnumSingleton) ois.readObject();
        ois.close();
        System.out.println(o.getContent());
        // 序列化后 扔是同一个对象
        System.out.println("equals: " + (instance1.equals(o)));

        System.out.println(instance1.valueOf("INSTANCE1"));
        System.out.println(EnumSingleton.INSTANCE3.valueOf("INSTANCE3"));
    }

    private static void testReflection() throws NoSuchMethodException, InstantiationException, IllegalAccessException, java.lang.reflect.InvocationTargetException {
        final EnumSingleton instance1 = EnumSingleton.INSTANCE1;
//        final EnumSingleton instance2 = EnumSingleton.INSTANCE2;  // true
        final EnumSingleton instance2 = EnumSingleton.INSTANCE1;    // false
        System.out.println(instance1 == instance2); // true

        Constructor<EnumSingleton> constructor = null;
        // NoSuchMethodException
//        constructor = EnumSingleton.class.getDeclaredConstructor();
        // java.lang.IllegalArgumentException: Cannot reflectively create enum objects
        // java.reflect.Constructor.newInstance明确说明 不能通过反射实例化java.lang.Enum类
        constructor = EnumSingleton.class.getDeclaredConstructor(String.class, int.class);
        constructor.setAccessible(true);
        EnumSingleton instance3 = null;
        instance3 = constructor.newInstance();
        System.out.println(instance1 == instance3);
    }
}

/**
 * 枚举中 定义几个成员 就有几个成员
 */

/**
 * 每个枚举类型 机器 定义的枚举变量在jvm中都是唯一的, 因此枚举类型的序列化和反序列化上, java做了特殊规定
 * 序列化时java仅仅是将枚举对象的name属性输出到结果中,反序列化的时候通过java.lang.Enum的valueOf()方法根据名字查找枚举对象
 *
 * 也就是说,序列化的时候只将DATASOURCE这个名称输出,反序列化再通过这个名称查找对应的枚举类型, 因此反序列化后的实例也会和之前被序列化的对象实例相同
 *
 * 充分利用枚举默认构造私有的性质实现单例, 且内部成员都是final的, 因此不会有线程不安全的问题
 */