package pub.willow.a.taskservice.dao.db;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * 数据库工具类
 * 
 * @author albert.zhang
 * 
 */
public class DBUtil {
	private final static String SELECT_ERROR = "数据库查询失败！";
	private final static String UPDATE_ERROR = "数据库执行失败！";
	private final static String SQL_FORMAT_ERROR = "SQL语句格式错误!";
	private final static String CONNECT_CLOSE_ERROR = "数据库连接关闭失败！";
	public final static String REPEAT_TOKEN = "{@token@}";
	private Logger logger = Logger.getLogger("DBUtil");
	private ComboPooledDataSource cpds;
	private String dbName;

	public DBUtil(String dbName) {
		this.dbName = dbName;
		cpds = MysqlDBPoolFactory.getInstance().getComboPooledDataSource(dbName);
	}

	public List<Map<String, String>> executeQuery(String sql) {
		List<Map<String, String>> resultMap = new ArrayList<Map<String, String>>();
		Connection con = null;
		try {
			con = getConnection();
			ResultSet rs = con.createStatement().executeQuery(sql);
			ResultSetMetaData rsmd = rs.getMetaData();
			while (rs.next()) {
				HashMap<String, String> rowMap = new HashMap<String, String>();
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
//					rowMap.put(rsmd.getColumnLabel(i), rs.getString(i));
					String columnType = rsmd.getColumnTypeName(i);
					if (columnType.equals("LONGBLOB")) {
						Blob tweetBlob = rs.getBlob(i);
						int bolblen = (int) tweetBlob.length();
						byte[] data = tweetBlob.getBytes(1, bolblen);
						String content = new String(data);
						rowMap.put(rsmd.getColumnLabel(i), content);
					} else {
						rowMap.put(rsmd.getColumnLabel(i), rs.getString(i));
					}
				}
				resultMap.add(rowMap);
			}
			return resultMap;
		} catch (SQLException e) {
			e.printStackTrace();
			
			logger.error(SELECT_ERROR + "db: " + dbName + " sql:" + sql, e);
		} finally {
			close(con);
		}
		return resultMap;
	}

	public List<Map<String, String>> prepareExecuteQuery(String sql, String[] params) {
		List<Map<String, String>> resultMap = new ArrayList<Map<String, String>>();
		Connection con = null;
		PreparedStatement pst = null;
		try {
			con = getConnection();
			pst = con.prepareStatement(sql);
			for (int i = 0; i < params.length; i++) {
				pst.setString(i + 1, params[i]);
			}
			ResultSet rs = pst.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			while (rs.next()) {
				HashMap<String, String> rowMap = new HashMap<String, String>();
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					rowMap.put(rsmd.getColumnLabel(i), rs.getString(i));
				}
				resultMap.add(rowMap);
			}
			return resultMap;
		} catch (SQLException e) {
			logger.error(SELECT_ERROR + "db: " + dbName + " sql:" + sql, e);
		} finally {
			close(con, pst);
		}
		return resultMap;
	}

	public List<List<String>> executeQueryList(String sql) {
		List<List<String>> resultList = new ArrayList<List<String>>();
		Connection con = null;
		try {
			con = getConnection();
			ResultSet rs = con.createStatement().executeQuery(sql);
			ResultSetMetaData rsmd = rs.getMetaData();
			while (rs.next()) {
				List<String> coll1 = new ArrayList<String>();
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					coll1.add(rs.getString(i));
				}
				resultList.add(coll1);
			}
		} catch (SQLException e) {
			logger.error(SELECT_ERROR + "db: " + dbName + " sql:" + sql, e);
		} finally {
			close(con);
		}
		return resultList;
	}

	public int executeUpdate(String sql) {
		Connection con = null;
		Statement st = null;
		try {
			con = getConnection();
			st = con.createStatement();
			return st.executeUpdate(sql);
		} catch (SQLException e) {
			logger.error(UPDATE_ERROR + "db: " + dbName + " sql:" + sql, e);
		} finally {
			close(con, st);
		}
		return -1;
	}

	public String executeInsertReturnKey(String sql) {
		Connection con = null;
		try {
			con = getConnection();
			Statement st = con.createStatement();
			st.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
			ResultSet rs = st.getGeneratedKeys();
			if (rs.next()) {
				return rs.getString(1);
			}
		} catch (Exception e) {
			logger.error(UPDATE_ERROR + "db: " + dbName + " sql:" + sql, e);
		} finally {
			close(con);
		}
		return null;
	}

	public String prepareExecuteInsertReturnKey(String sql, String[] params) {
		Connection con = null;
		PreparedStatement pst = null;
		try {
			con = getConnection();
			pst = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			for (int i = 0; i < params.length; i++) {
				pst.setString(i + 1, params[i]);
			}
			pst.executeUpdate();
			ResultSet rs = pst.getGeneratedKeys();
			if (rs.next()) {
				return rs.getString(1);
			}
		} catch (Exception e) {
			logger.error(UPDATE_ERROR + "db: " + dbName + " sql:" + sql, e);
		} finally {
			close(con, pst);
		}
		return null;
	}

	public int prepareExecuteUpdate(String sql, String[] params) {
		Connection con = null;
		PreparedStatement pst = null;
		try {
			con = getConnection();
			pst = con.prepareStatement(sql);
			for (int i = 0; i < params.length; i++) {
				pst.setString(i + 1, params[i]);
			}
			return pst.executeUpdate();
		} catch (SQLException e) {
			logger.error(UPDATE_ERROR + "db: " + dbName + " sql:" + sql + params[0], e);
		} finally {
			close(con, pst);
		}
		return -1;
	}

	public int executeBatchPrepareUpdate(String sql, String tokenToRepeat, List<String[]> paramsList) {
		if (!sql.contains(REPEAT_TOKEN)) {
			logger.error(SQL_FORMAT_ERROR + "sql:" + sql);
			return 0;
		}

		Connection con = null;
		PreparedStatement pst = null;
		int count = 0;
		String token = "";
		if (paramsList.size() == 0) {
			return 0;
		}
		for (int i = 0; i < paramsList.size(); i++) {
			token += "," + tokenToRepeat;
		}
		sql = sql.replace(REPEAT_TOKEN, token.substring(1));
		try {
			con = getConnection();
			pst = con.prepareStatement(sql);
			int paramsIndex = 0;
			for (String[] params : paramsList) {
				for (int i = 0; i < params.length; i++) {
					pst.setString(paramsIndex * params.length + i + 1, params[i]);
				}
				paramsIndex++;
			}
			count = pst.executeUpdate();
		} catch (SQLException e) {
			logger.error(UPDATE_ERROR + "db: " + dbName + " sql:" + sql, e);
		} finally {
			close(con, pst);
		}
		return count;
	}

	public int prepareExecuteUpdateBatch(String sql, List<String[]> paramsList) {
		Connection con = null;
		PreparedStatement pst = null;
		int count = 0;
		try {
			con = getConnection();
			pst = con.prepareStatement(sql);
			for (String[] params : paramsList) {
				for (int i = 0; i < params.length; i++) {
					pst.setString(i + 1, params[i]);
					pst.addBatch();
					count++;
				}
			}
			try {
				pst.executeBatch();
			} catch (SQLException e) {
				logger.error(sql + "," + e.getMessage());
			}
		} catch (SQLException e) {
			logger.error(UPDATE_ERROR + "sql:" + sql, e);
		} finally {
			close(con, pst);
		}
		return count;
	}

	public int prepareExecuteUpdateBatchByDuplicate(String sql, List<String[]> paramsList) {
		Connection con = null;
		PreparedStatement pst = null;
		int count = 0;
		try {
			con = getConnection();
			pst = con.prepareStatement(sql);
			for (String[] params : paramsList) {
				for (int i = 0; i < params.length; i++) {
					pst.setString(i + 1, params[i]);
				}
				int resCode = pst.executeUpdate();
				if (resCode != 2) {
					count++;
				}
			}
		} catch (SQLException e) {
			logger.error(UPDATE_ERROR + "sql:" + sql, e);
		} finally {
			close(con, pst);
		}
		return count;
	}

	private Connection getConnection() throws SQLException {
		return cpds.getConnection();
	}

	private void close(Connection con) {
		try {
			if (con != null) {
				con.close();
			}
		} catch (SQLException e) {
			logger.error(CONNECT_CLOSE_ERROR, e);
		}
	}

	private void close(Connection con, Statement st) {
		try {
			if (st != null) {
				st.close();
			}
			close(con);
		} catch (SQLException e) {
			logger.error(CONNECT_CLOSE_ERROR, e);
		}
	}

	private void close(Connection con, PreparedStatement pst) {
		try {
			if (pst != null) {
				pst.close();
			}
			close(con);
		} catch (SQLException e) {
			logger.error(CONNECT_CLOSE_ERROR, e);
		}
	}
}
