package com.exavalu.customer.product.portal.entities.salesforce;

import java.util.Map;

import com.exavalu.customer.product.portal.entities.salesforce.deserializer.CartSFDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = CartSFDeserializer.class)
public class CartDetails {

	private String customerEmail;
	private String customerId;
	private Map<String, ItemsFromCartDetails> items; 
	private String nearestDbLocation;
	private String status;
	private int totalAmount;
	private String salesforceId;
	
	public String getSalesforceId() {
		return salesforceId;
	}
	public void setSalesforceId(String salesforceId) {
		this.salesforceId = salesforceId;
	}
	public String getCustomerEmail() {
		return customerEmail;
	}
	public String getCustomerId() {
		return customerId;
	}
	public Map<String, ItemsFromCartDetails> getItems() {
		return items;
	}
	public String getNearestDbLocation() {
		return nearestDbLocation;
	}
	public String getStatus() {
		return status;
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
	public void setItems(Map<String, ItemsFromCartDetails> items) {
		this.items = items;
	}
	public void setNearestDbLocation(String nearestDbLocation) {
		this.nearestDbLocation = nearestDbLocation;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public void setTotalAmount(int totalAmount) {
		this.totalAmount = totalAmount;
	}
	
	
}
