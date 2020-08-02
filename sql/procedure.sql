DELIMITER //
CREATE PROCEDURE excute_seckill(IN v_seckill_id BIGINT, IN v_user_phone BIGINT, IN v_kill_time datetime, OUT r_result int)
BEGIN
	DECLARE s_time datetime;  
	DECLARE e_time datetime;  
	DECLARE quality int ;
	DECLARE success_count int;
	START TRANSACTION;
	SELECT start_time FROM seckill WHERE seckill_id = v_seckill_id INTO s_time FOR UPDATE;
	SELECT end_time FROM seckill WHERE seckill_id = v_seckill_id INTO e_time;
	SELECT number FROM seckill WHERE seckill_id = v_seckill_id INTO quality;
	SELECT COUNT(*) from  success_killed where seckill_id = v_seckill_id and user_phone = v_user_phone INTO success_count;
-- 	校验时间
	IF v_kill_time<s_time THEN
		ROLLBACK;
		SET r_result = 0;
	ELSEIF v_kill_time>e_time THEN
		ROLLBACK;
		SET r_result = 0;
-- 	校验库存
	ELSEIF quality<=0 THEN
		ROLLBACK;
		SET r_result = 0;
-- 	校验重复秒杀
	ELSEIF success_count>0 THEN
		ROLLBACK;
		SET r_result = -1;
	ELSE
		UPDATE seckill SET number = number - 1 WHERE seckill_id = v_seckill_id;
		INSERT INTO success_killed VALUES(v_seckill_id,v_user_phone,1,v_kill_time);
		COMMIT;
		SET r_result = 1;
	END IF;
	COMMIT;
END

CALL excute_seckill(1000,13123327863,'2020-07-28 17:28:30',@r_result)