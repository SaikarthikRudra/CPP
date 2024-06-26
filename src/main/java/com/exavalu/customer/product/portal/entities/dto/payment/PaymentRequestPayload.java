package com.exavalu.customer.product.portal.entities.dto.payment;

import com.exavalu.customer.product.portal.entities.mongodb.Customer;

public class PaymentRequestPayload {
	
	private Customer customerDetails;
	private Payment paymentDetails;
	
	
	public PaymentRequestPayload() {
		super();
		
	}


	public PaymentRequestPayload(Customer customerDetails, Payment paymentDetails) {
		super();
		this.customerDetails = customerDetails;
		this.paymentDetails = paymentDetails;
	}


	public static class Payment{
		
		private String paymentMode;
		private int cardId;
		private String bankName;
		private String cardNumber;
		private String cvv;
		private int totalAmount;
		private String upiId;
		private boolean useCashback;
		
		public String getPaymentMode() {
			return paymentMode;
		}
		public int getCardId() {
			return cardId;
		}
		public String getBankName() {
			return bankName;
		}
		public String getCardNumber() {
			return cardNumber;
		}
		public String getCvv() {
			return cvv;
		}
		public int getTotalAmount() {
			return totalAmount;
		}
		public String getUpiId() {
			return upiId;
		}
		public void setPaymentMode(String paymentMode) {
			this.paymentMode = paymentMode;
		}
		public void setCardId(int cardId) {
			this.cardId = cardId;
		}
		public void setBankName(String bankName) {
			this.bankName = bankName;
		}
		public void setCardNumber(String cardNumber) {
			this.cardNumber = cardNumber;
		}
		public void setCvv(String cvv) {
			this.cvv = cvv;
		}
		public void setTotalAmount(int totalAmount) {
			this.totalAmount = totalAmount;
		}
		public void setUpiId(String upiId) {
			this.upiId = upiId;
		}
		
		public Payment(String paymentMode, int cardId, String bankName, String cardNumber, String cvv, int totalAmount,
				String upiId, boolean useCashback) {
			super();
			this.paymentMode = paymentMode;
			this.cardId = cardId;
			this.bankName = bankName;
			this.cardNumber = cardNumber;
			this.cvv = cvv;
			this.totalAmount = totalAmount;
			this.upiId = upiId;
			this.useCashback = useCashback;
		}
		public Payment() {
			super();
			// TODO Auto-generated constructor stub
		}
		public boolean isUseCashback() {
			return useCashback;
		}
		public void setUseCashback(boolean useCashback) {
			this.useCashback = useCashback;
		}
		
	}


	public Customer getCustomerDetails() {
		return customerDetails;
	}


	public Payment getPaymentDetails() {
		return paymentDetails;
	}


	public void setCustomerDetails(Customer customerDetails) {
		this.customerDetails = customerDetails;
	}


	public void setPaymentDetails(Payment paymentDetails) {
		this.paymentDetails = paymentDetails;
	}

}
