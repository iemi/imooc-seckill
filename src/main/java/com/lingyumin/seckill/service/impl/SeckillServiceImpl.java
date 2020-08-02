package com.lingyumin.seckill.service.impl;

import com.lingyumin.seckill.dao.SeckillDao;
import com.lingyumin.seckill.dao.SuccessKilledDao;
import com.lingyumin.seckill.dto.Exposer;
import com.lingyumin.seckill.dto.SeckillExecution;
import com.lingyumin.seckill.dto.SeckillResult;
import com.lingyumin.seckill.entity.Seckill;
import com.lingyumin.seckill.entity.SuccessKilled;
import com.lingyumin.seckill.entity.SuccessKilledKey;
import com.lingyumin.seckill.enums.SeckillStatEnum;
import com.lingyumin.seckill.exception.RepeatKillException;
import com.lingyumin.seckill.exception.SeckillCloseException;
import com.lingyumin.seckill.exception.SeckillException;
import com.lingyumin.seckill.service.SeckillService;
import com.lingyumin.seckill.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * [简要描述]:
 * [详细描述]:
 *
 * @author: lingyumin
 * @date: 2020/7/6 3:07 下午
 * @since: JDK 1.8
 */
@Service
public class SeckillServiceImpl implements SeckillService {

    private final String salt = "secrect";

    @Autowired
    private SeckillDao seckillDao;
    @Autowired
    private SuccessKilledDao successKilledDao;
    @Autowired
    private RedisUtil redisUtil;

    @Override
    public List<Seckill> getSeckillList() {
        List<Seckill> seckillList = seckillDao.findAll();
        return seckillList;
    }

    @Override
    public Seckill getSeckillById(long seckillId) {
        Seckill seckill = seckillDao.getOne(seckillId);
        return seckill;
    }

    @Override
    public Exposer exportSeckillUrl(long seckillId) {

        Seckill seckill = (Seckill) redisUtil.get("seckill:"+seckillId);
        //redis没有缓存
        if(seckill == null){
            seckill = seckillDao.getOne(seckillId);
            if(seckill != null){
                //从mysql中读取
                Optional<Seckill> optionalSeckill = seckillDao.findById(seckillId);
                seckill = optionalSeckill.get();
                redisUtil.expire("seckill:"+seckill.getSeckillId(),60*60);
            }else {
                return new Exposer(false,seckillId);
            }
        }
        //当前时间
        Date date = new Date();
        if(date.getTime() < seckill.getStartTime().getTime() || date.getTime() > seckill.getEndTime().getTime()){
            return new Exposer(false, seckillId, date.getTime(), seckill.getStartTime().getTime(), seckill.getEndTime().getTime());
        }
        Exposer exposer = new Exposer(true, getMd5(seckillId), seckillId);
        return exposer;
    }

    @Transactional
    @Override
    public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5) throws SeckillException, RepeatKillException, SeckillCloseException {

        if(md5 == null || !md5.equals(getMd5(seckillId))){
            throw new SeckillException("秒杀数据被篡改");
        }

        //注意这条查询加了排他锁
        Seckill seckill = seckillDao.getBySeckillId(seckillId);
        //秒杀时间校验
        Date date = new Date();
        if(date.getTime() < seckill.getStartTime().getTime() || date.getTime() > seckill.getEndTime().getTime()){
            throw new SeckillCloseException("秒杀已结束");
        }
        //库存校验
        if(seckill.getNumber() <= 0){
            throw new SeckillCloseException("商品已经卖完咯");
        }
        //重复秒杀校验
        if(successKilledDao.countBySeckillAndUserPhone(seckillId,userPhone) > 0){
            throw new RepeatKillException("你已经成功秒杀一次了");
        }

        //减库存
        int num = seckill.getNumber() - 1;
        seckill.setNumber(num);
        seckillDao.save(seckill);

        //生成成功秒杀记录
        SuccessKilled successKilled = new SuccessKilled();
        successKilled.setSeckillId(seckillId);
        successKilled.setUserPhone(userPhone);
        successKilled.setState(SeckillStatEnum.SUCCESS.getState());
        successKilled.setCreateTime(date);
        successKilledDao.save(successKilled);
        return new SeckillExecution(seckillId, SeckillStatEnum.SUCCESS, successKilled);
    }

    @Override
    public SeckillExecution executeSeckillProcedure(long seckillId, long userPhone, String md5) {

        if(md5 == null || !md5.equals(getMd5(seckillId))){
            return new SeckillExecution(seckillId, SeckillStatEnum.DATE_REWRITE);
        }

        int result = seckillDao.excute_seckill(seckillId,userPhone,new Date());

        //秒杀结束
        if(result == 0){
            return new SeckillExecution(seckillId, SeckillStatEnum.END);
        }

        //重复秒杀
        if (result == -1){
            return new SeckillExecution(seckillId, SeckillStatEnum.REPEAT_KILL);
        }

        //成功
        Optional<SuccessKilled> optionalSuccessKilled = successKilledDao.findById(new SuccessKilledKey(seckillId, userPhone));
        return new SeckillExecution(seckillId, SeckillStatEnum.SUCCESS, optionalSuccessKilled.get());
    }

    /**
     * 获取MD5加密字符串
     * @param seckillId
     * @return
     */
    private String getMd5(long seckillId){
        String base = seckillId+'/'+salt;
        return DigestUtils.md5DigestAsHex(base.getBytes());
    }
}
