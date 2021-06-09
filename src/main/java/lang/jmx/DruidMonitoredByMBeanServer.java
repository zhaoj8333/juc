package lang.jmx;

import javax.management.*;
import java.lang.management.ManagementFactory;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.*;

/**
 * {@link MBeanServer}
 *
 * {@link MBeanServer}是包含所有MBean的仓库，是JMX代理的核心，所有MBean操作通过 {@link MBeanServer}执行
 * 每个 MBean具有一个唯一的标识，{@link ObjectName}
 *
 * java -classpath /data/jc/target/classes  -Dcom.sun.management.jmxremote.port=9999 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false lang.jmx.hello.MBeanPublish
 * java -classpath /Users/allen/Project/jc/target/classes  -Dcom.sun.management.jmxremote.port=9999 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false lang.jmx.hello.MBeanPublish
 */
public class DruidMonitoredByMBeanServer {

    public static void main(String[] args) {
        DruidMonitoredByMBeanServer monitor = new DruidMonitoredByMBeanServer();
        System.out.println(beanServer);
    }

    private static volatile MBeanServer beanServer = ManagementFactory.getPlatformMBeanServer();
    private static synchronized MBeanServer getMBeanServer() {
        if (Objects.nonNull(beanServer)) return beanServer;
        try {
            beanServer = AccessController.doPrivileged(
                    (PrivilegedExceptionAction<MBeanServer>) ManagementFactory::getPlatformMBeanServer
            );
        } catch (PrivilegedActionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Set<ObjectInstance> queryMBeans(String objName) {
        if (Objects.isNull(getMBeanServer())) {
            try {
                return getMBeanServer().queryMBeans(new ObjectName(objName), null);
            } catch (MalformedObjectNameException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static Map<String, Object> getAttr(String objName, List<String> attrsList) {
        Map<String, Object> attrs = null;
        try {
            attrs = getAttrs(new ObjectName(objName), attrsList);
        } catch (MalformedObjectNameException e) {
            e.printStackTrace();
        }
        return attrs;
    }

    public static Map<String, Object> getAttrs(ObjectName objName, List<String> attrList) {
        HashMap<String, Object> result = new HashMap<>();
        if (attrList.isEmpty() || Objects.isNull(getMBeanServer())) return result;
        try {
            AttributeList attributes = getMBeanServer().getAttributes(objName, attrList.toArray(new String[]{}));
            for (int i = 0; i < attributes.size(); i++) {
                result.put((String) attributes.get(i), i < attributes.size() ? ((Attribute) attributes.get(i)).getValue() : "");
            }
        } catch (InstanceNotFoundException e) {
            e.printStackTrace();
        } catch (ReflectionException e) {
            e.printStackTrace();
        }
        return result;
    }

}
