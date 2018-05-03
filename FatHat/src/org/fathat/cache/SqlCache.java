package org.fathat.cache;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 * @author wyhong
 * @date 2018-5-3
 */
public class SqlCache {

	Map<String, Object> resultMap = new HashMap<String, Object>();

	Queue<String> abandonedQueue = new LinkedList<String>();
	
	private static SqlCache cache = new SqlCache();
	
	private SqlCache(){};
	
	public static SqlCache getInstance() {
		return cache;
	}

	public synchronized Object get(String sql) {
		if(!resultMap.containsKey(sql)) return null;
		abandonedQueue.remove(sql);
		abandonedQueue.add(sql);
		return resultMap.get(sql);
	}
	
	public synchronized boolean put(String sql, Object obj){
		if(obj instanceof List){
			List list = (List)obj;
			if(list.size() > 10) return false;
		}
		String remove = null;
		if(abandonedQueue.size() == 10) {
			remove = abandonedQueue.remove();
			resultMap.remove(remove);
		}
		//sql一定不存在，不用担心重复问题
		abandonedQueue.add(sql);
		resultMap.put(sql, obj);
		return true;
	}
}
