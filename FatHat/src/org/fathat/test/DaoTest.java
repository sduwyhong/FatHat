package org.fathat.test;

import java.util.List;

import org.fathat.dao.EnterpriseDao;
import org.fathat.model.Enterprise;
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
		List<Enterprise> enterprises = enterpriseDao.getAll();
		for (Enterprise enterprise : enterprises) {
			System.out.println(enterprise);
		}
		System.out.println();
		List<Enterprise> _enterprises = enterpriseDao.getAll();
		for (Enterprise enterprise : _enterprises) {
			System.out.println(enterprise);
		}
//		System.out.println(enterpriseDao.getNameById(1));
//		System.out.println(enterpriseDao.getIdByName("网易"));
	}
}
