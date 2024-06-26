package com.exavalu.customer.product.portal.entities.mongodb;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Products")
public class Product {
	
	@Id
	private String mongoId;
	
	private String brandName;
	private String location;
	private int price;
	private String productTitle;
	private int quantity;
	private String ram;
	private String rom;
	private String seriesName;
	private String warranty;
	
	
	
	public Product() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Product(String mongoId, String brandName, String location, int price, String productTitle, int quantity,
			String ram, String rom, String seriesName, String warranty) {
		super();
		this.mongoId = mongoId;
		this.brandName = brandName;
		this.location = location;
		this.price = price;
		this.productTitle = productTitle;
		this.quantity = quantity;
		this.ram = ram;
		this.rom = rom;
		this.seriesName = seriesName;
		this.warranty = warranty;
	}
	@Override
	public String toString() {
		return "Product [mongoId=" + mongoId + ", brandName=" + brandName + ", location=" + location + ", price="
				+ price + ", productTitle=" + productTitle + ", quantity=" + quantity + ", ram=" + ram + ", rom=" + rom
				+ ", seriesName=" + seriesName + ", warranty=" + warranty + "]";
	}
	public String getBrandName() {
		return brandName;
	}
	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
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
	public String getRam() {
		return ram;
	}
	public void setRam(String ram) {
		this.ram = ram;
	}
	public String getRom() {
		return rom;
	}
	public void setRom(String rom) {
		this.rom = rom;
	}
	public String getSeriesName() {
		return seriesName;
	}
	public void setSeriesName(String seriesName) {
		this.seriesName = seriesName;
	}
	public String getWarranty() {
		return warranty;
	}
	public void setWarranty(String warranty) {
		this.warranty = warranty;
	}
	




}
