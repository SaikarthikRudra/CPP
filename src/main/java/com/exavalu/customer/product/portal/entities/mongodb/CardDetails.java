package com.exavalu.customer.product.portal.entities.mongodb;

public class CardDetails {

	private String bankName;
	private String cardNumber;

	public CardDetails() {
		// Default constructor
	}

	public CardDetails(String bankName, String cardNumber) {
		super();
		this.bankName = bankName;
		this.cardNumber = cardNumber;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}
}

