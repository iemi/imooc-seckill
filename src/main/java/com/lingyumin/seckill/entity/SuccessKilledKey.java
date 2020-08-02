package com.lingyumin.seckill.entity;

import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * [简要描述]:
 * [详细描述]:
 *
 * @author: lingyumin
 * @date: 2020/7/24 12:50 下午
 * @since: JDK 1.8
 */
@Embeddable
public class SuccessKilledKey implements Serializable {

    private long seckillId;
    private long userPhone;

    public SuccessKilledKey(long seckillId, long userPhone){
        this.seckillId = seckillId;
        this.userPhone = userPhone;
    }

    public SuccessKilledKey(){}

    public long getSeckillId() {
        return seckillId;
    }

    public void setSeckillId(long seckillId) {
        this.seckillId = seckillId;
    }

    public long getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(long userPhone) {
        this.userPhone = userPhone;
    }
}
