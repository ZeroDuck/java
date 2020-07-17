package sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;

/**
 * @author 本类为数据库连接管理类 初始化时建立数据库连接池，当有数据库操作时将数据库连接从池中取走，操作完毕将连接返回连接池
 *         getConnection（）方法获得自动确认的数据库连接 getTransactionConnection()方法获得事务连接
 *         releaseConnection方法用于释放连接。当连接池中数量超过正常数量时结束连接，小于正常连接数时返回连接池
 */
public class ConnectPool {
	private static LinkedList<Connection> connectionPool = new LinkedList<Connection>();
	static {
		for (int i = 0; i < 5; i++) {
			Connection conn = new ConnectPool().new Connect().getConnection();
			connectionPool.add(conn);
		}
	}

	public Connection getConnection() {
		if (connectionPool.isEmpty()) {
			return new Connect().getConnection();
		} else
			return connectionPool.removeFirst();
	}

	public Connection getTransactionConnection() throws SQLException {
		Connection conn = getConnection();
		conn.setAutoCommit(false);
		return conn;
	}

	public void releaseConnection(Connection conn) {
		try {
			if (connectionPool.size() < 5) {
				conn.setAutoCommit(true);
				connectionPool.add(conn);
			} else
				conn.close();
		} catch (SQLException e) {
			System.out.println("release connection failed!");
		}
	}

	class Connect {
		private final String VER = "com.mysql.cj.jdbc.Driver";
		private final String URL = "jdbc:mysql://localhost:3306/book?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai";
		private final String USER = "root";
		private final String PASS = "123456";
		Connection conn = null;

		public Connection getConnection() {
			try {
				Class.forName(VER);

			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			try {
				conn = DriverManager.getConnection(URL, USER, PASS);
			} catch (SQLException e) {
				System.out.println("数据库连接失败");
				e.printStackTrace();
			}
			return (conn);
		}
	}
}
