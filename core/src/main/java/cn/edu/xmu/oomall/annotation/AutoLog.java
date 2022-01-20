package cn.edu.xmu.oomall.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自动日志注解
 *
 * @author wwc
 * @date 2020/11/22 09:32
 * @version 1.0
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoLog {
    /** 模块 */
    String title() default "";

    /** 功能 */
    String action() default "";
}
