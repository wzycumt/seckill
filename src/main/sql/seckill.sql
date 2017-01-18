
--执行秒杀存储过程
DROP PROCEDURE IF EXISTS `seckill`.`execute_seckill`;

DELIMITER $$ --console ; 转换为$$
--定义存储过程
CREATE PROCEDURE `seckill`.`execute_seckill`
	(in v_seckill_id bigint, in v_phone bigint, in v_kill_time timestamp, out r_result int)
	BEGIN
		DECLARE insert_count int DEFAULT 0;
		START TRANSACTION;
		INSERT IGNORE INTO success_killed
			(seckill_id, user_phone, create_time)
		VALUES(v_seckill_id, v_phone, v_kill_time);
		SELECT ROW_COUNT() INTO insert_count;
		IF (insert_count = 0) THEN 
			ROLLBACK;
			SET r_result = -1; /*重复秒杀*/
		ELSEIF (insert_count < 0) THEN
			ROLLBACK;
			SET r_result = -2;
		ELSE
			UPDATE seckill
			SET number = number - 1
			WHERE seckill_id = v_seckill_id
			AND start_time < v_kill_time
			AND end_time > v_kill_time
			AND number > 0;
			SELECT ROW_COUNT() INTO insert_count;
			IF(insert_count = 0) THEN 
				ROLLBACK;
				SET r_result = 0;
			ELSEIF (insert_count < 0) THEN
				ROLLBACK;
				SET r_result = -2;
			ELSE
				COMMIT;
				SET r_result = 1;
			END IF;
		END IF;
	END;
$$
--存储过程结束

DELIMITER ;

set @r_result = -3;
call execute_seckill(1003,13511111111,now(),@r_result);

select @r_result;
