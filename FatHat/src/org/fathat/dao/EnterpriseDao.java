package org.fathat.dao;

import org.fathat.annotation.Dao;
import org.fathat.annotation.Param;
import org.fathat.annotation.Sql;
import org.fathat.model.Enterprise;

/**
 * @author wyhong
 * @date 2018-4-28
 */
@Dao("enterpriseDao")
public interface EnterpriseDao {

	@Sql("SELECT * FROM enterprise WHERE id = ?")
	Enterprise get(int id);
	
	@Sql("SELECT * FROM enterprise WHERE name = ?")
	Enterprise getByName(String name);
	
	@Sql("SELECT * FROM enterprise WHERE address= ? AND name = ?")
	Enterprise getByInfo(String address, String name);
}
