package com.exavalu.customer.product.portal.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.exavalu.customer.product.portal.entities.dto.payment.PaymentRequestPayload;
import com.exavalu.customer.product.portal.entities.salesforce.OrderDetails;
import com.exavalu.customer.product.portal.reusable.service.EncryptionService;
import com.exavalu.customer.product.portal.service.salesforceservice.OrderDetailsSFService;
import com.exavalu.customer.product.portal.services.PaymentService;

@RestController
@RequestMapping("v1")
public class PaymentController {

	@Autowired
	private OrderDetailsSFService orderDetailsSFService;
	@Autowired
	private EncryptionService encryptionService;
	@Autowired
	private PaymentService paymentService;

	@GetMapping("/getOrderDetails")
	public ResponseEntity<Object> getOrderDetails(
			@RequestParam(value = "customerId", required = false) String customerId,
			@RequestParam(value = "orderId", required = false) String orderId) {
		List<OrderDetails> order = new ArrayList<>();
		if (customerId != null) {
			order = orderDetailsSFService.getParticularOrder(customerId, "customerId");
		} else if (orderId != null) {
			order = orderDetailsSFService.getParticularOrder(orderId, "orderId");
		}
		if (!order.isEmpty()) {
			return ResponseEntity.status(HttpStatus.OK).body(order);
		}

		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Your haven't order any product yet!");
	}

	@PostMapping("/createOrder")
	public ResponseEntity<Object> createOrder(@RequestBody OrderDetails newOrder) {

		try {
			orderDetailsSFService.createOrder(newOrder);
		} catch (Exception e) {
//			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage().toString());

		}

		return ResponseEntity.status(HttpStatus.OK).body("Created");
	}

//payment ep official---------------------------------------------------------------------------------

	@PostMapping("/payment")
	public ResponseEntity<Object> processPayment(@RequestBody PaymentRequestPayload paymentRequestPayload) {
		Map<String, Object> response = new HashMap<>();
		if (paymentRequestPayload == null || paymentRequestPayload.getPaymentDetails() == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid payment request payload.");
		}

		PaymentRequestPayload.Payment paymentDetails = paymentRequestPayload.getPaymentDetails();

		switch (paymentDetails.getPaymentMode()) {
		case "card": {
			if(paymentRequestPayload.getCustomerDetails().getCustomerId() == null && paymentDetails.isUseCashback() ) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cashback is not applicable for guest users!");
			}
			if (paymentDetails.getCardId() == 0) {
				if ((paymentDetails.getCardNumber() == null || paymentDetails.getCvv() == null)) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Card number or CVV is missing.");
				} else {
					// Encrypt the card number and CVV
					String encryptedCardNumber = encryptionService.encrypt(paymentDetails.getCardNumber());
					String encryptedCVV = encryptionService.encrypt(paymentDetails.getCvv());

					// Set the encrypted values back into the request payload
					paymentDetails.setCardNumber(encryptedCardNumber);
					paymentDetails.setCvv(encryptedCVV);
				}
				

			} else if (paymentDetails.getCvv() == null) {

				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body("CVV is required if stored payment method is used ");
			}
			response = paymentService.processPayment(paymentRequestPayload);
			break;
		}
		case "UPI": {
			if(paymentRequestPayload.getCustomerDetails().getCustomerId() == null && paymentDetails.isUseCashback() ) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cashback is not applicable for guest users!");
			}
			if (paymentDetails.getUpiId() == null) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("UPI Id is missing.");
			}
			response = paymentService.processPayment(paymentRequestPayload);

			break;
		}
		case "cash": {
			
			if(paymentDetails.isUseCashback()) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Payment using cashback not applicable for cash on delivery!");
			}
			
			response = paymentService.processPayment(paymentRequestPayload);

			break;
		}
		default:
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("Unexpected value: " + paymentDetails.getPaymentMode());
		}

		// ----temp-------------------------------------------------------------------------------------

		int httpStatus = 200;
		if (response.get("status")!=null) {
			httpStatus = Integer.parseInt(response.get("status").toString());
			response.remove("status");
		}
		return ResponseEntity.status(httpStatus).body(response);
	}

}
