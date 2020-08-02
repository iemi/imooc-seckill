package com.lingyumin.seckill.service;

import com.lingyumin.seckill.dto.Exposer;
import com.lingyumin.seckill.dto.SeckillExecution;
import com.lingyumin.seckill.entity.Seckill;
import com.lingyumin.seckill.exception.RepeatKillException;
import com.lingyumin.seckill.exception.SeckillCloseException;
import com.lingyumin.seckill.exception.SeckillException;

import java.util.List;

/**
 * [简要描述]:
 * [详细描述]:
 *
 * @author: lingyumin
 * @date: 2020/7/6 11:11 上午
 * @since: JDK 1.8
 */
public interface SeckillService {
    /**
     * 获取秒杀商品列表
     * @return
     */
    List<Seckill> getSeckillList();

    /**
     * 获取单个秒杀商品信息
     * @param seckillId
     * @return
     */
    Seckill getSeckillById(long seckillId);

    /**
     * 暴漏秒杀地址，防止秒杀接口提前暴漏
     * @param seckillId
     * @return
     */
    Exposer exportSeckillUrl(long seckillId);

    /**
     * 执行秒杀
     * @param seckillId
     * @param userPhone
     * @param md5
     * @return
     * @throws SeckillException
     * @throws RepeatKillException
     * @throws SeckillCloseException
     */
    SeckillExecution executeSeckill(long seckillId, long userPhone, String md5) throws SeckillException, RepeatKillException, SeckillCloseException;

    /**
     * 执行秒杀存储过程
     * @param seckillId
     * @param userPhone
     * @param md5
     * @return
     */
    SeckillExecution executeSeckillProcedure(long seckillId, long userPhone, String md5);
}
