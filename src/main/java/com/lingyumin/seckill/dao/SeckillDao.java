package com.lingyumin.seckill.dao;

import com.lingyumin.seckill.entity.Seckill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.stereotype.Repository;

import java.util.Date;

/**
 * [简要描述]:
 * [详细描述]:
 *
 * @author: lingyumin
 * @date: 2020/7/6 10:54 上午
 * @since: JDK 1.8
 */
@Repository
public interface SeckillDao extends JpaRepository<Seckill, Long> , JpaSpecificationExecutor<Seckill> {

    @Query(nativeQuery = true, value = "select * from seckill where seckill_id = ?1 for update")
    Seckill getBySeckillId(Long seckillId);

    @Procedure(procedureName = "excute_seckill")
    Integer excute_seckill(Long v_seckill_id, Long v_user_phone, Date v_kill_time);
}
