package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;


import entity.Book2;
import entity.BorrowRecord;
import entity.User;
import exception.BookNotFoundException;
import exception.SQLUpdateException;
import exception.UserNotFoundException;


public class BookDao {
	private PreparedStatement pstmt;
public boolean updateUser(Connection conn,User user) throws SQLUpdateException {
	String sql = "update user set book_num=?,cost_amount=?";	
	try {		
		PreparedStatement pstmt = conn.prepareStatement(sql);		
		pstmt.setInt(1,user.getBookNum());
		pstmt.setDouble(2, user.getCostAmount());
		pstmt.executeUpdate();
		pstmt.close();
		return true ;
	}catch (SQLException e) {
		throw new SQLUpdateException("update User");
	}
}

public boolean updateReturnUser(Connection conn,User user) throws SQLUpdateException {
	String sql = "update user set book_num=?,cost_amount=?";
	String sql2 = "SELECT payment FROM borrow Where return_time!=0 ORDER BY ABS(NOW() - return_time) ASC LIMIT 0, 1";
	double payment =0;
	
	try {
		PreparedStatement pstmt2 =conn.prepareStatement(sql2);
		ResultSet re = pstmt2.executeQuery();
		if(re.next()) {
			payment=re.getDouble(1);
		}
	} catch(SQLException e) {
		System.out.println("Update User error.");
	}
	
	try {		
		PreparedStatement pstmt = conn.prepareStatement(sql);		
		pstmt.setInt(1,user.getBookNum());
		pstmt.setDouble(2, user.getCostAmount()-payment);
		pstmt.executeUpdate();
		pstmt.close();
		return true ;
	}catch (SQLException e) {
		throw new SQLUpdateException("update User");
	}
}


public boolean insertUser(Connection conn,User user) throws SQLUpdateException {

	String sql = "insert into User (type,user_name) values (?,?)";
	try {
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, user.getType());
		pstmt.setString(2, user.getUserName());
		if (pstmt.executeUpdate()!=0) {
			return true ;	
		}else {
			System.out.println("userName is exist!");
			return false;	
		}
    }catch (SQLException e) {
	    throw new SQLUpdateException("insert User");
     }
}

public User getUserByName(Connection conn,String userName) throws SQLUpdateException, UserNotFoundException {
	String sql = "select type,user_name,book_num,cost_amount from user where user_name=?";
	try {	
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, userName);		
		ResultSet rs=pstmt.executeQuery(); 
		if (rs.next()) {
			return new User(rs.getString(2),rs.getString(1),rs.getInt(3),rs.getDouble(4));
		}else throw new UserNotFoundException();
	} catch (SQLException e) {
		e.printStackTrace();
		throw new SQLUpdateException("query User");
	}		
}

public String getUserIdByName(Connection conn, String userName) throws SQLUpdateException, UserNotFoundException {
	
	String sql = "select user_id from user where user_name=?";
	try {	
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, userName);		
		ResultSet rs=pstmt.executeQuery(); 
		if (rs.next()) {
			return rs.getString(1);
		}else throw new UserNotFoundException();
	} catch (SQLException e) {
		throw new SQLUpdateException("query UserId");
	}		
}

public Book2 getBookByBookId(Connection conn, String bookId) throws BookNotFoundException, SQLUpdateException {
	String sql = "select * from book where book_id=?";
	try {
		pstmt= conn.prepareStatement(sql);
		pstmt.setString(1, bookId);		
		ResultSet rs=pstmt.executeQuery(); 
		if (rs.next()) {
			boolean inStore = rs.getInt(5) == 1 ? true : false;
			return new Book2(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), inStore, rs.getString(6));
		}else throw new BookNotFoundException("无此图书");
	} catch (SQLException e) {
		throw new SQLUpdateException("query Book");
	}finally {
		try {
			pstmt.close();
		} catch (SQLException e) {
			System.out.println("close statement failed!");
		}
	}
}
//按照书名查询图书，返回同名图书的列表
public List<Book2> getBooksByBookName(Connection conn, String bookName) {
	// TODO Auto-generated method stub
	return null;
}

public LinkedList<BorrowRecord> getBorrowRecordByUserId(Connection conn, String userId){
	String sql = "select book_id, borrow_time, return_time, payment from borrow where user_id=?";
	LinkedList<BorrowRecord> borrowRecords = new LinkedList<>();
	try {
		pstmt = conn.prepareStatement(sql);
		pstmt.setInt(1, Integer.parseInt(userId));
		ResultSet rs = pstmt.executeQuery();
		while (rs.next()) {
			borrowRecords.add(new BorrowRecord(rs.getInt(1)
					,Integer.parseInt(userId)
					,rs.getTimestamp(2).toLocalDateTime()
					,rs.getTimestamp(3) == null ? null : rs.getTimestamp(3).toLocalDateTime()
					,rs.getDouble(4) ));
		}
	} catch (SQLException e) {
		//e.printStackTrace();
		System.out.println("您还有额外未还书目未记录");
	}
	if (borrowRecords.size() == 0){
		System.out.println("暂无任何借记记录");
	}
	return borrowRecords;
}
//借书时修改数据库中book实体的instore与borrower属性	
public boolean updateBookBorrow(Connection conn, String userName, String bookId) throws SQLUpdateException {
	String sql="update book set is_instore=0,borrower=? where book_id=?";
	try {	
		pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, userName);
		pstmt.setString(2, bookId);	
		if(pstmt.executeUpdate()!=0) {
			return true;
		}else throw new SQLUpdateException("update Book Borrowed");
	} catch (SQLException e) {
		throw new SQLUpdateException("update Book Borrowed");
	}finally {
		try {
			pstmt.close();
		} catch (SQLException e) {
			System.out.println("close statement failed!");
		}
	}
}
// 还书时修改数据库中book实体的instore与borrower属性
public boolean updateBookReturn(Connection conn,String bookId) throws SQLUpdateException {
    try {
        String sql="update book set is_instore=1,borrower='' where book_id=?";
        pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, bookId);
        if(pstmt.executeUpdate()!=0) {
            return true;
        }else throw new SQLUpdateException("update Book Return");
    } catch (SQLException e) {
        throw new SQLUpdateException("update Book Return");
    }finally {
        try {
            pstmt.close();
        } catch (SQLException e) {
            System.out.println("close statement failed!");
        }
    }
}
public LinkedList<User> getAllUsers(Connection conn){
	String sql = "select type, user_name, book_num, cost_amount from user";
	LinkedList<User> users = new LinkedList<>();
	try {
		pstmt = conn.prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery();
		while (rs.next()) {
			users.add(new User(rs.getString(2)
					,rs.getString(1)
					,rs.getInt(3)
					,rs.getDouble(4)));
		}
	} catch (SQLException e) {
		e.printStackTrace();
	}
	return users;
}
public LinkedList<Book2> getAllBooks(Connection conn){
	String sql = "select book_id, book_name, author, publish_house, is_instore, borrower from book";
	LinkedList<Book2> books = new LinkedList<>();
	try {
		pstmt = conn.prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery();
		while (rs.next()){
			books.add(new Book2(rs.getInt(1)
					,rs.getString(2)
					,rs.getString(3)
					,rs.getString(4)
					,rs.getInt(5)== 1
					,rs.getString(6)));
		}
	} catch (SQLException e) {
		e.printStackTrace();
	}
	return books;
}
//借书时添加借阅记录
public boolean insertBorrowRecord(Connection conn, String userId, String bookId) throws SQLUpdateException {
	String sql="insert into borrow (book_id,user_id,borrow_time,return_time) values (?,?,?,?)";
	try {	
		pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, bookId);
		pstmt.setString(2, userId);	
		pstmt.setObject(3, LocalDateTime.now());
		pstmt.setObject(4, 0);
		if(pstmt.executeUpdate()!=0) {
			return true;
		}else throw new SQLUpdateException("insert BorrowRecord");
	} catch (SQLException e) {
		throw new SQLUpdateException("insert BorrowRecord");
	}finally {
		try {
			pstmt.close();
		} catch (SQLException e) {
			System.out.println("close statement failed!");
		}
	}			
}
//还书时更新借阅记录
/*在borrow表中找到包含bookId且return_time为null的记录，更新return_time为当前时间（LocalDateTime类型)，payment为经计算的借阅超期罚金
 *使用pstmt传递return_time参数 例：pstmt.setObject(1, LocalDateTime.now());
 */
public boolean updateBorrowRecord(Connection conn,String bookId) throws SQLUpdateException {
	String sql="update borrow set return_time=?,payment=? where book_id=? and return_time=0";
	String sql2 = "SELECT TIMESTAMPDIFF(DAY,borrow_time,NOW()) from borrow where book_id=? and return_time=0";
	ResultSet re=null;
	int days =0;
	try {
		PreparedStatement pstmt1 = conn.prepareStatement(sql2);
		pstmt1.setString(1, bookId);
		re=pstmt1.executeQuery();
		if(re.next())
		{
			days=re.getInt(1);
		}
	} catch (SQLException e) {
		System.out.println(days);
		System.out.println("updateBorrowRecord的sql2执行有问题");
	}
	double payment=0;
	if(days>10)
	{
		payment=(days-10)*0.1;
	}
	else 
	{
		payment = 10;
	}
    try {	
        pstmt = conn.prepareStatement(sql);
        pstmt.setObject(1, LocalDateTime.now());	
        pstmt.setDouble(2, payment);
        pstmt.setString(3, bookId);
        if(pstmt.executeUpdate()!=0) {
			return true;
		}else throw new SQLUpdateException("update BorrowRecord");
	} catch (SQLException e) {
		throw new SQLUpdateException("update BorrowRecord");
	}finally {
		try {
			pstmt.close();
		} catch (SQLException e) {
			System.out.println("close statement failed!");
		}
    }
			
}


 /*
  * 插入图书表,将图书插入表中
  */

public boolean insertbook(Connection conn,Book2 book) throws SQLUpdateException
{
	String sql = "insert into book (book_name,author,publish_house,is_instore) values (?,?,?,?)";
	try	{
		pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, book.getBookName());
		pstmt.setString(2, book.getAuthor());
		pstmt.setString(3, book.getPublishHouse());
		pstmt.setInt(4, 1);
		if(pstmt.executeUpdate()!=0) {
			return true;
		}else throw new SQLUpdateException("insert book");
	} catch (SQLException e) {
		throw new SQLUpdateException("insert book");
	}finally {
		try {
			pstmt.close();
		} catch (SQLException e) {
			System.out.println("close statement failed!");
		}
    
	}
	
}



}