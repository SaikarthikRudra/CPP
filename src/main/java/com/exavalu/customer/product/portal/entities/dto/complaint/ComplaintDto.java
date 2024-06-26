package com.exavalu.customer.product.portal.entities.dto.complaint;

import com.exavalu.customer.product.portal.entities.salesforce.Complaint.ComplaintCategory;

public class ComplaintDto {

	private String complaintId;
	private String date_of_complaint;
	private String OrderId;
	private String customerId;
	public String productTitle;
	public int quantity;
	private ComplaintCategory complaint_category;
	private String description;
	private String status;
	private String action;
	private String reason;
	
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getComplaintId() {
		return complaintId;
	}
	public void setComplaintId(String complaintId) {
		this.complaintId = complaintId;
	}
	public String getDate_of_complaint() {
		return date_of_complaint;
	}
	public void setDate_of_complaint(String date_of_complaint) {
		this.date_of_complaint = date_of_complaint;
	}
	public String getOrderId() {
		return OrderId;
	}
	public void setOrderId(String orderId) {
		OrderId = orderId;
	}
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	public String getProductTitle() {
		return productTitle;
	}
	public void setProductTitle(String productTitle) {
		this.productTitle = productTitle;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public ComplaintCategory getComplaint_category() {
		return complaint_category;
	}
	public void setComplaint_category(ComplaintCategory complaint_category) {
		this.complaint_category = complaint_category;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
}
