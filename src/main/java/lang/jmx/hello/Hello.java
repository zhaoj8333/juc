package lang.jmx.hello;

import javax.management.*;

/**
 * {@link javax.management.JMX}
 * 除了可以监控外和配置参数外，还可以执行一些定义好的操作。
 *
 * 监控变化和通知：
 * {@link javax.management.JMX}定义了可以使MBean生成通知的机制，用于指示状态变更，检测事件或问题
 */
public class Hello extends NotificationBroadcasterSupport implements HelloMBean {

    private long sequenceNumber = 1;

    private final String name = "撒娇分科了大声叫卡拉发";

    private static final int DEFAULT_CACHE_SIZE = 200;

    private int cacheSize = DEFAULT_CACHE_SIZE;

    @Override
    public void sayHello() {
        System.out.println("hello, jmx");
    }

    @Override
    public int add(int x, int y) {
        return x + y;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public int getCacheSize() {
        return this.cacheSize;
    }

    @Override
    public synchronized void setCacheSize(int size) {
        int oldSize = this.cacheSize;
        this.cacheSize = size;
        System.out.println("Cache size now is : " + this.cacheSize);
        Notification n = new AttributeChangeNotification(
                this,
                sequenceNumber++,
                System.currentTimeMillis(),
                "CacheSize changed",
                "CacheSize",
                "int",
                oldSize,
                this.cacheSize
        );
        sendNotification(n);
    }


    /**
     * 要生成通知，MBean必须实现 {@link NotificationEmitter} 或 {@link NotificationBroadcasterSupport}
     * 要发送通知，还需要构造 {@link Notification}类或一个子类实例，并将该实例传递给 {@link NotificationBroadcasterSupport}.sendNotification
     */
    @Override
    public void addNotificationListener(NotificationListener listener, NotificationFilter filter, Object handback) throws IllegalArgumentException {

    }

    @Override
    public void removeNotificationListener(NotificationListener listener) throws ListenerNotFoundException {

    }

    @Override
    public MBeanNotificationInfo[] getNotificationInfo() {
        String[] types = {AttributeChangeNotification.ATTRIBUTE_CHANGE};
        String name = AttributeChangeNotification.class.getName();
        String des = "An attribute of this mBean has changed";
        MBeanNotificationInfo info = new MBeanNotificationInfo(types, name, des);
        return new MBeanNotificationInfo[] {info};
    }
}
