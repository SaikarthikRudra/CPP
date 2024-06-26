package com.exavalu.customer.product.portal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import com.exavalu.customer.product.portal.entities.email.EmailRequest;
import com.exavalu.customer.product.portal.reusable.service.EmailService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
public class EmailController {

	@Autowired
	private EmailService emailService;

	@PostMapping("/send")
	public String sendEmail(@RequestBody EmailRequest emailRequest) {

		emailService.sendEmail(emailRequest.getTo(), emailRequest.getSubject(), emailRequest.getBody());
		return "email sent Successfully!";
	}

}
