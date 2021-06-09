package lang.anno;

import java.lang.reflect.Method;
import java.util.Objects;

@UrlAnnotation(title = "class", url = "www.baidu.com")
public class HttpUrl {

    @UrlAnnotation(title = "sendMessage by url", url = "/?keyword=send")
    public void send(String message) {
        System.out.println("[消息发送] " + message);
    }

    public static void main(String[] args) throws Exception {
        getClassAnnon();
        System.out.println("--------------");
        getMethodAnnon();
    }

    private static void getClassAnnon() {
        try {
            UrlAnnotation urlAnnotation = HttpUrl.class.getAnnotation(UrlAnnotation.class);
            System.out.println(urlAnnotation.url());
            System.out.println(urlAnnotation.title());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void getMethodAnnon() throws Exception {

        Method sendMethod = HttpUrl.class.getDeclaredMethod("send", String.class);
        UrlAnnotation annotation = sendMethod.getAnnotation(UrlAnnotation.class);

        if (Objects.isNull(annotation)) return;

        String title = annotation.title();
        String url = annotation.url();
        String s = " + title + " + title + ", url: " + url;

        System.out.println();
        sendMethod.invoke(HttpUrl.class.getDeclaredConstructor().newInstance(), s);

    }

}
