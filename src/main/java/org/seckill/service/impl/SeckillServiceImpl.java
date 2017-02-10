package org.seckill.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.seckill.dao.SeckillDao;
import org.seckill.dao.SuccessKilledDao;
import org.seckill.dao.cache.RedisDao;
import org.seckill.entity.Seckill;
import org.seckill.entity.SuccessKilled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import com.dyuproject.protostuff.runtime.RuntimeSchema;

import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.enums.SeckillStateEnum;
import org.seckill.exception.RepeatKillExecution;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;
import org.seckill.service.SeckillService;

/**
 * 
 * @author WZY
 *
 */
// @Component @Service @Dao @Controller
@Service
public class SeckillServiceImpl implements SeckillService {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	// 注入Service依赖
	@Autowired
	private SeckillDao seckillDao;

	@Autowired
	private SuccessKilledDao successKilledDao;

	@Autowired
	private RedisDao redisDao;

	private final String slat = "fdsae23re@#R@FEEG@@#<<>L";

	public List<Seckill> getSeckillList() {
		return seckillDao.queryAll(0, 100);
	}

	public Seckill getById(long seckillId) {
		return seckillDao.queryById(seckillId);
	}

	private RuntimeSchema<Seckill> schema = RuntimeSchema.createFrom(Seckill.class);

	public Exposer exportSeckillUrl(long seckillId) {
		// 优化点：缓存 优化
		Seckill seckill = redisDao.getSeckill(seckillId);
		if (seckill == null) {
			seckill = seckillDao.queryById(seckillId);
			if (null == seckill) {
				return new Exposer(false, seckillId);
			} else {
				redisDao.putSeckill(seckill);
			}
		}

		Date startTime = seckill.getStartTime();
		Date endTime = seckill.getEndTime();
		Date nowTime = new Date();
		if (nowTime.getTime() < startTime.getTime() || nowTime.getTime() > endTime.getTime()) {
			return new Exposer(false, nowTime.getTime(), startTime.getTime(), endTime.getTime());
		}
		String md5 = getMD5(seckillId);
		return new Exposer(true, md5, seckillId);
	}

	private String getMD5(long seckillId) {
		String base = seckillId + "/" + slat;
		return DigestUtils.md5DigestAsHex(base.getBytes());
	}

	@Transactional
	/**
	 * 使用注解控制事务方法的优点 1.开发团队达成一致约定，明确标注事务方法的编程风格。
	 * 2.保证事务方法的执行时间尽可能短，不要穿插其他网络操作，PRC/HTTP请求或者剥离到事务方法外部。
	 * 3.不是所有的方法都需要事务，如只有一条修改操作，或只读操作。
	 */
	public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
			throws SeckillException, RepeatKillExecution, SeckillCloseException {
		if (null == md5 || !md5.equals(getMD5(seckillId))) {
			throw new SeckillException("seckill data rewrite");
		}
		// 执行秒杀逻辑：减库存，记录购买行为
		Date nowTime = new Date();
		try {
			// 记录购买行为
			int insertCount = successKilledDao.insertSuccessKilled(seckillId, userPhone);
			if (insertCount <= 0) {
				throw new RepeatKillExecution("seckill repeated");
			} else {
				int updateCount = seckillDao.reduceNumber(seckillId, nowTime);
				if (updateCount <= 0) {
					throw new SeckillCloseException("seckill is closed");
				} else {
					SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
					return new SeckillExecution(seckillId, SeckillStateEnum.SUCCESS, successKilled);
				}
			}

		} catch (SeckillCloseException e) {
			throw e;
		} catch (RepeatKillExecution e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			// 所有编译期异常 转化为运行期异常
			throw new SeckillException("seckill inner error:" + e.getMessage());
		}
	}

	public SeckillExecution executeSeckillProcedure(long seckillId, long userPhone, String md5) {
		if (md5 == null || !md5.equals(getMD5(seckillId))) {
			return new SeckillExecution(seckillId, SeckillStateEnum.DATA_REWRITE);
		}
		Date killTime = new Date();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("seckillId", seckillId);
		map.put("phone", userPhone);
		map.put("killTime", killTime);
		map.put("result", null);
		try {
			seckillDao.killByProcedure(map);
			int result = MapUtils.getInteger(map, "result", -2);
			if (result == 1) {
				SuccessKilled sk = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
				return new SeckillExecution(seckillId, SeckillStateEnum.SUCCESS, sk);
			} else {
				return new SeckillExecution(seckillId, SeckillStateEnum.stateOf(result));
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return new SeckillExecution(seckillId, SeckillStateEnum.INNER_ERROR);
		}
	}

}
