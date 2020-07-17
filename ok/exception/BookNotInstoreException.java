package exception;

public class BookNotInstoreException extends Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2L;

	public BookNotInstoreException(){
		System.out.println("This book is not Instore.");
	}
}
