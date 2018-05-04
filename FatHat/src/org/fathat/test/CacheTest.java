package org.fathat.test;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

/**
 * @author wyhong
 * @date 2018-5-4
 */
public class CacheTest {

	@Test
	public void test(){
		String sql = "  update   sr_stock,  sr_user  set id = 5  ";
		System.out.println(sql.trim());
//		System.out.println(StringUtils.deleteWhitespace(StringUtils.substringBefore(StringUtils.substringAfter(sql, "update"), "set")));
		String delete = "delete from sr_stock where a=1";
		System.out.println(StringUtils.deleteWhitespace(StringUtils.substringBefore(StringUtils.substringAfter(delete, "from"), "where")));
	}
	
}
