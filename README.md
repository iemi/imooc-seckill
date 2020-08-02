# 慕课网Java高并发秒杀优化
### 简介
慕课网Java高并发秒杀优化课程源码，原课程采用SSM框架 + MySQL + Redis，此源码采用SpringBoot + SpringDataJPA + MySQL + Redis，
主要并发优化点：
1. 静态资源挂载CDN
2. Redis缓存秒杀商品
3. 执行秒杀逻辑通过MySQL存储过程完成，减少事务持有秒杀商品数据锁的时间
### 相关版本
1. SpringBoot：2.3.1.RELEASE
2. MySQL：5.6
3. Redis：4.0.7
### 快速开始
1. 拉取代码
2. 修改配置文件，MySQL和Redis配置
3. 秒杀商品列表页面：http://localhost:8888/seckill/list
### Jmeter压力测试
源码里面秒杀接口注释取消了手机号码的验证，以System.currentTimeMillis()作为手机号
秒杀接口：
1. 正常执行秒杀接口：localhost:8888/seckill/1000/753839613a6c8e4c662f8cd12a741702/execution
2. 存储过程秒杀接口：localhost:8888/seckill/1000/753839613a6c8e4c662f8cd12a741702/procedure

通过Jmeter对两个接口进行1000哥线程的并发测试，确实存储过程秒杀接口的并发性能有优于正常执行秒杀接口，可以自己尝试测试一下