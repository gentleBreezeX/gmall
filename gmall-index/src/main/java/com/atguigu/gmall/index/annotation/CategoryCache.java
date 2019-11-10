package com.atguigu.gmall.index.annotation;


import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * @author breeze
 * @date 2019/11/9 11:49
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CategoryCache {
    /**
     *
     * @return 缓存到redis中键的前缀
     */
    String prefix() default "index:category:";

    /**
     * 超时时间单位
     * @return
     */
    TimeUnit unit();
    /**
     *
     * @return 超时时间
     */
    long timeout();

    /**
     * 为了解决redis雪崩问题，random随机参数范围
     * @return
     */
    long random();


}
