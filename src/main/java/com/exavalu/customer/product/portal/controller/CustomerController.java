package com.exavalu.customer.product.portal.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import com.exavalu.customer.product.portal.entities.mongodb.Customer;
import com.exavalu.customer.product.portal.service.mongodbservice.CustomerMongoDBService;
import com.exavalu.customer.product.portal.service.salesforceservice.CustomerDetailsSFService;
import com.exavalu.customer.product.portal.services.EmailAuthenticationService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.exavalu.customer.product.portal.entities.salesforce.CustomerSF;
import com.exavalu.customer.product.portal.reusable.service.EncryptionService;

@RestController
@RequestMapping("v1/customer")
public class CustomerController {
	private static final Logger log = LogManager.getLogger(CustomerController.class);
	@Autowired
	private CustomerMongoDBService customerMongoDBService;
	@Autowired
	private CustomerDetailsSFService customerDetailsSFService;
	@Autowired
	private EncryptionService encryptionService;
	@Autowired
	private EmailAuthenticationService emailAuthenticationService;

// mongodb database crud operations-------------------------------------------------------------------------------------
	@GetMapping("mongodb/search")
	public ResponseEntity<Object> getCustomer(@RequestParam(value = "customerId", required = false) String customerId,
			@RequestParam(value = "emailId", required = false) String emailId,
			@RequestParam("location") String location) {

		if (customerId != null && emailId == null) {
			// Encrypt card numbers
			Customer customer = customerMongoDBService.findParticularCustomer(location, customerId, "customerId");
			if (customer != null) {
				if (customer.getCardDetails() != null) {
					customer.getCardDetails().forEach((key, cardDetails) -> {
						String decryptedCardNumber = encryptionService.decrypt(cardDetails.getCardNumber());
						cardDetails.setCardNumber(decryptedCardNumber);
					});
				}

			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer not found!");
			}

			return ResponseEntity.ok(customer);
		} else if (emailId != null && customerId == null) {
			Customer customer = customerMongoDBService.findParticularCustomer(location, emailId, "emailId");
			if (customer != null) {
				if (customer.getCardDetails() != null) {
					customer.getCardDetails().forEach((key, cardDetails) -> {
						String decryptedCardNumber = encryptionService.decrypt(cardDetails.getCardNumber());
						cardDetails.setCardNumber(decryptedCardNumber);
					});
				}
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer not found!");
			}

			return ResponseEntity.ok(customer);
		} else {
			// Handle invalid request
			return ResponseEntity.badRequest().build();
		}
	}

	@PostMapping("mongodb/create")
	public ResponseEntity<Object> createCustomer(@RequestBody Customer newCustomer) {
		Map<String, String> response = new HashMap<>();
		if (newCustomer.getEmailId() == null) {
			return ResponseEntity.status(400).body("emailId is required!");
		}
		boolean isEmailVerified = emailAuthenticationService
				.checkIfEmailIsAlreadyVerifiedorNot(newCustomer.getEmailId());
		if (!isEmailVerified) {
			return ResponseEntity.status(200).body("Please verify your email first in order to register!");
		}

		try {
			// Encrypt card numbers
			if (newCustomer.getCardDetails() != null) {
				newCustomer.getCardDetails().forEach((key, cardDetails) -> {
					String encryptedCardNumber = encryptionService.encrypt(cardDetails.getCardNumber());
					cardDetails.setCardNumber(encryptedCardNumber);
				});
			}
			response = customerMongoDBService.createNewCustomer(newCustomer, newCustomer.getLocation());
		} catch (Exception e) {
			if (e.getCause().toString().contains("code=11000")) {
				// code =11000 is of duplicate entry of unique index in mongodb
				log.warn("Duplicate_Record_Entry_at_location : " + newCustomer.getLocation());
				response.put("msg", "EmailId already registered!");
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			}
		}
		return new ResponseEntity<>(response, HttpStatus.CREATED);

	}

	@PutMapping("mongodb/update")
	public ResponseEntity<Object> updateCustomer(@RequestBody Customer existingCustomer) {
		if (existingCustomer.getEmailId() == null) {
			return new ResponseEntity<>("emailId is required in the body!", HttpStatus.BAD_REQUEST);
		}
		Map<String, String> response = new HashMap<>();
		try {
			// Encrypt card numbers
			if (existingCustomer.getCardDetails() != null) {
				existingCustomer.getCardDetails().forEach((key, cardDetails) -> {
					String encryptedCardNumber = encryptionService.encrypt(cardDetails.getCardNumber());
					cardDetails.setCardNumber(encryptedCardNumber);
				});
			}
			response = customerMongoDBService.updateCustomer(existingCustomer, existingCustomer.getLocation());
		} catch (Exception e) {
			return new ResponseEntity<>(e.toString(), HttpStatus.BAD_REQUEST);
		}
		int status = Integer.parseInt(response.getOrDefault("httpStatus", "200"));
		response.remove("httpStatus");
		return ResponseEntity.status(status).body(response);

	}

// from salesforce database------------------------------------------------------------------------------------------------
	@GetMapping(value = "sf/search")
	public ResponseEntity<Object> getCustomersSF(
			@RequestParam(value = "customerId", required = false) String customerId,
			@RequestParam(value = "emailId", required = false) String emailId) {

		List<CustomerSF> customers = new ArrayList<>();
		customers = null;
		if (customerId != null && emailId == null) {
			String emailOrCustomerId = "customerId";
			customers = customerDetailsSFService.getParticularCustomer(customerId, emailOrCustomerId);
		} else if (customerId == null && emailId != null) {
			String emailOrCustomerId = "emailId";
			customers = customerDetailsSFService.getParticularCustomer(emailId, emailOrCustomerId);
		} else {
			customers = customerDetailsSFService.getCustomerList();
		}
		if (!customers.isEmpty()) {
			return ResponseEntity.ok(customers);
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("customers not found");
		}

	}

	@PostMapping("/sf/create")
	public ResponseEntity<Object> createCustomerSF(@RequestBody CustomerSF newCustomer) {

		boolean result = false;
		try {
			result = customerDetailsSFService.createCustomer(newCustomer);
		} catch (Exception e) {
			if (e.getMessage().toString().contains("DUPLICATE_VALUE")) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body("Customer with emailId or customerId is already registered!");
			}
		}
		if (result) {
			return ResponseEntity.ok("customer created successfully in salesforce customerDetails Object!");
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("error occured, please try again");
		}
	}

	@PatchMapping("/updateCustomerDetailsSF")
	public ResponseEntity<Object> updateCustomerSF(@RequestBody CustomerSF existingCustomerToUpdate) {
		Map<String, String> response = new HashMap<>();
		try {
			response = customerDetailsSFService.updateCustomerDetailsSF(existingCustomerToUpdate);
			return ResponseEntity.status(HttpStatus.CREATED).body(response);

		} catch (Exception e) {
			log.error(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

}
