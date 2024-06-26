package com.exavalu.customer.product.portal.entities.salesforce;

import com.exavalu.customer.product.portal.entities.salesforce.deserializer.ProductSFDeserializer;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = ProductSFDeserializer.class)
public class ProductSF {

	private String brandName;
	private String compositeId;
	private String location;
	private int price;
	private String productTitle;
	private int quantity;
	private String ram;
	private String rom;
	private String seriesName;
	private String warranty;
	private String salesforceId;

	public String getSalesforceId() {
		return salesforceId;
	}

	public void setSalesforceId(String salesforceId) {
		this.salesforceId = salesforceId;
	}

	public ProductSF() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ProductSF(String brandName, String compositeId, String location, int price, String productTitle,
			int quantity, String ram, String rom, String seriesName, String warranty, String salesforceId) {

		super();
		this.brandName = brandName;
		this.compositeId = compositeId;
		this.location = location;
		this.price = price;
		this.productTitle = productTitle;
		this.quantity = quantity;
		this.ram = ram;
		this.rom = rom;
		this.seriesName = seriesName;
		this.warranty = warranty;
		this.salesforceId = salesforceId;
	}

	@Override
	public String toString() {
		return "Product [brandName=" + brandName + ", compositeId=" + compositeId + ", location=" + location
				+ ", price=" + price + ", productTitle=" + productTitle + ", quantity=" + quantity + ", ram=" + ram
				+ ", rom=" + rom + ", seriesName=" + seriesName + ", warranty=" + warranty + "]";
	}

	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	public String getCompositeId() {
		return compositeId;
	}

	public void setCompositeId(String compositeId) {
		this.compositeId = compositeId;
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

	public void setQuantity(int i) {
		this.quantity = i;
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
