package org.fathat.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Map;

import org.fathat.exception.UnsupportReturnType;

/**
 * @author wyhong
 * @date 2018-5-4
 */
public class JdbcUtil {

	public static Object constructBasicObject(Class returnType, ResultSet rs) throws Exception {
		Object instance = null;
		Constructor constructor = null;
		if(Integer.class == returnType){
			constructor = returnType.getDeclaredConstructor(int.class);
			instance = constructor.newInstance(rs.getInt(1));
		}else if(Double.class == returnType){
			constructor = returnType.getDeclaredConstructor(double.class);
			instance = constructor.newInstance(rs.getDouble(1));
		}else if(Long.class == returnType){
			constructor = returnType.getDeclaredConstructor(long.class);
			instance = constructor.newInstance(rs.getLong(1));
		}else if(Date.class == returnType){
			constructor = returnType.getDeclaredConstructor(long.class);
			instance = constructor.newInstance(rs.getLong(1));
		}else if(String.class == returnType){
			constructor = returnType.getDeclaredConstructor(String.class);
			instance = constructor.newInstance(rs.getString(1));
		}else if(Boolean.class == returnType){
			constructor = returnType.getDeclaredConstructor(boolean.class);
			instance = constructor.newInstance(rs.getBoolean(1));
		}else{
			throw new UnsupportReturnType(returnType+" is unsupport!");
		}
		return instance;
	}
	
	public static int getRowCount(ResultSet rs) throws SQLException {
		int rows = 0;
		rs.last();
		rows = rs.getRow();
		rs.beforeFirst();
		return rows;
	}
	
	public static void setAttributes(Object instance, ResultSet rs, 
			Map<String, Method> setters, ResultSetMetaData metaData) 
					throws SQLException, IllegalAccessException, 
					IllegalArgumentException, InvocationTargetException {
		for (int i = 1; i <= metaData.getColumnCount(); i++) {
			//			System.out.println("column name:"+metaData.getColumnName(i));
			Method target = setters.get(metaData.getColumnName(i));
			Class<?>[] paramClass = target.getParameterTypes();
			//			System.out.println(paramClass[0].getSimpleName());
			if(short.class == paramClass[0] || int.class == paramClass[0]){
				target.invoke(instance, rs.getInt(i));
			}else if(long.class == paramClass[0]){
				target.invoke(instance, rs.getLong(i));
			}else if(Date.class == paramClass[0]){
				target.invoke(instance, rs.getDate(i));
			}else if(boolean.class == paramClass[0]){
				target.invoke(instance, (boolean)rs.getBoolean(i));
			}else if(float.class == paramClass[0] || double.class == paramClass[0]){
				target.invoke(instance, (double)rs.getDouble(i));
			}else{
				target.invoke(instance, (String)rs.getString(i));
			}
		}
	}

	public static void setParam(PreparedStatement prepareStatement, Method method, Object[] args) throws SQLException {
		//获取方法参数
		Class<?>[] parameterTypes = method.getParameterTypes();
		//设置sql（方法参数顺序须按声明顺序写）
		for(int i = 0; i < parameterTypes.length; i++){
			if(int.class == parameterTypes[i]){
				prepareStatement.setInt(i + 1, (int) args[i]);
			}else if(short.class == parameterTypes[i]){
				prepareStatement.setShort(i + 1, (short) args[i]);
			}else if(long.class == parameterTypes[i]){
				prepareStatement.setLong(i + 1, (long) args[i]);
			}else if(float.class == parameterTypes[i]){
				prepareStatement.setFloat(i + 1, (float) args[i]);
			}else if(double.class == parameterTypes[i]){
				prepareStatement.setDouble(i + 1, (double) args[i]);
			}else if(boolean.class == parameterTypes[i]){
				prepareStatement.setBoolean(i + 1, (boolean) args[i]);
			}else if(Date.class == parameterTypes[i]){
				prepareStatement.setDate(i + 1, (Date) args[i]);
			}else if(Timestamp.class == parameterTypes[i]){
				prepareStatement.setTimestamp(i + 1, (Timestamp) args[i]);
			}else{
				prepareStatement.setString(i + 1, (String) args[i]);
			}
		}
	}
}
