package com.atguigu.gmall.auth.exception;

/**
 * @author breeze
 * @date 2019/11/12 18:12
 */
public class GmallException extends RuntimeException {

    static final long serialVersionUID = -7034897190745766939L;

    public GmallException(String msg){
        super(msg);
    }
}
