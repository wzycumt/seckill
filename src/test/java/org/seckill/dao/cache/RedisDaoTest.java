package org.seckill.dao.cache;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dao.SeckillDao;
import org.seckill.entity.Seckill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/spring-dao.xml")
public class RedisDaoTest {
	private long id = 1001;

	@Autowired
	private RedisDao redisdao;

	@Autowired
	private SeckillDao seckillDao;

	@Test
	public void testSeckill() {
		Seckill seckill = redisdao.getSeckill(id);
		if (seckill == null) {
			seckill = seckillDao.queryById(id);
			if (seckill != null) {
				String result = redisdao.putSeckill(seckill);
				System.out.println(result);
				seckill = redisdao.getSeckill(id);
				System.out.println(seckill);
			}
		}
	}

}
