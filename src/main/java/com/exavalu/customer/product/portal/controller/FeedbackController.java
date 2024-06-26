package com.exavalu.customer.product.portal.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.exavalu.customer.product.portal.entities.dto.orderFeedbackDetails.OrderDetailsRequest;
import com.exavalu.customer.product.portal.service.salesforceservice.FeedbackSalesforceService;

@RestController
@RequestMapping("v1/feedback")
public class FeedbackController {

	@Autowired
	private FeedbackSalesforceService FeedbackSalesforceService;

	@PostMapping("/create")
	public ResponseEntity<Object> addFeedback(@RequestBody OrderDetailsRequest newFeedback) throws Exception {
		try {
			boolean feedbackServiceProcess = FeedbackSalesforceService.feedbackServiceProcess(newFeedback);
			return ResponseEntity.ok("Product feedback successfully added");

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(e.getMessage().replaceAll("^.*\"message\":\"(.*?)\".*$", "$1"));
		}

	}

}
