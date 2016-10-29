package pub.willow.a.taskservice.dao.db;

import java.beans.PropertyVetoException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import pub.willow.a.taskservice.utils.PropConfig;

/**
 * 数据库连接池
 * @author albert.zhang
 *
 */
public class MysqlDBPoolFactory {

    private final int DEFAULT_POOL_SIZE = 5;
    private final int MIN_POOL_SIZE = 1;
    private final int MAX_POOL_SIZE = 100;
    private final int ACQUIRE_INCREMENT = 1;

    private final int MAX_IDLE_TIME = 60 * 60;
    private final int ACQUIRE_RETRY_ATTEMPTS = 3;
    private final int ACQUIRE_RETRY_DELAY = 1000;
    private final int IDLE_CONNECTION_TEST_PERIOD = 60;

    private Map<String, ComboPooledDataSource> dbPoolMap = new HashMap<String, ComboPooledDataSource>();

    private static MysqlDBPoolFactory poolFactory = new MysqlDBPoolFactory();

    private MysqlDBPoolFactory() {
    }

    public static MysqlDBPoolFactory getInstance() {
        return poolFactory;
    }
    
    public ComboPooledDataSource getComboPooledDataSource(String dbName) {
        synchronized (dbPoolMap) {
            ComboPooledDataSource ds = dbPoolMap.get(dbName);
            if (ds == null || !checkAlive(ds)) {
                ds = createComboPooledDataSourceByXML(dbName);
                dbPoolMap.put(dbName, ds);
            }
            return ds;
        }
    }
    
    private boolean checkAlive(ComboPooledDataSource ds) {
        try {
            ds.getConnection().close();
            return true;
        } catch (SQLException e) {
            ds.close();
            return false;
        }
    }

    private ComboPooledDataSource createComboPooledDataSourceByXML(String dbName) {
    	String jdbcUrl = PropConfig.getFileConfig("database", dbName+".conn");
    	String user = PropConfig.getFileConfig("database", dbName+".user");
    	String password = PropConfig.getFileConfig("database", dbName+".password");
    	
    	try {
			return createComboPooledDataSource(jdbcUrl, user, password);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		return null;
    }

    private ComboPooledDataSource createComboPooledDataSource(String jdbcUrl, String user, String password) throws PropertyVetoException {
        ComboPooledDataSource ds = initDBPool();
        ds.setJdbcUrl(jdbcUrl);
        ds.setUser(user);
        ds.setPassword(password);
        return ds;
    }

    private ComboPooledDataSource initDBPool() throws PropertyVetoException {
        ComboPooledDataSource ds = new ComboPooledDataSource();
        ds.setDriverClass("com.mysql.jdbc.Driver");
        ds.setInitialPoolSize(DEFAULT_POOL_SIZE);
        ds.setMaxPoolSize(MAX_POOL_SIZE);
        ds.setMinPoolSize(MIN_POOL_SIZE);
        ds.setAcquireIncrement(ACQUIRE_INCREMENT);
        ds.setTestConnectionOnCheckin(true);
        ds.setTestConnectionOnCheckout(true);
        ds.setIdleConnectionTestPeriod(IDLE_CONNECTION_TEST_PERIOD);
        ds.setMaxIdleTime(MAX_IDLE_TIME);
        ds.setIdleConnectionTestPeriod(60);
        ds.setAutoCommitOnClose(true);
        ds.setAcquireRetryAttempts(ACQUIRE_RETRY_ATTEMPTS);
        ds.setAcquireRetryDelay(ACQUIRE_RETRY_DELAY);
        return ds;
    }
}