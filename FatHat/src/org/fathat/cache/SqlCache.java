package org.fathat.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.fathat.exception.UnqualifiedSqlException;

/**
 * @author wyhong
 * @date 2018-5-3
 */
public class SqlCache {

	Map<String, Object> resultMap = new HashMap<String, Object>();

	Queue<String> abandonedQueue = new LinkedList<String>();

	private static SqlCache cache = new SqlCache();

	private SqlCache() {
	};

	public static SqlCache getInstance() {
		return cache;
	}

	public synchronized Object get(String sql) {
		if (!resultMap.containsKey(sql))
			return null;
		abandonedQueue.remove(sql);
		abandonedQueue.add(sql);
		return resultMap.get(sql);
	}

	public synchronized boolean put(String sql, Object obj) {
		if (obj instanceof List) {
			List list = (List) obj;
			if (list.size() > 10)
				return false;
		}
		String remove = null;
		if (abandonedQueue.size() == 10) {
			remove = abandonedQueue.remove();
			resultMap.remove(remove);
		}
		// sql一定不存在，不用担心重复问题
		abandonedQueue.add(sql);
		resultMap.put(sql, obj);
		return true;
	}

	public synchronized int update(String updateSql) {
		updateSql = updateSql.toLowerCase().trim();
		String[] parts = updateSql.split(" ");
		if (parts.length == 0 || parts == null)
			return -1;
		String op = parts[0];
		String[] tableNames = null;
		if (op.equals("insert")) {
			String beforeFrom = StringUtils.substringBefore(updateSql, "from");
			String tableToBeInserted = null;
			if (beforeFrom.contains("("))
				tableToBeInserted = StringUtils.substringBefore(
						StringUtils.substringAfter(beforeFrom, "into"), "(");
			else
				tableToBeInserted = StringUtils.substringAfter(beforeFrom,
						"into");
			tableNames = new String[] { StringUtils
					.deleteWhitespace(tableToBeInserted) };
		} else if (op.equals("update")) {
			tableNames = StringUtils.deleteWhitespace(
					StringUtils.substringBefore(
							StringUtils.substringAfter(updateSql, "update"),
							"set")).split(",");
		} else {
			tableNames = StringUtils.deleteWhitespace(
					StringUtils.substringBefore(
							StringUtils.substringAfter(updateSql, "from"),
							"where")).split(",");
		}
		if (tableNames.length < 1)
			return -1;
		Set<String> keySet = resultMap.keySet();
		int count = 0;
		for (String sql : keySet) {
			for (String tableName : tableNames) {
				if (sql.contains(tableName))
					resultMap.remove(sql);
				count++;
				break;
			}
		}
		return count;
	}
}
