package com.exavalu.customer.product.portal.entities.dto.cart;

import lombok.Builder;

@Builder
public class ProductOutput {
	private String productTitle;
	private String brandName;
	private String seriesName;
	private String ram;
	private String rom;
	private String compositeId;
	private int price;
	private String warranty;
	private int quantity;
	private String location;

}
