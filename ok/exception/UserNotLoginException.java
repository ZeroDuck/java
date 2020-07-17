package exception;

public class UserNotLoginException extends Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UserNotLoginException(){
		System.out.println("Please Login first!");
	}
}
