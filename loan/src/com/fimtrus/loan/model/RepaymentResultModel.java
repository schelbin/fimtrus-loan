package com.fimtrus.loan.model;

public class RepaymentResultModel {
	
	private double repayments = 0; //월상환금
	private double loans = 0; //납입원금
	private double interest = 0; //납입이자
	private double remainingAmount = 0; //갚은금액
	private double totalLoans = 0; //남은 원금
	
	
	public double getRepayments() {
		return repayments;
	}
	public void setRepayments(double repayments) {
		this.repayments = repayments;
	}
	public double getLoans() {
		return loans;
	}
	public void setLoans(double loans) {
		this.loans = loans;
	}
	public double getInterest() {
		return interest;
	}
	public void setInterest(double interest) {
		this.interest = interest;
	}
	public double getRemainingAmount() {
		return remainingAmount;
	}
	public void setRemainingAmount(double remainingAmount) {
		this.remainingAmount = remainingAmount;
	}
	public double getTotalLoans() {
		return totalLoans;
	}
	public void setTotalLoans(double totalLoans) {
		this.totalLoans = totalLoans;
	}
}
