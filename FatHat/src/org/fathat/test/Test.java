package org.fathat.test;

import org.fathat.dao.UserDao;
import org.fathat.model.User;
import org.fathat.proxy.DaoContainer;

/**
 * @author wyhong
 * @date 2018-5-4
 */
public class Test {

	@org.junit.Test
	public void test(){
		UserDao dao = (UserDao) DaoContainer.getDao("userDao");
		User user = new User(3, "wyh23", "男", 22);
		System.out.println(user);
//		dao.insert(user);
		System.out.println(dao.get(1));
//		dao.update("db", 1);
//		dao.delete(1);
	}
}
