package com.exavalu.customer.product.portal.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.exavalu.customer.product.portal.entities.salesforce.CustomerSF;
import com.exavalu.customer.product.portal.entities.salesforce.EmailAuthentication;
import com.exavalu.customer.product.portal.service.salesforceservice.CustomerDetailsSFService;
import com.exavalu.customer.product.portal.service.salesforceservice.EmailAuthenticationSFService;

@Service
public class EmailAuthenticationService {

	private static final Logger log = LogManager.getLogger(EmailAuthenticationService.class);

	@Autowired
	private EmailAuthenticationSFService emailAuthenticationSFService;
	@Autowired
	private CustomerDetailsSFService customerDetailsSFService;

	public Map<String, Object> emailAuthenticationServiceProcess(EmailAuthentication emailAuthentication) {
		Map<String, Object> response = new HashMap<>();
		// check if email has corresponding customerId or not
		List<CustomerSF> fetchCustomerList = customerDetailsSFService
				.getParticularCustomer(emailAuthentication.getCustomerEmail(), "emailId");
		// if yes then add email record with customerId
		if (!fetchCustomerList.isEmpty()) {
			String customerId = fetchCustomerList.get(0).getCustomerId();
			emailAuthentication.setCustomerId(customerId);
		}

		// now call create email authentication record to add verified email to object
		try {
			response = emailAuthenticationSFService.createEmailAuthenticationRecord(emailAuthentication);
			log.info(response.get("msg"));
			response.put("status", 200);
			response.put("msg", "OTP verified successfully! email verified:" + emailAuthentication.getCustomerEmail());

		} catch (Exception e) {
			response.put("status", 500);
			response.put("errorMsg", e.toString());
			log.error(e.toString());
			return response;
		}
		return response;

	}

	public boolean checkIfEmailIsAlreadyVerifiedorNot(String email) {

		// check if email is already verified or not
		List<EmailAuthentication> emailAlreadyExistList = emailAuthenticationSFService
				.getParticularEmailVerificationStatus(email, "emailId");
		if (!emailAlreadyExistList.isEmpty()) {
			boolean isEmailVerified = emailAlreadyExistList.get(0).isEmailVerified();
			if (isEmailVerified) {
				return true;
			}
		}
		return false;
	}

}
