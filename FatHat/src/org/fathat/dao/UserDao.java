package org.fathat.dao;

import org.fathat.annotation.Dao;
import org.fathat.annotation.Sql;
import org.fathat.model.User;

/**
 * @author wyhong
 * @date 2018-5-4
 */
@Dao("userDao")
public interface UserDao {

	@Sql("#INSERT INTO user " +
			"VALUES(?,?,?,?)")
	Integer insert(User user);
	
	@Sql("SELECT * " +
			"FROM user " +
			"WHERE id = ?")
	User get(int id);
	
	@Sql("UPDATE user " +
			"SET name = ? " +
			"WHERE id = ?")
	Integer update(String name, int id);
	
	@Sql("DELETE FROM user " +
			"WHERE id = ?")
	Integer delete(int id);
	
}
