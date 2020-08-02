package com.lingyumin.seckill.exception;

/**
 * [简要描述]:
 * [详细描述]:
 *
 * @author: lingyumin
 * @date: 2020/7/6 3:04 下午
 * @since: JDK 1.8
 */
public class SeckillCloseException extends SeckillException {
    public SeckillCloseException(String message) {
        super(message);
    }

    public SeckillCloseException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
