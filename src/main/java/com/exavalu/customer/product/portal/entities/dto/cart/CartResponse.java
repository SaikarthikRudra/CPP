package com.exavalu.customer.product.portal.entities.dto.cart;

import java.util.Map;


public class CartResponse {

	private String customerEmail;
	private String customerId;
	private Map<String, ItemsFromCartDetailsResponse> items; 
	private int totalAmount;
	
	public String getCustomerEmail() {
		return customerEmail;
	}
	public String getCustomerId() {
		return customerId;
	}
	public Map<String, ItemsFromCartDetailsResponse> getItems() {
		return items;
	}
	public int getTotalAmount() {
		return totalAmount;
	}
	public void setCustomerEmail(String customerEmail) {
		this.customerEmail = customerEmail;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	public void setItems(Map<String, ItemsFromCartDetailsResponse> items) {
		this.items = items;
	}
	public void setTotalAmount(int totalAmount) {
		this.totalAmount = totalAmount;
	}
	
}
