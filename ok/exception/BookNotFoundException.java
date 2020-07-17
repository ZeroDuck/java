package exception;

public class BookNotFoundException extends Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String message;
	public BookNotFoundException(String message){
		this.message=message;
	}
	public String getMessage() {
		return message;
	}
}
