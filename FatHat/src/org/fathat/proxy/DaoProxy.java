package org.fathat.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.fathat.annotation.Sql;
import org.fathat.datasource.DataSource;
import org.fathat.util.ConnectionUtil;

import com.mysql.jdbc.Connection;
/*
 * @author wyhong
 * @date 2018-4-28
 */
public class DaoProxy implements InvocationHandler {

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		//检查注解
		if(method.isAnnotationPresent(Sql.class)){
			Sql annotation = method.getAnnotation(Sql.class);
			String sql = annotation.value();
			//以后从pool获取
			Connection connection = ConnectionUtil.getConnection(new DataSource("db.properties"));
			PreparedStatement prepareStatement = connection.prepareStatement(sql);
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
			System.out.println(prepareStatement.toString());
			ResultSet rs = prepareStatement.executeQuery();
			System.out.println("warnings:"+prepareStatement.getWarnings());
			//获取返回值类型
			Class returnType = (Class) method.getReturnType();
			//以下是单个bean的情况
			Method[] methods = returnType.getMethods();
			Map<String, Method> setters = new HashMap<String, Method>();
			//获取所有setter
			for (Method _method : methods) {
				String methodName = _method.getName();
				if(!methodName.startsWith("set")) continue;
				String fieldName = methodName.substring(3);
				fieldName = (char)((int)fieldName.charAt(0) + 32) + fieldName.substring(1);
				setters.put(fieldName, _method);
			}
			ResultSetMetaData metaData = rs.getMetaData();
			Object instance = null;
			int count = metaData.getColumnCount();
			while(rs.next()){
				instance = returnType.newInstance();
				for (int i = 1; i <= count; i++) {
//					System.out.println("column name:"+metaData.getColumnName(i));
					Method target = setters.get(metaData.getColumnName(i));
					Class<?>[] paramClass = target.getParameterTypes();
//					System.out.println(paramClass[0].getSimpleName());
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
			rs.close();
			prepareStatement.close();
			ConnectionUtil.release(connection);
			return instance;
		}
		return null;
	}

}
