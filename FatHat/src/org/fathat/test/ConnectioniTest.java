package org.fathat.test;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.fathat.datasource.DataSource;
import org.fathat.util.ConnectionUtil;
import org.junit.Test;

import com.mysql.jdbc.Connection;

/**
 * @author wyhong
 * @date 2018-4-28
 */
public class ConnectioniTest {

	@Test
	public void test(){
		Connection connection = ConnectionUtil.getConnection(new DataSource("db.properties"));
		PreparedStatement prepareStatement = null;
		try {
			 prepareStatement = connection.prepareStatement("select * from enterprise where id = ?");
			 prepareStatement.setInt(1, 1);
			 System.out.println(prepareStatement.toString());
			 ResultSet resultSet = prepareStatement.executeQuery();
			 while(resultSet.next()){
				 System.out.print(resultSet.getInt(1)+" ");
				 System.out.print(resultSet.getString(2)+" ");
				 System.out.print(resultSet.getString(3)+" ");
				 System.out.print(resultSet.getString(4)+" ");
				 System.out.println();
			 }
			 resultSet.close();
			 prepareStatement.close();
			 ConnectionUtil.release(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
}
