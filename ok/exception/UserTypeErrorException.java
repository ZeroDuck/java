package exception;

public class UserTypeErrorException extends Exception{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UserTypeErrorException() {
		System.out.println("不是老师不可以管理图书馆！！！");
	}

}
