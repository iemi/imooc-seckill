package com.lingyumin.seckill.dao;

import com.lingyumin.seckill.entity.Seckill;
import com.lingyumin.seckill.entity.SuccessKilled;
import com.lingyumin.seckill.entity.SuccessKilledKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * [简要描述]:
 * [详细描述]:
 *
 * @author: lingyumin
 * @date: 2020/7/6 10:59 上午
 * @since: JDK 1.8
 */
@Repository
public interface SuccessKilledDao extends JpaRepository<SuccessKilled, SuccessKilledKey> {
    @Query("select count(s) from  SuccessKilled s where  s.seckillId = ?1 and s.userPhone = ?2")
    Integer countBySeckillAndUserPhone(Long seckillId, Long UserPhone);
}
