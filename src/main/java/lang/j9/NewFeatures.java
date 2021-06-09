package lang.j9;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashSet;

/**
 *
 * java 9 变化：
 *     jshell: 交互式编程环境
 *     接口允许有 私有方法
 *     钻石操作符： <>
 *     {@link java.lang.String} 底层使用byte[]数组，而不是char[]数组
 */
public class NewFeatures implements InterfacePrivateFunction {
    public static void main(String[] args) {
//        interfacePrivateFunction();
//        diamondOperator();
//        tryCatch();
//        underScore();
        stringStore();
//        collectionCreate();
//        enhancedStream();
//        http2Client();
    }

    /**
     * Http 1.1与Http 2区别：
     * http 1.1依赖于请求，响应周期，
     * http 2允许服务器 push数据，可以发送比客户端请求更多的数据，使得它可以优先处理并发送对于加载网页至关重要的数据
     */
    private static void http2Client() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest req = HttpRequest.newBuilder(URI.create("http://www.baidu.com")).GET().build();
        try {
            HttpResponse<String> response = client.send(req, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void enhancedStream() {

    }

    private static void collectionCreate() {
//        Collections.unmodifiableList();
    }

    /**
     *  {@link String}存储结构变更
     *  j9之前的String使用 {@link Character}字符数组，每个字符使用的是2字节的大小存储，但是绝大部分String对象只包含一个拉丁文字符
     *  这些字符只占据一个字符的空间 UTF_16
     *
     *  UTF-8: 字符大小从 8 到 24不整，动态存储
     *  UTF-16: 使用2字节大小存储所有字符
     *
     *  j9之后的String使用了byte数组存储，并加上了一个encoding-flag，指定使用的是那种字符编码集，提高内存使用效率
     *  {@link StringBuilder}
     *  {@link StringBuffer}
     *  {@link java.lang.AbstractStringBuilder}
     *  等类也有所变更
     */
    private static void stringStore() {

    }

    /**
     * 下划线 _ 不在可以作为变量名（标识符）
     */
    private static void underScore() {
//        String _ = "hello";
    }

    private static void tryCatch() {
        InputStreamReader isr = new InputStreamReader(System.in);
        OutputStreamWriter osr = new OutputStreamWriter(System.out);
        try (isr; osr) { // jdk8以及之前 不支持这样的使用
//            isr = null;
            isr.read();     // 此时的isr是final的，不需要显示的指明
            osr.write(1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void diamondOperator() {
        HashSet<Integer> set = new HashSet<>();
        set.add(1);
        set.add(2);
        System.out.println(set);
        System.out.println(new ArrayList<>(set));
    }

    private static void interfacePrivateFunction() {
        NewFeatures ip = new NewFeatures();
        ip.call();
    }
}
