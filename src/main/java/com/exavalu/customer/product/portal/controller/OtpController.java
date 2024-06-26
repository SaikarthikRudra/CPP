package com.exavalu.customer.product.portal.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.exavalu.customer.product.portal.entities.salesforce.EmailAuthentication;
import com.exavalu.customer.product.portal.reusable.service.EmailService;
import com.exavalu.customer.product.portal.reusable.service.OtpService;
import com.exavalu.customer.product.portal.services.EmailAuthenticationService;

@RestController
@RequestMapping("v1")
public class OtpController {

	@Autowired
	private EmailService emailService;

	@Autowired
	private OtpService otpService;

	@Autowired
	private EmailAuthenticationService emailAuthenticationService;

	@GetMapping("/sendOtp")
	public Map<String, String> sendOtp(@RequestParam String email) {
		
		Map<String, String> response = new HashMap<>();
		boolean isEmailAlreadyVerified = emailAuthenticationService.checkIfEmailIsAlreadyVerifiedorNot(email);
		if(isEmailAlreadyVerified) {
			response.put("msg", "Email: " + email + " is already verified!");
			return response;
		}
		response = otpService.generateOTP(email);
		
		if (response.get("otp") != null) {
			emailService.sendOtpEmail(email, response.get("otp"));
			response.remove("otp");
		}

		return response;
	}

	@PostMapping("/verifyOtp")
	public ResponseEntity<Object> verifyOtp(@RequestParam String email, @RequestParam String otp) {
		Map<String, Object> response = new HashMap<>();
		boolean isValid = otpService.verifyOTP(email, otp);

		// change status of email verification to verified if otp matches
		if (isValid) {
			EmailAuthentication emailAuthentication = new EmailAuthentication(null, email, true, null);
			response = emailAuthenticationService.emailAuthenticationServiceProcess(emailAuthentication);
		} else {
			response.put("msg", "Invalid OTP!");
		}
		int httpStatus = 200;
		if (response.get("status") != null) {
			httpStatus = Integer.parseInt(response.get("status").toString());
			response.remove("status");
		}
		return ResponseEntity.status(httpStatus).body(response);
	}
}
