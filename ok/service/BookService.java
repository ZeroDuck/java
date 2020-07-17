package service;

import java.util.LinkedList;
import java.util.List;

import entity.Book2;
import entity.BorrowRecord;
import entity.User;
import exception.UserNotFoundException;

public interface BookService {

	boolean returnBook(User user, String bookId);

	boolean borrowBook(User user, String bookId);

	List<Book2> getBooksByBookName(String bookName);
	
	boolean deleteBook(String bookId);

	boolean addBook(Book2 book,User user);

	boolean addBooks(List<Book2> list);

	List<BorrowRecord> getBorrowRecordByName(String userName);
	
	LinkedList<Book2> getAllBooks();
	
	LinkedList<User> getAllUsers();
	
	boolean isRegiste(String userName);

	User getUserByName(String userName)throws UserNotFoundException;
	
	boolean addUser(User user);
	
	boolean payBookCost(User user);

	boolean paycost(double cost_amount, User user);
}
