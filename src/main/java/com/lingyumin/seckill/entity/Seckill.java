package com.lingyumin.seckill.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * [简要描述]:
 * [详细描述]:
 *
 * @author: lingyumin
 * @date: 2020/7/6 10:50 上午
 * @since: JDK 1.8
 */
@Data
@Entity(name="Seckill")
@Getter
@Setter
@Table(name = "seckill")
@JsonIgnoreProperties(value={"hibernateLazyInitializer"})
//配置存储过程
@NamedStoredProcedureQuery(name="Seckill.excute_seckill",procedureName = "excute_seckill",parameters = {
        @StoredProcedureParameter(mode=ParameterMode.IN,name="v_seckill_id",type = Long.class),
        @StoredProcedureParameter(mode=ParameterMode.IN,name="v_user_phone",type = Long.class),
        @StoredProcedureParameter(mode=ParameterMode.IN,name="v_kill_time",type = Date.class),
        @StoredProcedureParameter(mode=ParameterMode.OUT,name="r_result",type = Integer.class),
})
public class Seckill implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long seckillId;
    private String name;
    private int number;
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date startTime;
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date endTime;
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date createTime;
}
