package com.atguigu.core.consts;


/**
 * @author breeze
 * @date 2019/11/11 19:40
 */
public interface Consts {

    /**
     * redis中手机验证码前缀
     */
    String CODE_PREFIX = "code:mobile:";
    /**
     * redis手机验证码获取次数前缀
     */
    String CODE_COUNT_PREFIX = "code:mobile:count";
    /**
     * 手机验证码次数
     */
    Integer CODE_COUNT = 5;


}
