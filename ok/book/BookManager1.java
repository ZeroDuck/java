package book;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import dao.BookDao;

import entity.Book2;
import entity.User;
import exception.SQLUpdateException;
import exception.UserNotFoundException;
import exception.UserNotLoginException;
import service.BookService;
import service.BookServiceImp;

/**
 * @author 此类包含面向图书馆用户及管理人员的所有操作 用户使用前需调用loginUser方法进行登陆，在currentUsers保存当前登陆用户
 *         执行borrowBook,returnBook,payBookCost等操作时从currentUsers获取用户对象作为方法参数，
 *         同时对用户对象使用线程锁，保证该用户操作的同步性，即在同一时刻同一用户只能执行一项操作
 */
public class BookManager1 {
	private static BookManager1 bookManager;
	private BookService bookService;
//登陆的用户对象保存在此Map中。本类中所有的操作所需User对象均从此Map获取
	private Map<String, User> currentUsers = new HashMap<String, User>();

//私有的构造方法，注入bookService接口的实现类的实例
	private BookManager1() {
		bookService = new BookServiceImp(new BookDao());
	}

//单例模式 获取唯一的BookManager实例
	public static BookManager1 getInstance() {
		synchronized (BookManager1.class) {
			if (bookManager == null) {
				bookManager = new BookManager1();
			}
			return bookManager;
		}
	}

//用户登陆。将用户对象保存currentUsers中
	public boolean loginUser(String userName) throws UserNotFoundException {
		if (!currentUsers.containsKey(userName)) {
			currentUsers.put(userName, bookService.getUserByName(userName));
			return true;
		} else {
			System.out.println("您已登陆，无需再次登陆");
			return false;
		}
	}

//从currentUsers中获取登陆用户，用于执行该用户相关操作
	public User getCurrentUser(String userName) throws UserNotLoginException {
		User user = currentUsers.get(userName);
		if (user == null) {
			throw new UserNotLoginException();
		} else
			return user;
	}

//用户登出
	public void logoutUser(String userName) {
		currentUsers.remove(userName);
	}
	public void displayAllUsers(){
		LinkedList<User> users = bookService.getAllUsers();
		if (users.size()==0)
			System.out.println("还没有人呢");
		else {
			System.out.println("\n目前图书馆里已注册了：");
			users.forEach(System.out::println);
		}

	}
//新用户注册
	public boolean registeUser(User user) {
		return bookService.addUser(user);
	}

	public boolean addBooks(List<Book2> list) {
		return bookService.addBooks(list);
	}

	public boolean addBook(Book2 book,User user) {
		return bookService.addBook(book,user);
	}

	public boolean deleteBook(String bookId) {
		return bookService.deleteBook(bookId);
	}

//查询用户信息
	public void queryUser(String userName) throws UserNotFoundException {
		System.out.println(bookService.getUserByName(userName));
	}
//是否注册
	public boolean isRegiste(String userName) {
		try {
			return (bookService.getUserByName(userName) != null);
		} catch (UserNotFoundException userNotFoundException) {
			return false;
		}
	}
//查询用户借阅记录
	public void queryUserBorrowRecord(String userName) throws SQLUpdateException, UserNotFoundException {
		System.out.println(bookService.getBorrowRecordByName(userName));
	}
//查询所有图书
	public void displayAllBooks() {
		LinkedList<Book2> books=bookService.getAllBooks();
		if (books.size()==0)
			System.out.println("还没有书呢");
		else{
			System.out.println("\n目前图书馆里的藏书有：");
			books.forEach(System.out::println);
		}
	}

//通过书名查询图书
	public void queryBook(String bookName) {
		bookService.getBooksByBookName(bookName).stream().forEach(System.out::println);
	}

//借书
	public boolean borrowBook(String userName, String bookId) throws UserNotLoginException {
		User user = getCurrentUser(userName);
		synchronized (user) {
			return bookService.borrowBook(user, bookId);
		}
	}

//还书
	public boolean returnBook(String userName, String bookId) throws UserNotLoginException {
		User user = getCurrentUser(userName);
		synchronized (user) {
			return bookService.returnBook(user, bookId);
		}
	}

//还清欠款
	public boolean payBookCost(String userName) throws UserNotLoginException {
		User user = getCurrentUser(userName);
		synchronized (user) {
			return bookService.payBookCost(user);
		}
	}
/*
 * 充值功能！！！！！
 * 
 */
	public boolean cz(double jine, User user) {
		// TODO Auto-generated method stub
		double cost_amount=user.getCostAmount();
		cost_amount += jine;
		try {
			return bookService.paycost(cost_amount,user);
		}catch (Exception e) {
			System.out.println("BookManager1的cz功能出现问题");
			return false;
		}
		
		
		
	}
}
