--数据库初始化脚本

--创建数据库
CREATE DATABASE seckill;
use seckill;
CREATE TABLE SECKILL(
	SECKILL_ID BIGINT NOT NULL AUTO_INCREMENT COMMENT '商品库存ID',
	NAME VARCHAR(120) NOT NULL COMMENT '商品名称',
	NUMBER INT NOT NULL COMMENT '库存数量',
	START_TIME DATETIME NOT NULL COMMENT '秒杀开始时间',
	END_TIME DATETIME NOT NULL COMMENT '秒杀结束时间',
	CREATE_TIME TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
	PRIMARY KEY (SECKILL_ID),
	KEY IDX_START_TIME(START_TIME),
	KEY IDX_END_TIME(END_TIME),
	KEY IDX_CREATE_TIME(CREATE_TIME)
)ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8 COMMENT='秒杀库存表';

INSERT INTO SECKILL(NAME,NUMBER,START_TIME,END_TIME)
VALUES
  ('1000元秒杀iphone6', 100, '2017-01-10 00:00:00', '2017-01-11 00:00:00'),
  ('500元秒杀ipad2', 200, '2017-01-04 00:00:00', '2017-01-05 00:00:00'),
  ('300元秒杀小米4', 300, '2017-01-11 00:00:00', '2017-01-12 00:00:00'),
  ('200元秒杀红米note', 400, '2017-01-06 00:00:00', '2017-01-15 00:00:00');

-- 秒杀成功明细表
CREATE TABLE SUCCESS_KILLED(
	SECKILL_ID BIGINT NOT NULL COMMENT '秒杀商品ID',
	USER_PHONE BIGINT NOT NULL COMMENT '用户手机号',
	STATE TINYINT NOT NULL DEFAULT -1 COMMENT '状态标识：-1：无效 0：成功 1：已付款；',
	CREATE_TIME TIMESTAMP NOT NULL COMMENT '创建时间',
	PRIMARY KEY(SECKILL_ID,USER_PHONE), /*联合主键*/
	KEY IDX_CREATE_TIME(CREATE_TIME)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='秒杀成功明细表';
