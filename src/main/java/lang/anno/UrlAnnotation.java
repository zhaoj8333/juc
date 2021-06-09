package lang.anno;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface UrlAnnotation {
    String title();
    String url() default "获取url，默认值";
}
