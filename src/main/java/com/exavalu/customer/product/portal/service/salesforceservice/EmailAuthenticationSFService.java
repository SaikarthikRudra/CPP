package com.exavalu.customer.product.portal.service.salesforceservice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.exavalu.customer.product.portal.entities.salesforce.EmailAuthentication;
import com.exavalu.customer.product.portal.salesforce.connectivity.SalesforceDataService;
import com.fasterxml.jackson.databind.ObjectMapper;


@Service
public class EmailAuthenticationSFService {

	private static final Logger log = LogManager.getLogger(EmailAuthenticationSFService.class);

	@Autowired
	private SalesforceDataService salesforceDataService;

	public List<EmailAuthentication> getParticularEmailVerificationStatus(String id, String usingCustomerIdOrEmailId) {

		String query = "";
		switch (usingCustomerIdOrEmailId) {
		case "customerId": {
			query = "SELECT Id, customerEmail__c,customerId__c,isEmailVerified__c FROM EmailAuthenticationCPP__c WHERE customerId__c = '"
					+ id + "'";
			break;
		}
		case "emailId": {
			query = "SELECT Id, customerEmail__c,customerId__c,isEmailVerified__c FROM EmailAuthenticationCPP__c WHERE customerEmail__c = '"
					+ id + "'";

			break;

		}
		default:
			break;

		}
		if (query.isBlank()) {
			return null;
		}
		Map<String, Object> data = salesforceDataService.getSalesforceData(query);
		return mapToEmailAuthenticationList(data);
	}

	// method to create new record for EmailAuthentication for userEmailId
	public Map<String, Object> createEmailAuthenticationRecord(EmailAuthentication emailAuthentication)
			throws Exception {
		
		//check if email is already verified and customerId is empty
		Map<String, Object> response = new HashMap<>();
		
		Map<String, Object> emailAuthenticationData = emailAuthenticationObjectFieldMapped(emailAuthentication);
		try {
			String objectName = "EmailAuthenticationCPP__c";
			salesforceDataService.createSalesforceRecord(objectName, emailAuthenticationData);
			log.info("New email added to emailAuthentication Record.");
			response.put("msg", "New email added to emailAuthentication Record.");
		} catch (Exception e) {
			log.error("Failed to add emailId to emailAuthenticationRecord: "
					+ e.getMessage().replaceAll("^.*\"message\":\"(.*?)\".*$", "$1"));
			throw new Exception("Failed to add emailId to emailAuthenticationRecord: "
					+ e.getMessage().replaceAll("^.*\"message\":\"(.*?)\".*$", "$1"));
		}

		return response;
	}

	public Map<String, Object> updateEmailRecordInEmailAuthentication(
			EmailAuthentication emailAuthenticationRecordTobeUpdated) throws Exception {
		Map<String, Object> response = new HashMap<>();
		Map<String, Object> emailStatus = new HashMap<>();
		emailStatus.put("isEmailVerified__c", emailAuthenticationRecordTobeUpdated.isEmailVerified());
		if (emailAuthenticationRecordTobeUpdated.getCustomerId() != null) {
			emailStatus.put("customerId__c", emailAuthenticationRecordTobeUpdated.getCustomerId());

		}
		try {
			List<EmailAuthentication> emailRecordToBeUpdatedList = new ArrayList<>();
			if (emailAuthenticationRecordTobeUpdated.getCustomerEmail() != null) {
				emailRecordToBeUpdatedList = getParticularEmailVerificationStatus(
						emailAuthenticationRecordTobeUpdated.getCustomerEmail(), "emailId");

				// Check if the productToBeUpdated list is empty or null
				if (emailRecordToBeUpdatedList.isEmpty()) {
					log.error("No email record found in sf db with the given emailId.");
					throw new Exception("No email record found in sf db with the given emailId.");

				}

			}
			String objectName = "EmailAuthenticationCPP__c";
			String recordId = emailRecordToBeUpdatedList.get(0).getSalesforceId();
			salesforceDataService.updateSalesforceRecord(objectName, recordId, emailStatus);
			response.put("msg", "email verification status updated .");
			log.info("email verification status updated .");

		} catch (Exception e) {
			log.error("Failed to update email verification status in salesforce: "
					+ e.getMessage().replaceAll("^.*\"message\":\"(.*?)\".*$", "$1"));
			throw new Exception("Failed to update email verification status in salesforce: "
					+ e.getMessage().replaceAll("^.*\"message\":\"(.*?)\".*$", "$1"));
		}

		return response;
	}

	// mapping to proper field --helper function to create new record
	private Map<String, Object> emailAuthenticationObjectFieldMapped(EmailAuthentication emailAuthentication) {
		Map<String, Object> emailAuthenticationData = new HashMap<>();
		emailAuthenticationData.put("customerEmail__c", emailAuthentication.getCustomerEmail());
		emailAuthenticationData.put("isEmailVerified__c", emailAuthentication.isEmailVerified());
		if (emailAuthentication.getCustomerId() != null) {
			emailAuthenticationData.put("customerId__c", emailAuthentication.getCustomerId());
		}
		

		return emailAuthenticationData;
	}
	// mapping
	// functionality-----------------------------------------------------------------------------

	private List<EmailAuthentication> mapToEmailAuthenticationList(Map<String, Object> data) {
		List<EmailAuthentication> emailAuthenticationList = new ArrayList<>();
		ObjectMapper objectMapper = new ObjectMapper();
		List<Map<String, Object>> records = (List<Map<String, Object>>) data.get("records");
		for (Map<String, Object> record : records) {
			EmailAuthentication emailAuthentication = objectMapper.convertValue(record, EmailAuthentication.class);
			emailAuthenticationList.add(emailAuthentication);
		}
		return emailAuthenticationList;
	}

}
