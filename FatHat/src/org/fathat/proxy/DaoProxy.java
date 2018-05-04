package org.fathat.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fathat.annotation.Sql;
import org.fathat.cache.SqlCache;
import org.fathat.exception.returnTypeMismatchedException;
import org.fathat.pool.ConnectionPool;
import org.fathat.util.ClassUtil;
import org.fathat.util.JdbcUtil;

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
			String sql = annotation.value().trim();
			boolean isSelect = "select".equalsIgnoreCase(sql.split(" ")[0]);
			if(isSelect){
				return handleSelect(sql, method, args);
			}else{
				handleUpdate(sql, method, args);
			}

		}
		return null;
	}

	private void handleUpdate(String sql, Method method, Object[] args) {
		//增删改查
	}

	//异常要不优化一下？？直接Exception算了
	private Object handleSelect(String sql, Method method, Object[] args) 
			throws Exception {
		//以后从pool获取
		Connection connection = ConnectionPool.getConnection();
		PreparedStatement prepareStatement = connection.prepareStatement(sql);
		//约定sql开头不能有空格
		JdbcUtil.setParam(prepareStatement, method, args);
		//查缓存
		SqlCache cache = SqlCache.getInstance();
		String statement = prepareStatement.toString().split(":")[1];
		Object obj = cache.get(statement);
		if(obj != null) return obj;
		//缓存没有
		System.out.println(prepareStatement.toString());
		ResultSet rs = prepareStatement.executeQuery();
		System.out.println("warnings:"+prepareStatement.getWarnings());
		//获取返回值类型
		Class returnType = (Class) method.getReturnType();
		List list = null;
		if(returnType == List.class){
			ParameterizedType genericReturnType = (ParameterizedType) method.getGenericReturnType();
			returnType = (Class) genericReturnType.getActualTypeArguments()[0];
			list = new ArrayList();
		}
		if(list == null && JdbcUtil.getRowCount(rs) > 1){
			throw new returnTypeMismatchedException("return type is a single model but the result set has more than 1 row");
		}
		ResultSetMetaData metaData = rs.getMetaData();
		Object instance = null;
		if(ClassUtil.isBasicType(returnType)){
			//返回基本数据类型
			while(rs.next()){
				instance = JdbcUtil.constructBasicObject(returnType, rs);
				if(list != null){
					list.add(instance);
				}else{
					break;
				}
			}
		}else{
			//返回bean
			Method[] methods = returnType.getMethods();
			Map<String, Method> setters = new HashMap<String, Method>();
			ClassUtil.fillSetters(setters, methods);
			while(rs.next()){
				instance = returnType.newInstance();
				JdbcUtil.setAttributes(instance, rs, setters, metaData);
				if(list != null){
					list.add(instance);
				}else{
					break;
				}
			}
		}
		rs.close();
		prepareStatement.close();
		ConnectionPool.returnConnection(connection);
		if(list != null) {
			cache.put(statement, list);
			return list;
		}
		cache.put(statement, instance);
		return instance;
	}

}
