package lang.anno;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class Main {
    public static void main(String[] args) throws Exception {
//        test1();

    }

    private static void test1() throws Exception {
        Annotation[] annotations = IMessage.class.getAnnotations();
        for (Annotation annotation : annotations) {
            System.out.println(annotation);
        }
        System.out.println("////////////////////////");
        /**
         * 此处无法获取 @{@link SuppressWarnings}注解，
         * 因为该注解的作用域为 {@link java.lang.annotation.RetentionPolicy.SOURCE}
         */
        Annotation[] annotationsImpl = MessageImpl.class.getAnnotations();
        for (Annotation annotation : annotationsImpl) {
            System.out.println(annotation);
        }
        System.out.println("////////////////////////");

        /**
         * {@link Override}注解的 {@link java.lang.annotation.RetentionPolicy.SOURCE}为source
         */
        Method send = MessageImpl.class.getDeclaredMethod("send");
        Annotation[] sendAnnos = send.getAnnotations();
        for (Annotation sendAnno : sendAnnos) {
            System.out.println(sendAnno);
        }

    }
}
