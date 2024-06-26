package com.exavalu.customer.product.portal.entities.salesforce;

import java.util.Map;

import com.exavalu.customer.product.portal.entities.salesforce.deserializer.OrderDetailsDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = OrderDetailsDeserializer.class)
public class OrderDetails {
	
	private String orderId;
	private String customerId;
	private String deliveryDate;
	private String feedback;
	private Map<String, ItemsFromCartDetails> items; 
	private String orderDate;
	private String paymentMode;
	private String status;
	private String totalPrice;
	private String salesforceId;
	
	public OrderDetails() {
		super();
		// TODO Auto-generated constructor stub
	}
	public OrderDetails(String orderId, String customerId, String deliveryDate, String feedback,
			Map<String, ItemsFromCartDetails> items, String orderDate, String paymentMode, String status,
			String totalPrice,String salesforceId) {
		super();
		this.orderId = orderId;
		this.customerId = customerId;
		this.deliveryDate = deliveryDate;
		this.feedback = feedback;
		this.items = items;
		this.orderDate = orderDate;
		this.paymentMode = paymentMode;
		this.status = status;
		this.totalPrice = totalPrice;
		this.salesforceId = salesforceId;
	}
	
	
	public String getCustomerId() {
		return customerId;
	}
	public String getDeliveryDate() {
		return deliveryDate;
	}
	public String getFeedback() {
		return feedback;
	}
	public Map<String, ItemsFromCartDetails> getItems() {
		return items;
	}
	public String getOrderDate() {
		return orderDate;
	}
	public String getPaymentMode() {
		return paymentMode;
	}
	public String getStatus() {
		return status;
	}
	public String getTotalPrice() {
		return totalPrice;
	}
	
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	public void setDeliveryDate(String deliveryDate) {
		this.deliveryDate = deliveryDate;
	}
	public void setFeedback(String feedback) {
		this.feedback = feedback;
	}
	public void setItems(Map<String, ItemsFromCartDetails> items) {
		this.items = items;
	}
	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}
	public void setPaymentMode(String paymentMode) {
		this.paymentMode = paymentMode;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public void setTotalPrice(String totalPrice) {
		this.totalPrice = totalPrice;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getSalesforceId() {
		return salesforceId;
	}
	public void setSalesforceId(String salesforceId) {
		this.salesforceId = salesforceId;
	}
	
	
}
