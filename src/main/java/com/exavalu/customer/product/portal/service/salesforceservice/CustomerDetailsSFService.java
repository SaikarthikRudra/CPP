package com.exavalu.customer.product.portal.service.salesforceservice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.exavalu.customer.product.portal.entities.salesforce.CustomerSF;
import com.exavalu.customer.product.portal.salesforce.connectivity.SalesforceDataService;
import com.exavalu.customer.product.portal.salesforce.connectivity.SalesforceDataServiceRB;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class CustomerDetailsSFService {
	private static final Logger log = LogManager.getLogger(CustomerDetailsSFService.class);
	Map<String, String> response = new HashMap<>();
	@Autowired
	private SalesforceDataService salesforceDataService;
	@Autowired
	private SalesforceDataServiceRB salesforceDataServiceRB;

	// get particular customer using mailid or
	// customerId-----------------------------------------
	public List<CustomerSF> getParticularCustomer(String id, String emailIdOrCustomerId) {

		String query = "";
		switch (emailIdOrCustomerId) {
		case "emailId": {
			query = "SELECT Id,Name,address__c,cardDetails__c,emailId__c,firstName__c,lastName__c,gender__c,location__c,phoneNumber__c,pincode__c FROM CustomerDetails__c WHERE emailId__c = '"
					+ id + "'";
			break;
		}
		case "customerId": {
			query = "SELECT Id,Name,address__c,cardDetails__c,emailId__c,firstName__c,lastName__c,gender__c,location__c,phoneNumber__c,pincode__c FROM CustomerDetails__c WHERE Name = '"
					+ id + "'";
			break;
		}
		default:
			break;
		}
		Map<String, Object> data = salesforceDataService.getSalesforceData(query);
		return mapToCustomerList(data);
	}

// get all customer from sf db---------------------------------------------------------------
	public List<CustomerSF> getCustomerList() {
		String query = "SELECT Id,Name,address__c,cardDetails__c,emailId__c,firstName__c,lastName__c,gender__c,location__c,phoneNumber__c,pincode__c FROM CustomerDetails__c";
		Map<String, Object> data = salesforceDataServiceRB.getSalesforceData(query);
		return mapToCustomerList(data);
	}

//create new customer in sf db-----------------------------------------------------------------------

	public boolean createCustomer(CustomerSF newCustomer) throws Exception {

		Map<String, Object> customerData = customerDetailsObjectFieldMapped(newCustomer);
//		System.out.println(customerData.toString());

		try {
			String objectName = "CustomerDetails__c";
			salesforceDataService.createSalesforceRecord(objectName, customerData);
			return true;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw e;
		}
	}

	// update customerDetails serviceRB
	public Map<String, String> updateCustomerDetailsSF(CustomerSF existingCustomerUpdate) throws Exception {

		Map<String, Object> customerData = customerDetailsObjectFieldMapped(existingCustomerUpdate);
		try {

			// Check if customerId is present in productData
			if (existingCustomerUpdate.getCustomerId() == null && existingCustomerUpdate.getEmailId() == null) {
				log.error("customerId or emailId is missing.");
				throw new Exception("customerId or emailId is missing.");
			}
			if (existingCustomerUpdate.getCustomerId() == null) {
				log.error("customerId is null.");
				throw new Exception("customerId is null.");
			}
			List<CustomerSF> customerToBeUpdated = getParticularCustomer(existingCustomerUpdate.getCustomerId(),
					"customerId");

			// Check if the productToBeUpdated list is empty or null
			if (customerToBeUpdated == null || customerToBeUpdated.isEmpty()) {
				log.error("No customer found in sf db with the given customerId or emailId.");
				throw new Exception("No customer found with the given customerId or emailId.");

			}

			String objectName = "CustomerDetails__c";
			String recordId = customerToBeUpdated.get(0).getSalesforceId();
			salesforceDataService.updateSalesforceRecord(objectName, recordId, customerData);
			response.put("msg", "customer Details updated .");

		} catch (Exception e) {
			log.error("Failed to update customerDetails in salesforce: "
					+ e.getMessage().replaceAll("^.*\"message\":\"(.*?)\".*$", "$1"));
			throw new Exception("Failed to update customerDetails in salesforce: "
					+ e.getMessage().replaceAll("^.*\"message\":\"(.*?)\".*$", "$1"));
		}

		return response;
	}

// mapping functionality-----------------------------------------------------------------------------

	private List<CustomerSF> mapToCustomerList(Map<String, Object> data) {
		List<CustomerSF> customerList = new ArrayList<>();
		ObjectMapper objectMapper = new ObjectMapper();
		List<Map<String, Object>> records = (List<Map<String, Object>>) data.get("records");
		for (Map<String, Object> record : records) {
			CustomerSF customer = objectMapper.convertValue(record, CustomerSF.class);
			customerList.add(customer);
		}
		return customerList;
	}

// mapping to proper field --helper function to create new record
	private Map<String, Object> customerDetailsObjectFieldMapped(CustomerSF newCustomer) {
		Map<String, Object> customerData = new HashMap<>();
		customerData.put("Name", newCustomer.getCustomerId());
		customerData.put("address__c", newCustomer.getAddress());
		customerData.put("cardDetails__c", newCustomer.getCardDetails());
		customerData.put("emailId__c", newCustomer.getEmailId());
		customerData.put("firstName__c", newCustomer.getFirstName());
		customerData.put("lastName__c", newCustomer.getLastName());
		customerData.put("gender__c", newCustomer.getGender());
		customerData.put("location__c", newCustomer.getLocation());
		customerData.put("phoneNumber__c", newCustomer.getPhoneNumber());
		customerData.put("pincode__c", newCustomer.getPincode());
		return customerData;
	}
	
	

}
