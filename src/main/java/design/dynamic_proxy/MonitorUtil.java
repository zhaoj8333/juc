package design.dynamic_proxy;

public class MonitorUtil {
    private static ThreadLocal<Long> t = new ThreadLocal<>();

    public static void start() {
        t.set(System.currentTimeMillis());
    }

    public static void finish(String methodName) {
        long finishTime = System.currentTimeMillis();
        System.out.println(methodName + " 耗时 " + (finishTime - t.get()) + " ms");
    }
}
