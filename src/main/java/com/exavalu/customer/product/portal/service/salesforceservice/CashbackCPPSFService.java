package com.exavalu.customer.product.portal.service.salesforceservice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.exavalu.customer.product.portal.entities.salesforce.CashbackCPP;
import com.exavalu.customer.product.portal.salesforce.connectivity.SalesforceDataService;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class CashbackCPPSFService {
	private static final Logger log = LogManager.getLogger(CashbackCPPSFService.class);

	@Autowired
	private SalesforceDataService salesforceDataService;

	public List<CashbackCPP> getParticularCashbackWallet(String id, String usingCustomerIdOrEmailId) {
		String query = "";
		switch (usingCustomerIdOrEmailId) {
		case "customerId": {
			query = "SELECT Id, Name,customerEmail__c,cashbackWallet__c FROM CashbackCPP__c WHERE Name = '" + id + "'";
			break;
		}
		case "emailId": {
			query = "SELECT Id,Name,customerEmail__c,cashbackWallet__c FROM CashbackCPP__c WHERE customerEmail__c = '"
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
		return mapToCashbackList(data);
	}

	public Map<String, Object> createCashbackWallet(CashbackCPP newCashbackWallet) throws Exception {
		Map<String, Object> response;
		if (newCashbackWallet.getCustomerEmail() == null || newCashbackWallet.getCustomerId() == null) {
			throw new Exception("Failed to create cashbackWallet: Email or customerId is null ");
		}
		Map<String, Object> newCashbackData = cashbackDetailsObjectFieldMapped(newCashbackWallet);
		try {
			String objectName = "CashbackCPP__c";
			response = salesforceDataService.createSalesforceRecord(objectName, newCashbackData);

		} catch (Exception e) {
			
			log.error("Failed to create cashbackWallet: "
					+ e.getMessage().replaceAll("^.*\"message\":\"(.*?)\".*$", "$1"));

			throw new Exception("Failed to create cashbackWallet: "
					+ e.getMessage().replaceAll("^.*\"message\":\"(.*?)\".*$", "$1"));
		}

		return response;
	}

	// update cashbackwallet serviceRB
	public Map<String, String> updateCashbackWalletActive(CashbackCPP cashbackpointsTobeUpdated) throws Exception {

		Map<String, String> response = new HashMap<>();
		Map<String, Object> cashbackPoint = new HashMap<>();
		cashbackPoint.put("cashbackWallet__c", cashbackpointsTobeUpdated.getCashbackWallet());
		try {
			List<CashbackCPP> cashbackWalletTobeUpdated = new ArrayList<>();
			if (cashbackpointsTobeUpdated.getCustomerId() != null) {
				cashbackWalletTobeUpdated = getParticularCashbackWallet(cashbackpointsTobeUpdated.getCustomerId(),
						"customerId");

				// Check if the productToBeUpdated list is empty or null
				if (cashbackWalletTobeUpdated.isEmpty()) {
					log.error("No cashback wallet found in sf db with the given customerId.");
					throw new Exception("No cashback wallet found in sf db with the given customerId.");

				}

			} else if (cashbackpointsTobeUpdated.getCustomerEmail() != null) {
				cashbackWalletTobeUpdated = getParticularCashbackWallet(cashbackpointsTobeUpdated.getCustomerEmail(),
						"emailId");

				// Check if the productToBeUpdated list is empty or null
				if (cashbackWalletTobeUpdated.isEmpty()) {
					log.error("No cashback wallet found in sf db with the given emailId.");
					throw new Exception("No cashback wallet found in sf db with the given emailId.");

				}
			}
			String objectName = "CashbackCPP__c";
			String recordId = cashbackWalletTobeUpdated.get(0).getSalesforceId();
			salesforceDataService.updateSalesforceRecord(objectName, recordId, cashbackPoint);
			response.put("msg", "customer Details updated .");

		} catch (Exception e) {
			log.error("Failed to update customerDetails in salesforce: "
					+ e.getMessage().replaceAll("^.*\"message\":\"(.*?)\".*$", "$1"));
			throw new Exception("Failed to update customerDetails in salesforce: "
					+ e.getMessage().replaceAll("^.*\"message\":\"(.*?)\".*$", "$1"));
		}

		return response;
	}

	// update cashbackwallet serviceRB
	public Map<String, String> updateCashbackWalletPending(CashbackCPP cashbackpointsTobeUpdated) throws Exception {

		Map<String, String> response = new HashMap<>();
		Map<String, Object> cashbackPoint = new HashMap<>();
		cashbackPoint.put("cashbackWalletPending__c", cashbackpointsTobeUpdated.getCashbackWallet());
		try {
			List<CashbackCPP> cashbackWalletTobeUpdated = new ArrayList<>();
			if (cashbackpointsTobeUpdated.getCustomerId() != null) {
				cashbackWalletTobeUpdated = getParticularCashbackWallet(cashbackpointsTobeUpdated.getCustomerId(),
						"customerId");

				// Check if the productToBeUpdated list is empty or null
				if (cashbackWalletTobeUpdated.isEmpty()) {
					log.error("No cashback wallet found in sf db with the given customerId.");
					throw new Exception("No cashback wallet found in sf db with the given customerId.");

				}

			} else if (cashbackpointsTobeUpdated.getCustomerEmail() != null) {
				cashbackWalletTobeUpdated = getParticularCashbackWallet(cashbackpointsTobeUpdated.getCustomerEmail(),
						"emailId");

				// Check if the productToBeUpdated list is empty or null
				if (cashbackWalletTobeUpdated.isEmpty()) {
					log.error("No cashback wallet found in sf db with the given emailId.");
					throw new Exception("No cashback wallet found in sf db with the given emailId.");

				}
			}
			String objectName = "CashbackCPP__c";
			String recordId = cashbackWalletTobeUpdated.get(0).getSalesforceId();
			salesforceDataService.updateSalesforceRecord(objectName, recordId, cashbackPoint);
			response.put("msg", "customer Details updated .");

		} catch (Exception e) {
			throw new Exception("Failed to update customerDetails in salesforce: "
					+ e.getMessage().replaceAll("^.*\"message\":\"(.*?)\".*$", "$1"));
		}

		return response;
	}

	// mapping to proper field --helper function to create new record
	private Map<String, Object> cashbackDetailsObjectFieldMapped(CashbackCPP newCashbackWallet) {
		Map<String, Object> cashbackData = new HashMap<>();
		cashbackData.put("Name", newCashbackWallet.getCustomerId());
		cashbackData.put("customerEmail__c", newCashbackWallet.getCustomerEmail());
		cashbackData.put("cashbackWallet__c", newCashbackWallet.getCashbackWallet());
		return cashbackData;
	}

	private List<CashbackCPP> mapToCashbackList(Map<String, Object> data) {
		List<CashbackCPP> cashbackWalletList = new ArrayList<>();
		ObjectMapper objectMapper = new ObjectMapper();
		List<Map<String, Object>> records = (List<Map<String, Object>>) data.get("records");
//		log.info("records" + records.toString());
		for (Map<String, Object> record : records) {
			CashbackCPP cashbackWallet = objectMapper.convertValue(record, CashbackCPP.class);
			cashbackWalletList.add(cashbackWallet);
		}
		return cashbackWalletList;
	}
}
