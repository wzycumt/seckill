package org.seckill.dao;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/spring-dao.xml")
public class SuccessKilledDaoTest {
    @Resource
    private SuccessKilledDao successKilledDao;

	@Test
	public void testInsertSuccessKilled() {
        long id = 1000L;
        long userPhone = 13211111111L;
        System.out.println(successKilledDao.insertSuccessKilled(id, userPhone));
	}

	@Test
	public void testQueryByIdWithSeckill() {
        long id = 1000L;
        long userPhone = 13211111111L;
        System.out.println(successKilledDao.queryByIdWithSeckill(id, userPhone));
	}

}
