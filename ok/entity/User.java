package entity;

import constant.ConstantValue;

public class User {
	private String userName;
//�û����� ѧ��Ϊstu����ʦΪtea
	private String type;
//��ǰ����������δ����
	private int bookNum;

	@Override
	public String toString() {
		return "User [userName=" + userName + ", type=" + type + ", bookNum=" + bookNum + ", costAmount=" + costAmount
				+ "]";
	}

//Ƿ���ܶ�
	private double costAmount;

	public User(String userName, String type, int bookNum, double costAmount) {
		super();
		this.userName = userName;
		this.type = type;
		this.bookNum = bookNum;
		this.costAmount = costAmount;
	}
	public User(String userName, String type){
		super();
		this.userName=userName;
		this.type=type;
		this.bookNum = 0;
		this.costAmount = 0;

	}

//�Ƿ�Ƿ��
	public boolean isOweFee() {
		if (costAmount <= 0) {
			return true;
		} else
			return false;
	}

//�Ƿ�ﵽ����������
	public boolean isMaxBorrowed() {
		if (type.equals("stu")) {
			return (bookNum < ConstantValue.STUBOOKNUM) ? false : true;
		} else
			return (bookNum < ConstantValue.TEABOOKNUM) ? false : true;
	}

//����ɹ�Num+1
	public void addBookNum() {
		bookNum++;
	}

//����num-1
	public void minusBookNum() {
		bookNum--;
	}

	public double getCostAmount() {
		return costAmount;
	}

	public void setCostAmount(double costAmount) {
		this.costAmount = costAmount;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getBookNum() {
		return bookNum;
	}

	public void setBookNum(int bookNum) {
		this.bookNum = bookNum;
	}
}
