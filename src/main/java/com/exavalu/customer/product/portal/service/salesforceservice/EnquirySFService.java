package com.exavalu.customer.product.portal.service.salesforceservice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.exavalu.customer.product.portal.entities.salesforce.Enquiry;
import com.exavalu.customer.product.portal.salesforce.connectivity.SalesforceDataService;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@RestController
public class EnquirySFService {

	private static final Logger log = LogManager.getLogger(EnquirySFService.class);

	@Autowired
	private SalesforceDataService salesforceDataService;
	
	@GetMapping("/enquiry/email")
	public List<Enquiry> getAllEnquiryWithMailId(String emailId) {

		String query = "SELECT Id,emailId__c,productTitle__c,quantity__c FROM Enquiry__c WHERE emailId__c = '" + emailId
				+ "'";

		Map<String, Object> data = salesforceDataService.getSalesforceData(query);
		return mapToEnquiryList(data);
	}
	@GetMapping("/enquiry")
	public List<Enquiry> getAllEnquiry() {

		String query = "SELECT Id,emailId__c,productTitle__c,quantity__c FROM Enquiry__c";

		Map<String, Object> data = salesforceDataService.getSalesforceData(query);
		return mapToEnquiryList(data);
	}

	// create new enquiry in sf
	// db-----------------------------------------------------------------------

	public boolean createEnquiry(Enquiry enquiry) throws Exception {

		Map<String, Object> enquiryData = enquiryDetailsObjectFieldMapped(enquiry);

		try {
			String objectName = "Enquiry__c";
			salesforceDataService.createSalesforceRecord(objectName, enquiryData);
			return true;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw e;
		}
	}

	// delete particular enquiry method
	public boolean deleteEnquiry(String salesforceIdToDelete) throws Exception {
		try {
			String objectName = "Enquiry__c";
			salesforceDataService.deleteSalesforceRecord(objectName, salesforceIdToDelete);
			return true;

		} catch (Exception e) {
			log.error(e.getMessage());
			throw e;
		}
	}

	// mapping to proper field --helper function to create new record
	private Map<String, Object> enquiryDetailsObjectFieldMapped(Enquiry enquiry) {

		Map<String, Object> enquiryData = new HashMap<>();
		enquiryData.put("emailId__c", enquiry.getEmailId());
		enquiryData.put("productTitle__c", enquiry.getProductTitle());
		enquiryData.put("quantity__c", enquiry.getQuantity());

		return enquiryData;
	}
	// mapping
	// functionality-----------------------------------------------------------------------------

	private List<Enquiry> mapToEnquiryList(Map<String, Object> data) {
		List<Enquiry> enquiryList = new ArrayList<>();
		ObjectMapper objectMapper = new ObjectMapper();
		List<Map<String, Object>> records = (List<Map<String, Object>>) data.get("records");
		for (Map<String, Object> record : records) {
			Enquiry enquiry = objectMapper.convertValue(record, Enquiry.class);
			enquiryList.add(enquiry);
		}
		return enquiryList;
	}

}
