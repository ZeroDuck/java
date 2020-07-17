package exception;

public class SQLUpdateException extends Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String message;
public SQLUpdateException(String message) {
	this.message=message;
}
public String getMessage() {
	return message;
}
}
