package com.exavalu.customer.product.portal.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.exavalu.customer.product.portal.entities.dto.order.OrderRequestPayload;
import com.exavalu.customer.product.portal.services.OrderService;

@RestController
@RequestMapping("v1/delivery-agent")
public class OrderController {

	@Autowired
	private OrderService orderService;

	@PutMapping("/changeOrderStatus")
	public ResponseEntity<Object> createOrder(@RequestBody OrderRequestPayload orderRequestPayload) {
		
		Map<String,Object> response = new HashMap<>();

		// check if orderId is sent or not
		if (orderRequestPayload.getOrderId() == null || orderRequestPayload.getStatus() == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Either orderId or status is missing!");
		}
		switch (orderRequestPayload.getStatus()) {
		case "delivered": {
			try {
				response = orderService.orderServiceProcess(orderRequestPayload);

			} catch (Exception e) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage().toString());
			}

			break;
		}
		case "cancelled": {
			try {
				response = orderService.orderServiceProcess(orderRequestPayload);
			} catch (Exception e) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage().toString());
			}

			break;
		}
		default:
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("Unexpected value: " + orderRequestPayload.getStatus());
		}
		int httpStatus = 200;
		if (response.get("status")!=null) {
			httpStatus = Integer.parseInt(response.get("status").toString());
			response.remove("status");
		}

		return ResponseEntity.status(httpStatus).body(response);
	}

}
