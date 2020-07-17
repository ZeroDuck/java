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
		System.out.println("\n��ӭ����ͼ��ݣ�\n");
		boolean flag = true;
		
		System.out.println("1����½ 2��ע��");
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
					System.out.println("ע��ɹ�������Ϊ���Զ���¼");
					flag = true;
				}
				else {
					flag =false;
				}
				break;
		}
			
		if(!flag) {
			System.out.println("���ڹرճ����´���������ע��");
			return;
		}
		//��ȡuser,���ÿ��ٸ�ֵ
		User user=null;
		try {
			user = bm.getCurrentUser(userName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			try {
				loginChoose(bm,userName);
				//e.printStackTrace();
				System.out.println("�Ѿ�Ϊ���Զ���¼��");
				}catch (Exception e1) {
					System.out.println("�޷��Զ���¼�����������ֶ���¼");
				}
		}
		flag =true;
		while (flag){
			displayMenu();
			System.out.println("\n�����룺\n");
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
 * ���ܿ�
 */
	public static void displayMenu() {
		System.out.printf("===========================================\n");
		System.out.printf("|             ++��ӭ��Ա��¼ͼ���++            |\n");
		System.out.printf("|         *******��  ��  ��  ��  ѡ   ��*******      |\n");
		System.out.printf("|      1           ����������ѡ��                   1		   |\n");
		System.out.printf("|      2           ����������ѡ��                   2		   |\n");
		System.out.printf("|      3       ��ѯ���˽��ļ�¼��ѡ��        3		   |\n");
		System.out.printf("|      4       ����ͼ�鹦�ܣ�����Ա��        4		   |\n");
		System.out.printf("|      5            ���������ѡ��                  5		   |\n");
		System.out.printf("|      6       ��ѯ��ͼ��ݿ����ѡ��        6		   |\n");
		System.out.printf("|     88          �˳�ͼ�����ѡ��                88		   |\n");
		System.out.printf("=========================================");

	}
	public static void displayAllBooksChoice(BookManager1 bm)
	{
		System.out.println("\n����ͼ���״̬���£�");
		bm.displayAllBooks();
	}
	public static void returnBookChoice(BookManager1 bm ,String userName)
	{
		boolean flag = true;
		Scanner scanner = new Scanner(System.in);
		while (flag) {
			System.out.println("\n������Ҫ����ͼ��id(Q�˳�)��");
			String returnBookId = scanner.next();
			if (returnBookId.equals("Q")||returnBookId.equals("q")){
				flag=false;
				System.out.println("Bye~");
			}else{
				try {
					if (bm.returnBook(userName,returnBookId)){
						System.out.println(userName + "�黹��" + returnBookId);
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
			System.out.println("\n���Ľ��ļ�¼���£�");
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
			System.out.println("\n������Ҫ���ͼ��id(Q�˳�)��");
			String borrowBookId = scanner.next();
			if (borrowBookId.equals("Q")||borrowBookId.equals("q")){
				flag=false;
				System.out.println("Bye~");
			}else
				try {
					if (bm.borrowBook(userName,borrowBookId))
						System.out.println(userName + "����" + borrowBookId);
				} catch (UserNotLoginException e) {
				}

		}
	}
	public static String scanUserName()
	{
		System.out.println("������������");
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
			System.out.println("����ע��\n");
			return false;
		}
	}
	public static boolean registeChoose(BookManager1 bm, String userName) {
		Scanner scanner = new Scanner(System.in);
		if (bm.isRegiste(userName)){
			System.out.println( userName +" �Ѿ�ע����");
			return true;
		}
		System.out.println("��ʦ����ѧ����(tea?stu)");
		String userType = scanner.next();
		while (!(userType.equals("stu")||userType.equals("tea"))){
			System.out.println("�������(tea?stu)");
			userType = scanner.next();
			System.out.println( userType +" " + userName +" ע����");
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
			System.out.println("����������");
			String book_name = scanner.next();
			System.out.println("�������ߣ�");
			String author = scanner.next();
			System.out.println("��������磺");
			String publish_house = scanner.next();
			try {
				bm.addBook(new Book2(book_name,author,publish_house),user);
				System.out.println("���ͼ��ɹ���\n");
				System.out.println("��������밴Y������������");
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
					System.out.println("��Ӳ��ɹ���");
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
			System.out.print("��������Ҫ��ֵ�Ľ�");
			try {
				jine = scanner.nextDouble();
				try {
					bm.cz(jine,user);
					System.out.println("��ֵ�ɹ�������\n����ֵ�ˣ�"+jine+"Ԫ");
					System.out.println("�������Ϊ��"+user.getCostAmount());
					System.out.println("\n �����ϣ��������ֵ�밴Y,��������������һ��");
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
					System.out.println("��ֵ���ɹ�");
					xunhuan= false;
					
				}
				
			}catch(Exception e) {
				//e.printStackTrace();
				xunhuan=false;
				System.out.println("��ֵʧ��");
				System.out.println("��������֣���֮�޷���ֵ��");
			}
		}
	}
}
	
