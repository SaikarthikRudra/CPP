package com.exavalu.customer.product.portal.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.exavalu.customer.product.portal.entities.dto.cart.AddToCartRequest;
import com.exavalu.customer.product.portal.entities.dto.cart.CartResponse;
import com.exavalu.customer.product.portal.entities.salesforce.CartDetails;
import com.exavalu.customer.product.portal.entities.salesforce.CustomerSF;
import com.exavalu.customer.product.portal.service.distanceapi.DistanceServiceAPI;
import com.exavalu.customer.product.portal.service.distanceapi.DistanceServiceMain;
import com.exavalu.customer.product.portal.service.salesforceservice.CartDetailsSFService;
import com.exavalu.customer.product.portal.services.CartService;
import com.exavalu.customer.product.portal.services.EmailAuthenticationService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("v1/cartDetails")
public class CartController {
	private static final Logger log = LogManager.getLogger(CartController.class);

	@Autowired
	private CartDetailsSFService cartDetailsSFService;
	@Autowired
	private EmailAuthenticationService emailAuthenticationService;

	// from salesforce database

	@GetMapping("sf/cartDetails")
	public ResponseEntity<Object> getCart() {
		List<CartDetails> cartService = cartDetailsSFService.getCartDetails();
		if (!cartService.isEmpty()) {
			return ResponseEntity.ok(cartService);
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cart details not found");
		}
	}

	@GetMapping("sf/ParticularCartDetail")
	public ResponseEntity<Object> getParticularCart(
			@RequestParam(value = "customerId", required = false) String customerId,
			@RequestParam(value = "emailId", required = false) String emailId) {

		if (customerId == null && emailId == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Either customerId or emailId is required");
		}
		List<CartDetails> cartService = new ArrayList<>();
		String customerIdOrEmailId = "";
		if (customerId != null && emailId == null) {
			customerIdOrEmailId = "customerId";
			cartService = cartDetailsSFService.getParticularCartDetail(customerId, customerIdOrEmailId);
		} else if (customerId == null && emailId != null) {
			customerIdOrEmailId = "emailId";
			cartService = cartDetailsSFService.getParticularCartDetail(emailId, customerIdOrEmailId);
		}
		// generate the response
		if (!cartService.isEmpty()) {

			try {
				ObjectMapper mapper = new ObjectMapper();
				mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

				// Convert CartDetails list to CartResponse list
				List<CartResponse> responseList = mapper.convertValue(cartService,
						new TypeReference<List<CartResponse>>() {
						});

				return ResponseEntity.ok(responseList);
			} catch (Exception e) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing cart details");
			}

		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cart details not found");
		}
	}

	@PostMapping("/sf/createcart")
	public ResponseEntity<Object> createCart(@RequestBody Map<String, Object> newCart) {

		try {
			cartDetailsSFService.createCart(newCart);
			return ResponseEntity.status(HttpStatus.CREATED)
					.body("new cart created!, Use customerId to fetch cartDetails.");

		} catch (Exception e) {
//			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}

	}
//-----------------------addToCart------------------------------------------------------------------------
	@Autowired
	private CartService cartService;
	
	@PostMapping("/addToCart")
	public ResponseEntity<Object> addToCart(@RequestBody AddToCartRequest cartPayload) {
		Map<String,Object> response = new HashMap<>();
		
		if(cartPayload.getEmailId()!=null) {
			boolean isEmailVerified = emailAuthenticationService.checkIfEmailIsAlreadyVerifiedorNot(cartPayload.getEmailId());
			if(!isEmailVerified) {
				response.put("msg", "Please verify your EmailId first!");
				return ResponseEntity.status(401).body(response);
			}
		}
		
		response = cartService.addToCartProcess(cartPayload);
		int httpStatus = Integer.parseInt(response.get("status").toString());
		response.remove("status");
		return ResponseEntity.status(httpStatus).body(response);
	}

}
