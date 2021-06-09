package lang.jmx;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Map;
import java.util.TreeMap;

public class JmxUtil {

    static RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();

    /**
     * {
     *     id = 40975@Zhaos-MacBook-Pro.local,
     *     name = Java HotSpot(TM) 64-Bit Server VM,
     *     obj name = java.lang:type = Runtime,
     *     sys pros = {
     *         java.specification.version = 15,
     *         sun.management.compiler = HotSpot 64-Bit Tiered Compilers,
     *         ftp.nonProxyHosts = local|*.local|169.254/16|*.169.254/16,
     *         sun.jnu.encoding = UTF-8,
     *         java.runtime.version = 15.0.1+9-18,
     *         java.class.path = /Users/allen/Project/jc/target/classes:/Users/allen/.m2/repository/fr/ujm/tse/lt2c/satin/cachesize/0.2.1/cachesize-0.2.1.jar:/Users/allen/.m2/repository/ru/concerteza/util/ctz-utils/2.26/ctz-utils-2.26.jar:/Users/allen/.m2/repository/commons-lang/commons-lang/2.4/commons-lang-2.4.jar:/Users/allen/.m2/repository/commons-io/commons-io/2.1/commons-io-2.1.jar:/Users/allen/.m2/repository/com/google/guava/guava/10.0/guava-10.0.jar:/Users/allen/.m2/repository/com/google/code/findbugs/jsr305/1.3.9/jsr305-1.3.9.jar:/Users/allen/.m2/repository/com/lmax/disruptor/3.4.4/disruptor-3.4.4.jar:/Users/allen/.m2/repository/log4j/log4j/1.2.17/log4j-1.2.17.jar:/Users/allen/.m2/repository/com/google/code/gson/gson/2.8.6/gson-2.8.6.jar,
     *         user.name = allen,
     *         java.vm.vendor = Oracle Corporation,
     *         path.separator = :,
     *         sun.arch.data.model = 64,
     *         os.version = 10.15.7,
     *         java.runtime.name = Java(TM) SE Runtime Environment,
     *         java.vendor.url = https://java.oracle.com/,
     *         file.encoding = UTF-8,
     *         user.timezone = Asia/Shanghai,
     *         java.vm.specification.version = 15,
     *         os.name = Mac OS X,
     *         java.vm.name = Java HotSpot(TM) 64-Bit Server VM,
     *         sun.java.launcher = SUN_STANDARD,
     *         user.country = CN,
     *         sun.boot.library.path = /Library/Java/JavaVirtualMachines/jdk-15.0.1.jdk/Contents/Home/lib,
     *         sun.java.command = jvm.JvmArchitecture,
     *         java.vendor.url.bug = https://bugreport.java.com/bugreport/,
     *         http.nonProxyHosts = local|*.local|169.254/16|*.169.254/16,
     *         java.io.tmpdir = /var/folders/tw/qwt2dxkd7w5bjf83pm36pp_00000gn/T/,
     *         jdk.debug = release,
     *         sun.cpu.endian = little,
     *         java.version = 15.0.1,
     *         user.home = /Users/allen,
     *         user.dir = /Users/allen/Project/jc,
     *         user.language = en,
     *         os.arch = x86_64,
     *         java.specification.vendor = Oracle Corporation,
     *         java.vm.specification.name = Java Virtual Machine Specification,
     *         java.version.date = 2020-10-20,
     *         java.home = /Library/Java/JavaVirtualMachines/jdk-15.0.1.jdk/Contents/Home,
     *         file.separator = /,
     *         java.vm.compressedOopsMode = Zero based,
     *         line.separator = ,
     *         java.library.path = /Users/allen/Library/Java/Extensions:/Library/Java/Extensions:/Network/Library/Java/Extensions:/System/Library/Java/Extensions:/usr/lib/java:.,
     *         java.vm.specification.vendor = Oracle Corporation,
     *         java.specification.name = Java Platform API Specification,
     *         java.vm.info = mixed mode,
     *         sharing,
     *         java.vendor = Oracle Corporation,
     *         java.vm.version = 15.0.1+9-18,
     *         sun.io.unicode.encoding = UnicodeBig,
     *         socksNonProxyHosts = local|*.local|169.254/16|*.169.254/16,
     *         java.class.version = 59.0
     *     },
     *     vendor = Oracle Corporation
     * }
     *
     * {
     *   management version=3.0,
     *   name=Java Virtual Machine Specification,
     *   vendor=Oracle Corporation,
     *   version=15
     * }
     *
     * {
     *   start time=1621479878853,
     *   uptime=5118
     * }
     *
     * {
     *     bootclass supported: =false,
     *     lib path: =/Users/allen/Library/Java/Extensions:/Library/Java/Extensions:/Network/Library/Java/Extensions:/System/Library/Java/Extensions:/usr/lib/java:.,
     *     path: =/Users/allen/Project/jc/target/classes
     *       :/Users/allen/.m2/repository/fr/ujm/tse/lt2c/satin/cachesize/0.2.1/cachesize-0.2.1.jar
     *       :/Users/allen/.m2/repository/ru/concerteza/util/ctz-utils/2.26/ctz-utils-2.26.jar
     *       :/Users/allen/.m2/repository/commons-lang/commons-lang/2.4/commons-lang-2.4.jar
     *       :/Users/allen/.m2/repository/commons-io/commons-io/2.1/commons-io-2.1.jar
     *       :/Users/allen/.m2/repository/com/google/guava/guava/10.0/guava-10.0.jar
     *       :/Users/allen/.m2/repository/com/google/code/findbugs/jsr305/1.3.9/jsr305-1.3.9.jar
     *       :/Users/allen/.m2/repository/com/lmax/disruptor/3.4.4/disruptor-3.4.4.jar
     *       :/Users/allen/.m2/repository/log4j/log4j/1.2.17/log4j-1.2.17.jar
     *       :/Users/allen/.m2/repository/com/google/code/gson/gson/2.8.6/gson-2.8.6.jar
     * }
     */
    public static String getRuntime() {

        StringBuilder sb = new StringBuilder();

        Map<String, Map<String, String>> info = new TreeMap<>();
        info.put("vm", new TreeMap<>());
        info.get("vm").put("id", runtimeMXBean.getName());
        info.get("vm").put("name", runtimeMXBean.getVmName());
        info.get("vm").put("vendor", runtimeMXBean.getVmVendor());
        info.get("vm").put("sys pros", String.valueOf(runtimeMXBean.getSystemProperties()));
        info.get("vm").put("obj name", String.valueOf(runtimeMXBean.getObjectName()));

        sb.append(info.get("vm").toString());
        sb.append("\n");

        info.put("spec", new TreeMap<>());
        info.get("spec").put("version", runtimeMXBean.getSpecVersion());
        info.get("spec").put("name", runtimeMXBean.getSpecName());
        info.get("spec").put("vendor", runtimeMXBean.getSpecVendor());
        info.get("spec").put("management version", runtimeMXBean.getManagementSpecVersion());

        sb.append(info.get("spec").toString());
        sb.append("\n");

        info.put("start", new TreeMap<>());
        info.get("start").put("uptime", String.valueOf(runtimeMXBean.getUptime()));
        info.get("start").put("start time", String.valueOf(runtimeMXBean.getStartTime()));

        sb.append(info.get("start").toString());
        sb.append("\n");

        info.put("class", new TreeMap<>());
        info.get("class").put("path: ", runtimeMXBean.getClassPath());
        info.get("class").put("lib path: ", runtimeMXBean.getLibraryPath());
//        info.get("class").put("bootclass path: ", runtimeMXBean.getBootClassPath());
        info.get("class").put("bootclass supported: ", String.valueOf(runtimeMXBean.isBootClassPathSupported()));

        sb.append(info.get("class").toString());
        sb.append("\n");

        return sb.toString();
    }
}
