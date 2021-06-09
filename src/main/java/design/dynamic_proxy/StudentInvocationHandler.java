package design.dynamic_proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class StudentInvocationHandler<T> implements InvocationHandler {

    // 被代理对象
    private T target;

    public StudentInvocationHandler(T target) {
        this.target = target;
    }

    /**
     * 所有被代理的方法都是通过在 {@link InvocationHandler}中的invoke调用的，所以只要在invoke中统一处理，就可以对所有被代理的方法进行相同操作
     * 动态代理过程中，并没有实际看到代理类，
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("代理执行： " + method.getName() + "方法");

        MonitorUtil.start();
        Object result = method.invoke(this.target, args);
        MonitorUtil.finish(method.getName());

        return result;
    }
}
