package com.exavalu.customer.product.portal.entities.salesforce;

import com.exavalu.customer.product.portal.entities.salesforce.deserializer.CashbackCPPDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = CashbackCPPDeserializer.class )
public class CashbackCPP {

	private String customerId;
	private String customerEmail;
	private int cashbackWallet;
	private String salesforceId;
	private int cashbackWalletPending;
	
	
	
	public CashbackCPP(String customerId, String customerEmail, int cashbackWallet, String salesforceId,
			int cashbackWalletPending) {
		super();
		this.customerId = customerId;
		this.customerEmail = customerEmail;
		this.cashbackWallet = cashbackWallet;
		this.salesforceId = salesforceId;
		this.cashbackWalletPending = cashbackWalletPending;
	}
	public String getCustomerId() {
		return customerId;
	}
	public String getCustomerEmail() {
		return customerEmail;
	}
	public int getCashbackWallet() {
		return cashbackWallet;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	public void setCustomerEmail(String customerEmail) {
		this.customerEmail = customerEmail;
	}
	public void setCashbackWallet(int cashbackWallet) {
		this.cashbackWallet = cashbackWallet;
	}
	public CashbackCPP() {
		super();
		
	}
	public String getSalesforceId() {
		return salesforceId;
	}
	public void setSalesforceId(String salesforceId) {
		this.salesforceId = salesforceId;
	}
	public int getCashbackWalletPending() {
		return cashbackWalletPending;
	}
	public void setCashbackWalletPending(int cashbackWalletPending) {
		this.cashbackWalletPending = cashbackWalletPending;
	}
	

}
