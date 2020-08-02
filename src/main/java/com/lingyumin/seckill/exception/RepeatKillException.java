package com.lingyumin.seckill.exception;

/**
 * [简要描述]:
 * [详细描述]:
 *
 * @author: lingyumin
 * @date: 2020/7/6 3:05 下午
 * @since: JDK 1.8
 */
public class RepeatKillException extends SeckillException {

    public RepeatKillException(String message) {
        super(message);
    }

    public RepeatKillException(String msg, Throwable cause) {
        super(msg, cause);
    }
}