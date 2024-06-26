package com.exavalu.customer.product.portal.entities.dto.cart;

import java.util.List;


public class AddToCartRequest {

	private String customerId;
	private String emailId;
	private String location;
	private List<Product> products;
	
	
	public AddToCartRequest() {
		super();
		// TODO Auto-generated constructor stub
	}


	public AddToCartRequest(String customerId, String emailId, String location, List<Product> products) {
		super();
		this.customerId = customerId;
		this.emailId = emailId;
		this.location = location;
		this.products = products;
	}


	public String getCustomerId() {
		return customerId;
	}


	public String getEmailId() {
		return emailId;
	}


	public String getLocation() {
		return location;
	}


	public List<Product> getProducts() {
		return products;
	}


	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}


	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}


	public void setLocation(String location) {
		this.location = location;
	}


	public void setProducts(List<Product> products) {
		this.products = products;
	}


	public static class Product{
		private String productTitle;
		private int quantity;
		public String getProductTitle() {
			return productTitle;
		}
		public int getQuantity() {
			return quantity;
		}
		public void setProductTitle(String productTitle) {
			this.productTitle = productTitle;
		}
		public void setQuantity(int quantity) {
			this.quantity = quantity;
		}
		public Product() {
			super();
			// TODO Auto-generated constructor stub
		}
		public Product(String productTitle, int quantity) {
			super();
			this.productTitle = productTitle;
			this.quantity = quantity;
		}
		
	}
}
