package com.lingyumin.seckill.controller;

import com.lingyumin.seckill.dao.SeckillDao;
import com.lingyumin.seckill.dto.Exposer;
import com.lingyumin.seckill.dto.SeckillExecution;
import com.lingyumin.seckill.dto.SeckillResult;
import com.lingyumin.seckill.entity.Seckill;
import com.lingyumin.seckill.enums.SeckillStatEnum;
import com.lingyumin.seckill.exception.RepeatKillException;
import com.lingyumin.seckill.exception.SeckillCloseException;
import com.lingyumin.seckill.exception.SeckillException;
import com.lingyumin.seckill.service.SeckillService;
import com.lingyumin.seckill.util.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * [简要描述]:
 * [详细描述]:
 *
 * @author: lingyumin
 * @date: 2020/7/6 11:12 下午
 * @since: JDK 1.8
 */
@Controller
@RequestMapping("/seckill")
public class SeckillController {

    Logger logger = LoggerFactory.getLogger(SeckillController.class);

    @Autowired
    private SeckillService seckillService;

    @Autowired
    private SeckillDao seckillDao;

    @Autowired
    private RedisUtil redisUtil;

    @GetMapping("/list")
    public String patchSeckill (Model model){
        List<Seckill> seckills = seckillService.getSeckillList();
        model.addAttribute("list",seckills);
        return "list";
    }

    @GetMapping("/{seckillId}/detail")
    public String getSeckill (@PathVariable("seckillId") Long seckillId, Model model){
        if(seckillId==null){
            return "forward:/seckill/list";
        }
        Seckill seckill = seckillService.getSeckillById(seckillId);
        if(seckill==null){
            return "forward:/seckill/list";
        }
        model.addAttribute("seckill",seckill);
        return "detail";
    }

    @RequestMapping(value = "/time/now",method = RequestMethod.GET)
    @ResponseBody
    public SeckillResult<Long> getTimeNow(){
        Date date = new Date();
        return new SeckillResult<Long>(true,date.getTime());
    }

    @GetMapping("/{seckillId}/exposer")
    @ResponseBody
    public SeckillResult<Exposer> exposer(@PathVariable("seckillId") Long seckillId){
        SeckillResult<Exposer> result;
        try {
            Exposer exposer = seckillService.exportSeckillUrl(seckillId);
            result = new SeckillResult<Exposer>(true,exposer);
        }catch (Exception e){
            result = new SeckillResult<Exposer>(false,e.getMessage());
        }
        return result;
    }

    @PostMapping("/{seckillId}/{md5}/execution")
    @ResponseBody
    public SeckillResult<SeckillExecution> executeSeckill (@PathVariable("seckillId") Long seckillId,
                                                           @CookieValue(value = "killPhone",required = false)Long userPhone,
                                                           @PathVariable("md5") String md5){
//        if(userPhone == null){
//            return new SeckillResult<SeckillExecution>(true,"未注册");
//        }
//        int num = (int) Math.random()*100;
        try {
            SeckillExecution seckillExecution = seckillService.executeSeckill(seckillId,System.currentTimeMillis(),md5);
            return new SeckillResult<SeckillExecution>(true,seckillExecution);
        }catch (RepeatKillException e){
            return new SeckillResult<SeckillExecution>(true,new SeckillExecution(seckillId, SeckillStatEnum.REPEAT_KILL));
        }catch (SeckillCloseException e){
            return new SeckillResult<SeckillExecution>(true,new SeckillExecution(seckillId,SeckillStatEnum.END));
        }catch (SeckillException e){
            return new SeckillResult<SeckillExecution>(true,new SeckillExecution(seckillId,SeckillStatEnum.INNER_ERROR));
        }catch (RuntimeException e){
            logger.error("秒杀异常："+e.getMessage());
            return new SeckillResult<SeckillExecution>(true,new SeckillExecution(seckillId,SeckillStatEnum.INNER_ERROR));
        }
    }

    @PostMapping("/{seckillId}/{md5}/procedure")
    @ResponseBody
    public SeckillResult<SeckillExecution> executeSeckillProcedure (@PathVariable("seckillId") Long seckillId,
                                                           @CookieValue(value = "killPhone",required = false)Long userPhone,
                                                           @PathVariable("md5") String md5){
//        if(userPhone == null){
//            return new SeckillResult<SeckillExecution>(true,"未注册");
//        }
        try {
            SeckillExecution seckillExecution = seckillService.executeSeckillProcedure(seckillId,System.currentTimeMillis(),md5);
            return new SeckillResult<SeckillExecution>(true,seckillExecution);
        }catch (Exception e){
            logger.error("秒杀异常:"+e.getMessage());
            return new SeckillResult<SeckillExecution>(true,new SeckillExecution(seckillId,SeckillStatEnum.INNER_ERROR));
        }
    }

    @PostMapping("setRedis")
    @ResponseBody
    public String setSeckill(Long seckillId){
        try {
            Optional<Seckill> optionalSeckill = seckillDao.findById(seckillId);
            Seckill seckill = optionalSeckill.get();
            redisUtil.set("seckill:"+seckill.getSeckillId(),seckill);
            logger.info("set redis success");
            return "set redis success";
        }catch (Exception e){
            e.getMessage();
            logger.error("set redis fail");
            return "set redis fail";
        }
    }

    @GetMapping("getRedis")
    @ResponseBody
    public Seckill getSeckill(Long seckillId){
        Seckill seckill;
        try {
            seckill = (Seckill) redisUtil.get("seckill:"+seckillId);
            logger.info("get redis success");
        }catch (Exception e){
            e.getMessage();
            logger.error("get redis fail");
            seckill = null;
        }
        return seckill;
    }

    @GetMapping("testProcedure")
    @ResponseBody
    public String testProcedure(){
        try {
            int result = seckillDao.excute_seckill(Long.valueOf("1000"),Long.valueOf("1111111"),new Date());
            logger.info("testProcedure success");
            return "result:"+result;
        }catch (Exception e){
            e.getMessage();
            logger.error("testProcedure fail:"+e.getMessage());
            return "fail";
        }
    }
}
