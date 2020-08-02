package com.lingyumin.seckill.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * [简要描述]:
 * [详细描述]:
 *
 * @author: lingyumin
 * @date: 2020/7/6 10:52 上午
 * @since: JDK 1.8
 */
@Data
@Entity
@Table(name = "success_killed")
@IdClass(com.lingyumin.seckill.entity.SuccessKilledKey.class)
public class SuccessKilled implements Serializable {
    @Id
    private long seckillId;
    @Id
    private long userPhone;
    private int state;
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date createTime;
}
