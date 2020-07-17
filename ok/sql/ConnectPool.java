package sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;

/**
 * @author ����Ϊ���ݿ����ӹ����� ��ʼ��ʱ�������ݿ����ӳأ��������ݿ����ʱ�����ݿ����Ӵӳ���ȡ�ߣ�������Ͻ����ӷ������ӳ�
 *         getConnection������������Զ�ȷ�ϵ����ݿ����� getTransactionConnection()���������������
 *         releaseConnection���������ͷ����ӡ������ӳ�������������������ʱ�������ӣ�С������������ʱ�������ӳ�
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
				System.out.println("���ݿ�����ʧ��");
				e.printStackTrace();
			}
			return (conn);
		}
	}
}
