package com.exavalu.customer.product.portal.service.salesforceservice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.exavalu.customer.product.portal.entities.salesforce.PromocodeRecordsCPP;
import com.exavalu.customer.product.portal.salesforce.connectivity.SalesforceDataService;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class PromocodeRecordsCPPSFService {

	private static final Logger log = LogManager.getLogger(PromocodeRecordsCPPSFService.class);

	@Autowired
	private SalesforceDataService salesforceDataService;

	public List<PromocodeRecordsCPP> getParticularCustomerUsedPromocodeRecordDetails(String customerId) {

		String query = "SELECT Id,customerId__c,usedPromocodes__c FROM PromocodeRecordCPP__c WHERE customerId__c = '"
				+ customerId + "'";

		Map<String, Object> data = salesforceDataService.getSalesforceData(query);

		return mapToPromocodeRecordsList(data);
	}

	// method to create new promocode for admin
	public Map<String, Object> createPromocodeRecordForNewCustomer(PromocodeRecordsCPP newPromocodeRecord)
			throws Exception {
		Map<String, Object> response = new HashMap<>();
		if (newPromocodeRecord.getCustomerId() == null) {
			log.error("Failed to create promocode record: customerId is null");
			throw new Exception("Failed to create promocode record: customerId is null");
		}
		Map<String, Object> newPromocodeRecordData = promocodeDetailsObjectFieldMapped(newPromocodeRecord);
		try {
			String objectName = "PromocodeRecordCPP__c";
			salesforceDataService.createSalesforceRecord(objectName, newPromocodeRecordData);
			response.put("msg", "New promocode  record added.");
		} catch (Exception e) {
			log.error("Failed to create promocode record: "
					+ e.getMessage().replaceAll("^.*\"message\":\"(.*?)\".*$", "$1"));
			throw new Exception("Failed to create promocode record: "
					+ e.getMessage().replaceAll("^.*\"message\":\"(.*?)\".*$", "$1"));
		}

		return response;
	}

	public Map<String, Object> updateAddUsedPrommocodeToPromocodeRecords(PromocodeRecordsCPP promocodeRecordToBeUpdated)
			throws Exception {

		Map<String, Object> response = new HashMap<>();
		Map<String, Object> promocode = new HashMap<>();

		if (promocodeRecordToBeUpdated.getCustomerId() == null) {
			log.error("customerId is null");
			throw new Exception("customerId should not be null");
		}

		List<PromocodeRecordsCPP> fetchedCustomerUsedpromocodeRecords = getParticularCustomerUsedPromocodeRecordDetails(
				promocodeRecordToBeUpdated.getCustomerId());
		if (fetchedCustomerUsedpromocodeRecords.isEmpty()) {
			log.error("promocode Records not found with given customerId");
			throw new Exception("promocode Records not found with given customerId");
		}

		// Initialize existingUsedPromocodes with an empty list if null
		List<String> existingUsedPromocodes = fetchedCustomerUsedpromocodeRecords.get(0).getUsedPromocodes();
		if (existingUsedPromocodes == null) {
			existingUsedPromocodes = new ArrayList<>();
		}

		// Create a new list for updated promocodes
		List<String> usedPromocodesUpdatedList = new ArrayList<>(existingUsedPromocodes);

		// Check and add new promocode if not null or empty
		if (promocodeRecordToBeUpdated.getUsedPromocodes() != null
				&& !promocodeRecordToBeUpdated.getUsedPromocodes().isEmpty()) {
			String promocodeusedByCustomer = promocodeRecordToBeUpdated.getUsedPromocodes().get(0);
			if (promocodeusedByCustomer != null && !promocodeusedByCustomer.trim().isEmpty()) {
				usedPromocodesUpdatedList.add(promocodeusedByCustomer);
			} else {
				log.warn("Attempted to add a null or empty promocode");
			}
		}

		log.info("Used promocodes list before conversion: " + usedPromocodesUpdatedList);
		String usedPromocodes = convertListToString(usedPromocodesUpdatedList);
		log.info("Converted used promocodes string: " + usedPromocodes);

		promocode.put("usedPromocodes__c", usedPromocodes);

		try {
			String objectName = "PromocodeRecordCPP__c";
			String recordId = fetchedCustomerUsedpromocodeRecords.get(0).getSalesforceId();
			salesforceDataService.updateSalesforceRecord(objectName, recordId, promocode);
			response.put("msg", "promocode added to used field in records.");
		} catch (Exception e) {
			throw new Exception("Failed to add promocode in salesforce promocode records object: "
					+ e.getMessage().replaceAll("^.*\"message\":\"(.*?)\".*$", "$1"));
		}

		return response;
	}

	private List<PromocodeRecordsCPP> mapToPromocodeRecordsList(Map<String, Object> data) {
		List<PromocodeRecordsCPP> promocodeRecordsList = new ArrayList<>();
		ObjectMapper objectMapper = new ObjectMapper();
		List<Map<String, Object>> records = (List<Map<String, Object>>) data.get("records");
//		log.info("records" + records.toString());
		for (Map<String, Object> record : records) {
			PromocodeRecordsCPP promocodeRecord = objectMapper.convertValue(record, PromocodeRecordsCPP.class);
			promocodeRecordsList.add(promocodeRecord);
		}
		return promocodeRecordsList;
	}

	public String convertListToString(List<String> list) {
		if (list == null) {
			return "";
		}
		return list.stream().filter(Objects::nonNull).filter(s -> !s.trim().isEmpty()).collect(Collectors.joining(","));
	}

	// mapping to proper field --helper function to create new record
	private Map<String, Object> promocodeDetailsObjectFieldMapped(PromocodeRecordsCPP newPromocodeRecord) {
		Map<String, Object> promocodeRecordData = new HashMap<>();

		promocodeRecordData.put("customerId__c", newPromocodeRecord.getCustomerId());
		return promocodeRecordData;
	}

}
