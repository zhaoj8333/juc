package lang.jmx;

import com.google.gson.Gson;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.*;

/**
 * {@link java.lang.management.ManagementFactory} 工厂类提供一套管理接口，提供jvm运行的各种信息：内存，gc，os
 * 辅助定位问题和性能调优
 *
 * 同时允许和远程JMX对正在运行的jvm进行监控和管理
 *
 * {@link javax.management.MXBean}: 通过调用 {@link ManagementFactory}的方法获取jvm各组件，可以获取到运行状态数据，
 * 这些bean是在服务启动的时候自动注册好的，运行数据也是提前计算好的。这些JavaBean成为MBean或MXBean,{@link javax.management.MXBean}
 * 可以在不加载类的情况下使用
 */
public class MemGcMxBean {

    public static void main(String[] args) {
        MemGcMxBean management = new MemGcMxBean();
        Gson gson = new Gson();
        String mem = gson.toJson(management.getMemStatus());
        String gc = gson.toJson(management.getGcStatus());

        System.out.println(mem);
        System.out.println(gc);
    }

    /**
     * 频繁的FullGC导致OOM，使用 {@link java.lang.management.MemoryMXBean} 和 {@link java.lang.management.GarbageCollectorMXBean} 获取信息
     */
    private static volatile MemoryMXBean memoryMXBean;
    private static volatile List<GarbageCollectorMXBean> garbageCollectorMXBeanList;
    private static synchronized MemoryMXBean getMemoryMXBean() {
        if (Objects.nonNull(memoryMXBean)) return memoryMXBean;
        try {
            memoryMXBean = AccessController.doPrivileged((PrivilegedExceptionAction<MemoryMXBean>) ManagementFactory::getMemoryMXBean);
        } catch (PrivilegedActionException e) {
            e.printStackTrace();
        }
        return memoryMXBean;
    }
    private static synchronized List<GarbageCollectorMXBean> getGcMXBeanList() {
        if (Objects.nonNull(garbageCollectorMXBeanList)) return garbageCollectorMXBeanList;
        try {
            garbageCollectorMXBeanList = AccessController.doPrivileged((PrivilegedExceptionAction<List<GarbageCollectorMXBean>>) ManagementFactory::getGarbageCollectorMXBeans);
        } catch (PrivilegedActionException e) {
            e.printStackTrace();
        }
        return garbageCollectorMXBeanList;
    }

    private Map<String, Object> getMemStatus() {
        HashMap<String, Object> memMap = new HashMap<>();
        // 堆内存
        memMap.put("Heap commmited", getMemoryMXBean().getHeapMemoryUsage().getCommitted());
        memMap.put("Heap mem init", getMemoryMXBean().getHeapMemoryUsage().getInit());
        memMap.put("Heap mem max", getMemoryMXBean().getHeapMemoryUsage().getMax());
        memMap.put("Heap mem used", getMemoryMXBean().getHeapMemoryUsage().getUsed());
        memMap.put("Heap mem use ratio", getMemoryMXBean().getHeapMemoryUsage().getUsed() * 100 / getMemoryMXBean().getHeapMemoryUsage().getMax() + "%");
        // 堆外内存
        memMap.put("Non heapmem commited", getMemoryMXBean().getNonHeapMemoryUsage().getCommitted());
        memMap.put("Non heapmem init", getMemoryMXBean().getNonHeapMemoryUsage().getInit());
        memMap.put("Non heapmem used", getMemoryMXBean().getNonHeapMemoryUsage().getUsed());
        return memMap;
    }

    private Map<String, Object> getGcStatus() {
        HashMap<String, Object> gcMap = new HashMap<>();
        if (!getGcMXBeanList().isEmpty()) {
            for (GarbageCollectorMXBean gcMXBean : getGcMXBeanList()) {
                gcMap.put(gcMXBean.getName() + "-" + Arrays.toString(gcMXBean.getMemoryPoolNames()) + "-count", gcMXBean.getCollectionCount());
                gcMap.put((gcMXBean.getName() + "-" + Arrays.toString(gcMXBean.getMemoryPoolNames())) + "-time", gcMXBean.getCollectionTime());
            }
        }
        return gcMap;
    }


}
