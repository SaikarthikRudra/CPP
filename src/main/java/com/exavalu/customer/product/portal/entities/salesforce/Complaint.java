package com.exavalu.customer.product.portal.entities.salesforce;

import com.exavalu.customer.product.portal.entities.salesforce.deserializer.ComplaintSFDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = ComplaintSFDeserializer.class)
public class Complaint {

	private String name;
	private String action;
	private ComplaintCategory complaint_category;
	private String customerId;
	private String OrderId;
	public String productTitle;
	public int quantity;
	private String date_of_complaint;
	private String description;
	private String status;
	private String reason;



	public Complaint() {
		super();
		// TODO Auto generated constructor stub
	}

	public Complaint(String name, String action, ComplaintCategory complaint_category, String customerId,
			String orderId, String productTitle, int quantity, String date_of_complaint, String description,
			String status, String reason) {
		super();
		this.name = name;
		this.action = action;
		this.complaint_category = complaint_category;
		this.customerId = customerId;
		OrderId = orderId;
		this.productTitle = productTitle;
		this.quantity = quantity;
		this.date_of_complaint = date_of_complaint;
		this.description = description;
		this.status = status;
		this.reason = reason;
	}

	@Override
	public String toString() {
		return "Complaint [complaintId=" + name + ", action=" + action + ", complaint_category=" + complaint_category
				+  ", customerId=" + customerId + ", OrderId=" + OrderId
				+ ", productTitle=" + productTitle + ", quantity=" + quantity + ", date_of_complaint="
				+ date_of_complaint + ", description=" + description + ", status=" + status + ", reason=" + reason + "]";
	}

	public String getName() {
		return name;
	}

	public void setName(String Name) {
		this.name = Name;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public ComplaintCategory getComplaint_category() {
		return complaint_category;
	}

	 public void setComplaint_category(String complaint_category) {
         if (complaint_category == null) {
             this.complaint_category = null;
         } else {
             try {
                 this.complaint_category = ComplaintCategory.valueOf(complaint_category);
             } catch (IllegalArgumentException e) {
                 // Handle if the string does not match any enum constant
                 this.complaint_category = null;
                 // Optionally log or handle the exception
                 e.printStackTrace();
             }
         }
     }


	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getDate_of_complaint() {
		return date_of_complaint;
	}

	public void setDate_of_complaint(String date_of_complaint) {
		this.date_of_complaint = date_of_complaint;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getOrderId() {
		return OrderId;
	}

	public void setOrderId(String orderId) {
		OrderId = orderId;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}
	public enum ComplaintCategory {
		Wrong_Items,
		Delivery_Delay ,
		Damaged_Items 
    }

}


