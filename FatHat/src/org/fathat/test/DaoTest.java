package org.fathat.test;

import org.fathat.dao.EnterpriseDao;
import org.fathat.proxy.DaoContainer;
import org.junit.Test;

/**
 * @author wyhong
 * @date 2018-4-28
 */
public class DaoTest {

	@Test
	public void test(){
		EnterpriseDao enterpriseDao = (EnterpriseDao) DaoContainer.getDao("enterpriseDao");
		System.out.println(enterpriseDao.getByInfo("北京","网易"));
	}
}
