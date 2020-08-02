package com.lingyumin.seckill.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * [简要描述]:
 * [详细描述]:
 *
 * @author: lingyumin
 * @date: 2020/7/8 10:27 上午
 * @since: JDK 1.8
 */
public class SeckillResult<T> {
    //请求是否成功
    @Getter
    @Setter
    private boolean success;
    private T data;
    @Getter
    @Setter
    private String error;

    public SeckillResult(boolean success, T data) {
        this.success = success;
        this.data = data;
    }

    public SeckillResult(boolean success, String error) {
        this.success = success;
        this.error = error;
    }

    public boolean isSuccess() {
        return success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

}
