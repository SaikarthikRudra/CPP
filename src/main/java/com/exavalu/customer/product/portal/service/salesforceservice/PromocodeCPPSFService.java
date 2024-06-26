package com.exavalu.customer.product.portal.service.salesforceservice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.exavalu.customer.product.portal.entities.salesforce.ProductSF;
import com.exavalu.customer.product.portal.entities.salesforce.PromocodeCPP;
import com.exavalu.customer.product.portal.salesforce.connectivity.SalesforceDataService;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class PromocodeCPPSFService {

	private static final Logger log = LogManager.getLogger(PromocodeCPPSFService.class);

	@Autowired
	private SalesforceDataService salesforceDataService;

	public List<PromocodeCPP> getParticularpromocodeDetails(String promocode) {

		String query = "SELECT Id,promoCode__c,cashback__c,discountType__c,status__c,usedByCustomerId__c FROM promocodeCPP__c WHERE promoCode__c = '"
				+ promocode + "'";

		Map<String, Object> data = salesforceDataService.getSalesforceData(query);

		return mapToPromocodeList(data);
	}

	// method to create new promocode for admin
	public Map<String, Object> createPromocode(PromocodeCPP newPromocode) throws Exception {
		Map<String, Object> response = new HashMap<>();
		Map<String, Object> promocodeData = promocodeDetailsObjectFieldMapped(newPromocode);
		try {
			String objectName = "promocodeCPP__c";
			salesforceDataService.createSalesforceRecord(objectName, promocodeData);
			response.put("msg", "New promocode added.");
		} catch (Exception e) {
			log.error("Failed to create promocode: " + e.getMessage().replaceAll("^.*\"message\":\"(.*?)\".*$", "$1"));

			throw new Exception(
					"Failed to create promocode: " + e.getMessage().replaceAll("^.*\"message\":\"(.*?)\".*$", "$1"));
		}

		return response;
	}

	// update cashbackwallet serviceRB
	public Map<String, Object> updatepromocodeStatus(PromocodeCPP promocodeToBeUpdated) throws Exception {

		Map<String, Object> response = new HashMap<>();
		Map<String, Object> promocodeStatus = new HashMap<>();
		promocodeStatus.put("status__c", promocodeToBeUpdated.getStatus());
		try {
			List<PromocodeCPP> promocodeToBeUpdatedList = new ArrayList<>();
			if (promocodeToBeUpdated.getPromoCode() != null) {
				promocodeToBeUpdatedList = getParticularpromocodeDetails(promocodeToBeUpdated.getPromoCode());

				// Check if the productToBeUpdated list is empty or null
				if (promocodeToBeUpdatedList.isEmpty()) {
					log.error("No promocode found in sf db with the given code.");
					throw new Exception("No promocode found in sf db with the given code");

				}

			}
			String objectName = "promocodeCPP__c";
			String recordId = promocodeToBeUpdatedList.get(0).getSalesforceId();
			salesforceDataService.updateSalesforceRecord(objectName, recordId, promocodeStatus);
			response.put("msg", "promocode status updated .");

		} catch (Exception e) {
			log.error("Failed to update promocode statu in salesforce: "
					+ e.getMessage().replaceAll("^.*\"message\":\"(.*?)\".*$", "$1"));
			throw new Exception("Failed to update promocode statu in salesforce: "
					+ e.getMessage().replaceAll("^.*\"message\":\"(.*?)\".*$", "$1"));
		}

		return response;
	}

	private List<PromocodeCPP> mapToPromocodeList(Map<String, Object> data) {
		List<PromocodeCPP> promocodeList = new ArrayList<>();
		ObjectMapper objectMapper = new ObjectMapper();
		List<Map<String, Object>> records = (List<Map<String, Object>>) data.get("records");
//		log.info("records" + records.toString());
		for (Map<String, Object> record : records) {
			PromocodeCPP promocode = objectMapper.convertValue(record, PromocodeCPP.class);
			promocodeList.add(promocode);
		}
		return promocodeList;
	}

	// mapping to proper field --helper function to create new record
	private Map<String, Object> promocodeDetailsObjectFieldMapped(PromocodeCPP newPromocode) {
		Map<String, Object> promocodeData = new HashMap<>();
		double cashbackDouble = Double.parseDouble(newPromocode.getCashback());
		int cashback = (int) cashbackDouble;
		promocodeData.put("cashback__c", cashback);
		promocodeData.put("promoCode__c", newPromocode.getPromoCode());
		promocodeData.put("status__c", newPromocode.getStatus());

		return promocodeData;
	}

}
