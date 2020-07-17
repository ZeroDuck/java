package entity;

public class Book2 {
	private int bookId;
	private String bookName;
	private String author;
	private String publishHouse;
	private boolean inStore;
	private String borrower;	
	
	
	public Book2() {
	}

	public Book2(int bookId,String bookName, String author, String publishHouse) {
		super();
		this.bookId = bookId;
		this.bookName = bookName;
		this.author = author;
		this.publishHouse = publishHouse;
		inStore = true;
		borrower = null;		
	}
	public Book2(String bookName, String author, String publishHouse) {
		super();
		//this.bookId = (Integer) null;
		this.bookName = bookName;
		this.author = author;
		this.publishHouse = publishHouse;
		inStore = true;
		borrower = null;		
	}
	
	public Book2(int bookId,String bookName, String author, String publishHouse,boolean inStore,String borrower) {
		super();
		this.bookId = bookId;
		this.bookName = bookName;
		this.author = author;
		this.publishHouse = publishHouse;
		this.inStore = inStore;
		this.borrower = borrower;		
	}
	

	public String getBookName() {
		return bookName;
	}

	public void setBookName(String bookName) {
		this.bookName = bookName;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getPublishHouse() {
		return publishHouse;
	}

	public void setPublishHouse(String publishHouse) {
		this.publishHouse = publishHouse;
	}

	public boolean isInStore() {
		return inStore;
	}

	public void setInStore(boolean inStore) {
		this.inStore = inStore;
	}

	public String getBorrower() {
		return borrower;
	}

	public void setBorrower(String borrower) {
		this.borrower = borrower;
	}

	public void setBookId(int bookId) {
		this.bookId = bookId;
	}

	public int getBookId() {
	return bookId;
	}
	
	@Override
	public String toString() {
		return "Book2 [bookId=" + bookId + ", bookName=" + bookName + ", author=" + author + ", publishHouse="
				+ publishHouse + ", inStore=" + inStore + ", borrower=" + borrower + "]";
	}
	
	public void updateBookBorrowed(String userName) {
		inStore=false;
		borrower=userName;
	}
	public void updateBookReturned() {
		inStore=true;
		borrower=null;
	}
	@Override
	public boolean equals(Object book1) {
		if(book1 instanceof Book2) {
			Book2 book=(Book2)book1;
		    if(this.bookName.equals(book.getBookName())&&this.author.equals(book.getAuthor())&&this.publishHouse.equals(book.getPublishHouse())) {			
			return true;}else return false;
		}else return false;
	} 
	
	
		
}
