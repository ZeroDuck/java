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

//�������Ƿ�� �׳��ر����ݿ����Ӳ������쳣��������ݿ�ʧ�ܲ������쳣
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

//����û�
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

//��ѯ�û�  �׳��޴��û��쳣
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

//��ѯ�û����ļ�¼
//���ݿ��е�timestamp��LocalDateTimeת���ķ�����LocalDateTime borrowTime=rs.getTimestamp("borrow_time").toLocalDateTime(); rsΪResultSet����
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

//��������ѯͼ��
	public List<Book2> getBooksByBookName(String bookName) {
		Connection conn = connectPool.getConnection();
		List<Book2> list = bookDao.getBooksByBookName(conn, bookName);
		connectPool.releaseConnection(conn);
		return list;
	}

//����
	/*
	 * ��Ƿ�Ѳ��ܽ��飬���ȵ���paybookCost��������Ƿ�� �ﵽ�����������ܽ��� ����ͼ����е�borrower��is_instore���� ���½��ı�
	 * �����û����е�book_num����+1 ���ݿ�����з����쳣ʱ�����лع�rollback����
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
//����
	/*
	 * ��ȡ����Ľ���ʱ�䣬���ݵ�ǰʱ��������������ʱ�����;�ΪLocalDateTime
	 * �ж��û�����ȷ��user����ѽ���������constantvalue.STUBORROWDAY��������ʱ�䳬������������㳬�ڷ���payment������x0.1�� 
	 * ����bookDao�е�updateBorrowRecord��������borrow���return_time��payment
	 * ����bookDao�е�updateBookReturn��������ͼ����д����is_instore��borrower
	 * ����bookDao�е�updateUser��������user��book_num ��ʹ���������ӣ��ο�borrowBook������
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
 * ����ͼ�鹦��
 * �ж��Ƿ�Ϊ��ʦ��������Ա��
 * �ٲ������ݿ⵱��
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
			System.out.println("���ͼ��BookServiceIMp����275��");
			
		}
		return false;
		
	}
/*
 * ��ֵ���ܣ�����
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
				System.out.println("paycost�ύ����BookServiceImp291��");
				return false;
			}
			return true;
		}catch(SQLException e) {
			System.out.println("paycost,���ݿ�ع�����");
			
		}
		return false;
	}

	@Override
	public boolean addBooks(List<Book2> list) {
		// TODO Auto-generated method stub
		return false;
	}
	

}
