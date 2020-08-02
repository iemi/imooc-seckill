package com.lingyumin.seckill.exception;

/**
 * [简要描述]:
 * [详细描述]:
 *
 * @author: lingyumin
 * @date: 2020/7/6 3:03 下午
 * @since: JDK 1.8
 */
public class SeckillException extends RuntimeException {
    public SeckillException(String message){
        super(message);
    }

    public SeckillException(String msg,Throwable cause){
        super(msg,cause);
    }
}
