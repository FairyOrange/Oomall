package cn.edu.xmu.oomall.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 过滤返回字段
 *
 * @author wwc
 * @date 2020/11/22 09:33
 * @version 1.0
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonFieldFilter {
    Class<?> type();//对哪个类的属性进行过滤
    String include() default "";//包含哪些字段，即哪些字段可以显示
    String exclude() default "";//不包含哪些字段，即哪些字段不可以显示
}
