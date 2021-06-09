package lang.jmx.hello;

import javax.management.*;
import java.lang.management.ManagementFactory;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * {@link ObjectName}规则：
 *     域:参数=值,参数=值
 */
public class MBeanPublish {

    public static void main(String[] args) {
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        try {
            ObjectName beanName = new ObjectName("lang.jmx.hello:type=Hello");

            Hello hello = new Hello();
            mbs.registerMBean(hello, beanName);

            System.out.println("waiting for requests ...");

            new Thread(() -> {
                Random random = new Random();
                while (true) {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException ignored) { }
                    hello.setCacheSize(random.nextInt() + hello.getCacheSize());
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
