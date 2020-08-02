package com.lingyumin.seckill.dto;

import com.lingyumin.seckill.entity.SuccessKilled;
import com.lingyumin.seckill.enums.SeckillStatEnum;
import lombok.Data;

/**
 * [简要描述]:
 * [详细描述]:
 *
 * @author: lingyumin
 * @date: 2020/7/6 11:20 上午
 * @since: JDK 1.8
 */
@Data
public class SeckillExecution {
    private long seckillId;

    //秒杀执行结果的状态
    private int state;

    //状态的明文标识
    private String stateInfo;

    //当秒杀成功时，需要传递秒杀成功的对象回去
    private SuccessKilled successKilled;

    //秒杀成功返回所有信息
    public SeckillExecution(long seckillId, SeckillStatEnum statEnum, SuccessKilled successKilled) {
        this.seckillId = seckillId;
        this.state = statEnum.getState();
        this.stateInfo = statEnum.getInfo();
        this.successKilled = successKilled;
    }

    //秒杀失败
    public SeckillExecution(long seckillId, SeckillStatEnum statEnum) {
        this.seckillId = seckillId;
        this.state = statEnum.getState();
        this.stateInfo = statEnum.getInfo();
    }
}
