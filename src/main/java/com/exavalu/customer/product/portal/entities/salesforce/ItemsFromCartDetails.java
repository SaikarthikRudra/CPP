package com.exavalu.customer.product.portal.entities.salesforce;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ItemsFromCartDetails {

	private String productTitle;
	private String warranty;
	private int quantity;
	private int price;
	private int deliveryCharge;
	private int totalPrice;
	private String shippingWareHouseLocation;
	private String addToCartDate;
	private String expectedDelivery;
	private String nearestDbLocation;
	
	public ItemsFromCartDetails(String productTitle, String warranty, int quantity, int price, int deliveryCharge,
			int totalPrice, String shippingWareHouseLocation, String addToCartDate, String expectedDelivery,
			String nearestDbLocation) {
		super();
		this.productTitle = productTitle;
		this.warranty = warranty;
		this.quantity = quantity;
		this.price = price;
		this.deliveryCharge = deliveryCharge;
		this.totalPrice = totalPrice;
		this.shippingWareHouseLocation = shippingWareHouseLocation;
		this.addToCartDate = addToCartDate;
		this.expectedDelivery = expectedDelivery;
		this.nearestDbLocation = nearestDbLocation;
	}
	
	
	public ItemsFromCartDetails() {
		super();
		// TODO Auto-generated constructor stub
	}


	public String getProductTitle() {
		return productTitle;
	}
	public void setProductTitle(String productTitle) {
		this.productTitle = productTitle;
	}
	public String getWarranty() {
		return warranty;
	}
	public void setWarranty(String warranty) {
		this.warranty = warranty;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
	public int getDeliveryCharge() {
		return deliveryCharge;
	}
	public void setDeliveryCharge(int deliveryCharge) {
		this.deliveryCharge = deliveryCharge;
	}
	public int getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(int totalPrice) {
		this.totalPrice = totalPrice;
	}
	public String getShippingWareHouseLocation() {
		return shippingWareHouseLocation;
	}
	public void setShippingWareHouseLocation(String shippingWareHouseLocation) {
		this.shippingWareHouseLocation = shippingWareHouseLocation;
	}
	public String getAddToCartDate() {
		return addToCartDate;
	}
	public void setAddToCartDate(String addToCartDate) {
		this.addToCartDate = addToCartDate;
	}
	public String getExpectedDelivery() {
		return expectedDelivery;
	}
	public void setExpectedDelivery(String expectedDelivery) {
		this.expectedDelivery = expectedDelivery;
	}
	public String getNearestDbLocation() {
		return nearestDbLocation;
	}
	public void setNearestDbLocation(String nearestDbLocation) {
		this.nearestDbLocation = nearestDbLocation;
	}
	
	
	
}
