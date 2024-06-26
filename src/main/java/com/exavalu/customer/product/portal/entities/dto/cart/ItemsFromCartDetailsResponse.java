package com.exavalu.customer.product.portal.entities.dto.cart;

import com.exavalu.customer.product.portal.entities.email.EmailRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemsFromCartDetailsResponse {
	
	private String productTitle;
	private String warranty;
	private int quantity;
	private int price;
	private int deliveryCharge;
	private int totalPrice;
	private String addToCartDate;
	private String expectedDelivery;
	
}
