package pub.willow.a.taskservice.dao.base;

import java.util.HashMap;
import java.util.Map;


public class BaseDao {
	private static Map<String, DBUtil> dbPool = new HashMap<String, DBUtil>();
	protected static final String A_PROJECT = "a_project";
	/**
	 * 获取数据库连接，通过dbName
	 * @param dbName
	 * @return
	 */
	public static DBUtil getDbUtilByDbName(String dbName) {
		DBUtil dbUtil = dbPool.get(dbName);
		if (dbUtil == null) {
			dbUtil = new DBUtil(dbName);
			dbPool.put(dbName, dbUtil);
		}
		return dbUtil;
	}
}
