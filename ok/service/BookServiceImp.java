package service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import constant.ConstantValue;
import dao.BookDao;

import entity.Book2;
import entity.BorrowRecord;
import entity.User;
import exception.BookNotFoundException;
import exception.BookNotInstoreException;
import exception.SQLUpdateException;
import exception.UserMaxBorrowedException;
import exception.UserNotFoundException;
import exception.UserNotLoginException;
import exception.UserOweFeeException;
import exception.UserTypeErrorException;
import sql.ConnectPool;

public class BookServiceImp implements BookService {
	private BookDao bookDao;
	private ConnectPool connectPool = new ConnectPool();

	public BookServiceImp(BookDao bookDao) {
		this.bookDao = bookDao;
	}

//还清借书欠款 抛出关闭数据库连接产生的异常与更新数据库失败产生的异常
	public boolean payBookCost(User user) {
		Connection conn = connectPool.getConnection();
		user.setCostAmount(0.0);
		try {
			if (bookDao.updateUser(conn, user)) {
				connectPool.releaseConnection(conn);
				System.out.println("pay bookCost successed!");
				return true;
			} else {
				connectPool.releaseConnection(conn);
				System.out.println("pay bookCost failed!");
				return false;
			}
		} catch (SQLUpdateException e) {
			System.out.println("database operate " + e.getMessage() + " failed!");
			return false;
		}
	}

//添加用户
	public boolean addUser(User user) {
		Connection conn = connectPool.getConnection();
		try {
			if (bookDao.insertUser(conn, user)) {
				connectPool.releaseConnection(conn);
				System.out.println("add User successed!");
				return true;
			} else {
				connectPool.releaseConnection(conn);
				System.out.println("add User failed!");
				return false;
			}
		} catch (SQLUpdateException e) {
			System.out.println("database operate " + e.getMessage() + " failed!");
			return false;
		}
	}

//查询用户  抛出无此用户异常
	public User getUserByName(String userName) throws UserNotFoundException {
		Connection conn = connectPool.getConnection();
		User user;
		try {
			user = bookDao.getUserByName(conn, userName);
			connectPool.releaseConnection(conn);
			return user;
		} catch (SQLUpdateException e) {
			System.out.println("database operate " + e.getMessage() + " failed!");
			return null;
		}
	}

//查询用户借阅记录
//数据库中的timestamp与LocalDateTime转换的方法：LocalDateTime borrowTime=rs.getTimestamp("borrow_time").toLocalDateTime(); rs为ResultSet对象
	public List<BorrowRecord> getBorrowRecordByName(String userName) {
		try {
			Connection conn = connectPool.getTransactionConnection();
			return bookDao.getBorrowRecordByUserId(conn, bookDao.getUserIdByName(conn,userName));
		} catch (UserNotFoundException | SQLException userNotFoundException) {
			userNotFoundException.printStackTrace();
		} catch (SQLUpdateException e) {
			e.printStackTrace();
		}
		return null;
	}

//按书名查询图书
	public List<Book2> getBooksByBookName(String bookName) {
		Connection conn = connectPool.getConnection();
		List<Book2> list = bookDao.getBooksByBookName(conn, bookName);
		connectPool.releaseConnection(conn);
		return list;
	}

//借书
	/*
	 * 有欠费不能借书，需先调用paybookCost方法还清欠款 达到最大借书量不能借书 更新图书表中的borrower与is_instore属性 更新借阅表
	 * 更新用户表中的book_num属性+1 数据库操作中发生异常时，进行回滚rollback（）
	 * 
	 * @see service.BookService#borrowBook(entity.User, java.lang.String)
	 */
	public boolean borrowBook(User user, String bookId) {
		try {
			if (user.isOweFee())
				throw new UserOweFeeException();
			if (user.isMaxBorrowed())
				throw new UserMaxBorrowedException();
			Connection conn = connectPool.getTransactionConnection();
			if (bookDao.getBookByBookId(conn, bookId).isInStore()) {
				
				try {
					user.addBookNum();
					String userId = bookDao.getUserIdByName(conn, user.getUserName());
					bookDao.updateBookBorrow(conn, user.getUserName(), bookId);					
					bookDao.insertBorrowRecord(conn, userId, bookId);					
					bookDao.updateUser(conn, user);
					
					conn.commit();
				} catch (Exception e) {
					conn.rollback();
					user.minusBookNum();
					System.out.println("borrow book failed!" + "for reason:" + e.getMessage());
					return false;
				}
				return true;
			} else
				throw new BookNotInstoreException();
		} catch (UserOweFeeException e) {
			System.out.println("please pay the owe fee first!");
			return false;
		} catch (UserMaxBorrowedException e) {
			System.out.println("borrow book num is exceed the max!");
			return false;
		} catch (BookNotFoundException e) {
			System.out.println(e.getMessage());
			return false;
		} catch (BookNotInstoreException e) {
			return false;
		} catch (SQLException e) {
			System.out.println("database rollback failed!");
			return false;
		} catch (SQLUpdateException e) {
			System.out.println("database operate " + e.getMessage() + " failed!");
			return false;
		}
	}
	@Override
	public LinkedList<Book2> getAllBooks() {
		Connection conn = connectPool.getConnection();
		return bookDao.getAllBooks(conn);
	}
	@Override
	public LinkedList<User> getAllUsers() {
		Connection conn = connectPool.getConnection();
		return bookDao.getAllUsers(conn);
	}
	@Override
	public boolean isRegiste(String userName)
	{
		Connection conn = connectPool.getConnection();
		try {
		String userId ="";
		userId = bookDao.getUserIdByName(conn , userName);
		}catch(SQLUpdateException e) {
		} catch (UserNotFoundException e) {
		}
		return true;
	}
//还书
	/*
	 * 获取此书的借书时间，根据当前时间计算借阅天数。时间类型均为LocalDateTime
	 * 判断用户类型确定user的免费借阅天数（constantvalue.STUBORROWDAY），借阅时间超过此天数则计算超期罚金payment（天数x0.1） 
	 * 调用bookDao中的updateBorrowRecord方法更新borrow表格，return_time与payment
	 * 调用bookDao中的updateBookReturn方法更新图书表中此书的is_instore与borrower
	 * 调用bookDao中的updateUser方法更新user的book_num 需使用事务连接（参考borrowBook方法）
	 * 
	 */

	public boolean returnBook(User user, String bookId) {
	    try {
	        if (user.isOweFee())
	            throw new UserOweFeeException();
	        Connection conn = connectPool.getTransactionConnection();
	        if (!(bookDao.getBookByBookId(conn, bookId).isInStore())) {
	            
	            try {
	                int num = user.getBookNum();
					user.setBookNum(num-1);
	                String userId = bookDao.getUserIdByName(conn, user.getUserName());
	                bookDao.updateBookReturn(conn, bookId);					
	                bookDao.updateBorrowRecord(conn, bookId);					
	                bookDao.updateReturnUser(conn, user);
	                
	                conn.commit();
	            } catch (Exception e) {
	                conn.rollback();
	                user.minusBookNum();
	                System.out.println("return book failed!" + "for reason:" + e.getMessage());
	                return false;
	            }
	            return true;
	        } else {
	            throw new BookNotInstoreException();
	        }
	    } catch (UserOweFeeException e) {
	        System.out.println("please pay the owe fee first!");
	        return false;
	    }  catch (BookNotFoundException e) {
	        System.out.println(e.getMessage());
	        return false;
	    } catch (BookNotInstoreException e) {
	        return false;
	    } catch (SQLException e) {
	        System.out.println("database rollback failed!");
	        return false;
	    } catch (SQLUpdateException e) {
	        System.out.println("database operate " + e.getMessage() + " failed!");
	        return false;
	    }
			
	}

	@Override
	public boolean deleteBook(String bookId) {
		
		// TODO Auto-generated method stub
		return false;
	}
/*
 * 
 * 
 * 增加图书功能
 * 判断是否为老师，即管理员。
 * 再插入数据库当中
 */
	
	@Override
	public boolean addBook(Book2 book,User user) {
		try {
			if (!user.getType().contentEquals("tea"))
			{
				System.out.println(user.getType());
				throw new UserTypeErrorException();
			}
			Connection conn = connectPool.getTransactionConnection();
			
				try {
					bookDao.insertbook(conn, book);
					
					
					conn.commit();
				} catch (Exception e) {
					conn.rollback();
					user.minusBookNum();
					System.out.println("borrow book failed!" + "for reason:" + e.getMessage());
					return false;
				}
				return true;
			
		
		} catch (SQLException e) {
			System.out.println("database rollback failed!");
			return false;
		} catch (UserTypeErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("添加图书BookServiceIMp错误275行");
			
		}
		return false;
		
	}
/*
 * 充值功能！！！
 * 
 * 
 */
	@Override
	public boolean paycost(double cost_amount, User user) {
		// TODO Auto-generated method stub
		try {
			Connection conn = connectPool.getTransactionConnection();
			try {
				user.setCostAmount(cost_amount);
				bookDao.updateUser(conn, user);
				conn.commit();
			} catch(Exception e) {
				conn.rollback();
				System.out.println("paycost提交错误，BookServiceImp291行");
				return false;
			}
			return true;
		}catch(SQLException e) {
			System.out.println("paycost,数据库回滚错误");
			
		}
		return false;
	}

	@Override
	public boolean addBooks(List<Book2> list) {
		// TODO Auto-generated method stub
		return false;
	}
	

}
