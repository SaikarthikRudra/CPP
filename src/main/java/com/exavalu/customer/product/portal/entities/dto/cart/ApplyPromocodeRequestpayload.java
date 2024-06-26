package com.exavalu.customer.product.portal.entities.dto.cart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplyPromocodeRequestpayload {
	
	private String customerId;
	private String promocode;
	
}
