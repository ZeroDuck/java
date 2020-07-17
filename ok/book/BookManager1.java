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
 * @author �����������ͼ����û���������Ա�����в��� �û�ʹ��ǰ�����loginUser�������е�½����currentUsers���浱ǰ��½�û�
 *         ִ��borrowBook,returnBook,payBookCost�Ȳ���ʱ��currentUsers��ȡ�û�������Ϊ����������
 *         ͬʱ���û�����ʹ���߳�������֤���û�������ͬ���ԣ�����ͬһʱ��ͬһ�û�ֻ��ִ��һ�����
 */
public class BookManager1 {
	private static BookManager1 bookManager;
	private BookService bookService;
//��½���û����󱣴��ڴ�Map�С����������еĲ�������User������Ӵ�Map��ȡ
	private Map<String, User> currentUsers = new HashMap<String, User>();

//˽�еĹ��췽����ע��bookService�ӿڵ�ʵ�����ʵ��
	private BookManager1() {
		bookService = new BookServiceImp(new BookDao());
	}

//����ģʽ ��ȡΨһ��BookManagerʵ��
	public static BookManager1 getInstance() {
		synchronized (BookManager1.class) {
			if (bookManager == null) {
				bookManager = new BookManager1();
			}
			return bookManager;
		}
	}

//�û���½�����û����󱣴�currentUsers��
	public boolean loginUser(String userName) throws UserNotFoundException {
		if (!currentUsers.containsKey(userName)) {
			currentUsers.put(userName, bookService.getUserByName(userName));
			return true;
		} else {
			System.out.println("���ѵ�½�������ٴε�½");
			return false;
		}
	}

//��currentUsers�л�ȡ��½�û�������ִ�и��û���ز���
	public User getCurrentUser(String userName) throws UserNotLoginException {
		User user = currentUsers.get(userName);
		if (user == null) {
			throw new UserNotLoginException();
		} else
			return user;
	}

//�û��ǳ�
	public void logoutUser(String userName) {
		currentUsers.remove(userName);
	}
	public void displayAllUsers(){
		LinkedList<User> users = bookService.getAllUsers();
		if (users.size()==0)
			System.out.println("��û������");
		else {
			System.out.println("\nĿǰͼ�������ע���ˣ�");
			users.forEach(System.out::println);
		}

	}
//���û�ע��
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

//��ѯ�û���Ϣ
	public void queryUser(String userName) throws UserNotFoundException {
		System.out.println(bookService.getUserByName(userName));
	}
//�Ƿ�ע��
	public boolean isRegiste(String userName) {
		try {
			return (bookService.getUserByName(userName) != null);
		} catch (UserNotFoundException userNotFoundException) {
			return false;
		}
	}
//��ѯ�û����ļ�¼
	public void queryUserBorrowRecord(String userName) throws SQLUpdateException, UserNotFoundException {
		System.out.println(bookService.getBorrowRecordByName(userName));
	}
//��ѯ����ͼ��
	public void displayAllBooks() {
		LinkedList<Book2> books=bookService.getAllBooks();
		if (books.size()==0)
			System.out.println("��û������");
		else{
			System.out.println("\nĿǰͼ�����Ĳ����У�");
			books.forEach(System.out::println);
		}
	}

//ͨ��������ѯͼ��
	public void queryBook(String bookName) {
		bookService.getBooksByBookName(bookName).stream().forEach(System.out::println);
	}

//����
	public boolean borrowBook(String userName, String bookId) throws UserNotLoginException {
		User user = getCurrentUser(userName);
		synchronized (user) {
			return bookService.borrowBook(user, bookId);
		}
	}

//����
	public boolean returnBook(String userName, String bookId) throws UserNotLoginException {
		User user = getCurrentUser(userName);
		synchronized (user) {
			return bookService.returnBook(user, bookId);
		}
	}

//����Ƿ��
	public boolean payBookCost(String userName) throws UserNotLoginException {
		User user = getCurrentUser(userName);
		synchronized (user) {
			return bookService.payBookCost(user);
		}
	}
/*
 * ��ֵ���ܣ���������
 * 
 */
	public boolean cz(double jine, User user) {
		// TODO Auto-generated method stub
		double cost_amount=user.getCostAmount();
		cost_amount += jine;
		try {
			return bookService.paycost(cost_amount,user);
		}catch (Exception e) {
			System.out.println("BookManager1��cz���ܳ�������");
			return false;
		}
		
		
		
	}
}
