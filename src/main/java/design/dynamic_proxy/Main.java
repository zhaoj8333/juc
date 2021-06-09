package design.dynamic_proxy;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public class Main {

    public static void main(String[] args) {
        // 实际被代理的对象
        Student a = new Student("A");
        // 与实际被代理对象相关联的 InvocationHandler, InvocationHandler与被代理的类一一对应
        StudentInvocationHandler<Person> si = new StudentInvocationHandler<>(a);

        // 创建一个代理对象来代理a，代理对象的每个执行方法都会替换执行 Invocation中的invoke方法
        /**
         * {@link Proxy#newProxyInstance(ClassLoader, Class[], InvocationHandler)}
         * 用于创建动态代理对象，封装了创建动态代理类的步骤
         *
         * Class<?> cl = getProxyClass0(loader, initfs);用于产生代理类，构造器也是通过这里的类来获得
         * 这个类的产生是整个动态代理的关键，由于是动态生成，该类文件缓存在JVM中
         *
         * jdk会生成 $Proxy0(0为编号，有多个代理会递增),该类放在内存中，创建代理时通过反射获取，创建类实例
         * 代理类继承了 {@link Proxy}类，决定了java动态代理只能对接口进行代理，这些动态代理类无法实现对class的动态代理
         */
        Person proxy = (Person) Proxy.newProxyInstance(
                Person.class.getClassLoader(),
                new Class<?>[]{Person.class},
                si
        );
//        byte[] $Proxy0s = ProxyGenerator.generateProxyClass("$Proxy0", Student.class.getInterfaces());
//        String path = System.getProperty("user.dir") + "/StudentProxy.class";
//        FileOutputStream fos = null;
//        try {
//            fos = new FileOutputStream(path);
//            fos.write($Proxy0s);
//            fos.flush();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        proxy.giveMoney();

    }
}
