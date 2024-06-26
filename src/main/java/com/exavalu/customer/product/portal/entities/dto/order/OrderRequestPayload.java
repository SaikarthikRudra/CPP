package com.exavalu.customer.product.portal.entities.dto.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequestPayload {
	
	private String orderId;
	private String status;
	
}
