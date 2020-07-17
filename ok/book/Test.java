package book;
import java.util.Scanner;

import entity.Book2;
import entity.User;
import exception.SQLUpdateException;
import exception.UserNotFoundException;
import exception.UserNotLoginException;
import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class Test {
	public static void main(String[] args) {
		BookManager1 bm = BookManager1.getInstance();
		Scanner scanner = new Scanner(System.in);
		String userName = "";
		System.out.println("\n欢迎进入图书馆！\n");
		boolean flag = true;
		
		System.out.println("1、登陆 2、注册");
		int choose = scanner.nextInt();
		userName = scanUserName();
		switch (choose){
			case 1:
				if(loginChoose(bm,userName))
				{
				
					flag = true;
				}
				else {
					flag=false;
				}
				
				break;
			case 2:
				if(registeChoose(bm,userName))
				{
					System.out.println("注册成功，正在为你自动登录");
					flag = true;
				}
				else {
					flag =false;
				}
				break;
		}
			
		if(!flag) {
			System.out.println("正在关闭程序，下次启动请先注册");
			return;
		}
		//获取user,先置空再赋值
		User user=null;
		try {
			user = bm.getCurrentUser(userName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			try {
				loginChoose(bm,userName);
				//e.printStackTrace();
				System.out.println("已经为你自动登录了");
				}catch (Exception e1) {
					System.out.println("无法自动登录，请你重新手动登录");
				}
		}
		flag =true;
		while (flag){
			displayMenu();
			System.out.println("\n请输入：\n");
			int choice = scanner.nextInt();
			switch (choice){
				case 1:
					borrowBookChoice(bm,userName);
					break;
				case 2:
					returnBookChoice(bm,userName);
					break;
				case 3:
					queryUserBorrowRecordChoice(bm,userName);
					break;
				case 4:
					addbookChoose(bm,user);
					break;
				case 5:
					Paychoice(bm,user);
					break;
				case 6:
					displayAllBooksChoice(bm);
					break;
				case 88:
					bm.logoutUser(userName);
					flag = false;
					break;
			}
//			try {
//				clear();
//			}catch(AWTException e){
//			}
			
		}
	}
/*
 * 功能块
 */
	public static void displayMenu() {
		System.out.printf("===========================================\n");
		System.out.printf("|             ++欢迎会员登录图书馆++            |\n");
		System.out.printf("|         *******在  线  服  务  选   择*******      |\n");
		System.out.printf("|      1           自助借书请选择                   1		   |\n");
		System.out.printf("|      2           自助还书请选择                   2		   |\n");
		System.out.printf("|      3       查询个人借阅记录请选择        3		   |\n");
		System.out.printf("|      4       增加图书功能（管理员）        4		   |\n");
		System.out.printf("|      5            在线余额请选择                  5		   |\n");
		System.out.printf("|      6       查询所图书馆库存请选择        6		   |\n");
		System.out.printf("|     88          退出图书馆请选择                88		   |\n");
		System.out.printf("=========================================");

	}
	public static void displayAllBooksChoice(BookManager1 bm)
	{
		System.out.println("\n现在图书馆状态如下：");
		bm.displayAllBooks();
	}
	public static void returnBookChoice(BookManager1 bm ,String userName)
	{
		boolean flag = true;
		Scanner scanner = new Scanner(System.in);
		while (flag) {
			System.out.println("\n请输入要还的图书id(Q退出)：");
			String returnBookId = scanner.next();
			if (returnBookId.equals("Q")||returnBookId.equals("q")){
				flag=false;
				System.out.println("Bye~");
			}else{
				try {
					if (bm.returnBook(userName,returnBookId)){
						System.out.println(userName + "归还了" + returnBookId);
						bm.payBookCost(userName);
					}
				} catch (UserNotLoginException e) {
					e.printStackTrace();
				}
			}

		}
	}
	public static void queryUserBorrowRecordChoice(BookManager1 bm ,String userName)
	{
		boolean flag = true;
		while (flag) {
			System.out.println("\n您的借阅记录如下：");
			try {
				bm.queryUserBorrowRecord(userName);
				flag = false;
			} catch (SQLUpdateException e) {
				e.printStackTrace();
				flag = false;
			} catch (UserNotFoundException userNotFoundException) {
				userNotFoundException.printStackTrace();
				flag = false;
			}
		}
	}
	public static void borrowBookChoice(BookManager1 bm ,String userName)
	{
		boolean flag = true;
		Scanner scanner = new Scanner(System.in);
		while (flag) {
			System.out.println("\n请输入要借的图书id(Q退出)：");
			String borrowBookId = scanner.next();
			if (borrowBookId.equals("Q")||borrowBookId.equals("q")){
				flag=false;
				System.out.println("Bye~");
			}else
				try {
					if (bm.borrowBook(userName,borrowBookId))
						System.out.println(userName + "借阅" + borrowBookId);
				} catch (UserNotLoginException e) {
				}

		}
	}
	public static String scanUserName()
	{
		System.out.println("请输入姓名：");
		Scanner scanner = new Scanner(System.in);
		String userName = "";
		userName =scanner.next();
		return userName;
	}
	public static boolean loginChoose(BookManager1 bm , String userName){
		try {
			bm.loginUser(userName);
			return true;
		} catch (UserNotFoundException userNotFoundException) {
			System.out.println("请先注册\n");
			return false;
		}
	}
	public static boolean registeChoose(BookManager1 bm, String userName) {
		Scanner scanner = new Scanner(System.in);
		if (bm.isRegiste(userName)){
			System.out.println( userName +" 已经注册了");
			return true;
		}
		System.out.println("老师还是学生？(tea?stu)");
		String userType = scanner.next();
		while (!(userType.equals("stu")||userType.equals("tea"))){
			System.out.println("输入错误，(tea?stu)");
			userType = scanner.next();
			System.out.println( userType +" " + userName +" 注册中");
		}
		try {
			bm.registeUser(new User(userName,userType));
			return true;
		}catch(Exception e){
			return false;
		}
	}
	
	public static void addbookChoose(BookManager1 bm,User user) {
		boolean xunhuan = true;
		Scanner scanner = new Scanner(System.in);
		String anjian;
		while(xunhuan) {
			System.out.println("输入书名：");
			String book_name = scanner.next();
			System.out.println("输入作者：");
			String author = scanner.next();
			System.out.println("输入出版社：");
			String publish_house = scanner.next();
			try {
				bm.addBook(new Book2(book_name,author,publish_house),user);
				System.out.println("添加图书成功！\n");
				System.out.println("继续添加请按Y，否则按其他键");
				anjian = scanner.next();
				if(anjian.contentEquals("y")||anjian.contentEquals("Y"))
				{
					xunhuan =true;
				}
				else
				{
				xunhuan=false;
				}
				
				}catch (Exception e) {
					e.printStackTrace();
					System.out.println("添加不成功！");
					xunhuan=false;
				}
			}
		
		
	}
	public static void Paychoice(BookManager1 bm,User user)
	{
		double jine=0;
		String anjian;
		boolean xunhuan=true;
		Scanner scanner = new Scanner(System.in);
		while(xunhuan)
		{
			System.out.print("请输入你要充值的金额：");
			try {
				jine = scanner.nextDouble();
				try {
					bm.cz(jine,user);
					System.out.println("充值成功！！！\n您充值了￥"+jine+"元");
					System.out.println("您的余额为￥"+user.getCostAmount());
					System.out.println("\n 如果您希望继续充值请按Y,按其他键返回上一级");
					anjian=scanner.next();
					if(anjian.contentEquals("y")||anjian.contentEquals("Y"))
					{
						xunhuan =true;
					}
					else
					{
						xunhuan=false;
					}
				}catch(Exception e) {
					e.printStackTrace();
					System.out.println("充值不成功");
					xunhuan= false;
					
				}
				
			}catch(Exception e) {
				//e.printStackTrace();
				xunhuan=false;
				System.out.println("充值失败");
				System.out.println("请输出数字，否之无法充值！");
			}
		}
	}
}
	
