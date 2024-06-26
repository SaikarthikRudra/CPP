package com.exavalu.customer.product.portal.service.salesforceservice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.exavalu.customer.product.portal.entities.salesforce.CartDetails;
import com.exavalu.customer.product.portal.entities.salesforce.CustomerSF;
import com.exavalu.customer.product.portal.salesforce.connectivity.SalesforceDataService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

@Service
public class CartDetailsSFService {
	private static final Logger log = LogManager.getLogger(CartDetailsSFService.class);

	@Autowired
	private SalesforceDataService salesforceDataService;

	public List<CartDetails> getCartDetails() {
		String query = "SELECT customerEmail__c,customerId__c,Items__c,Nearest_Db_Location__c,Status__c,Total_Amount__c FROM cartDetails__c";
		Map<String, Object> data = salesforceDataService.getSalesforceData(query);
		return mapToCartList(data);
	}

	public List<CartDetails> getParticularCartDetail(String id, String usingCustomerIdOrEmailId) {
		String query = "";
		switch (usingCustomerIdOrEmailId) {
		case "customerId": {
			query = "SELECT Id,customerEmail__c,customerId__c,Items__c,Nearest_Db_Location__c,Status__c,Total_Amount__c FROM cartDetails__c WHERE customerId__c = '"
					+ id + "'";
			break;
		}
		case "emailId": {
			query = "SELECT Id,customerEmail__c,customerId__c,Items__c,Nearest_Db_Location__c,Status__c,Total_Amount__c FROM cartDetails__c WHERE customerEmail__c = '"
					+ id + "'";
			log.info("query: " + query);
			break;

		}
		default:
			break;

		}
		if (query.isBlank()) {
			return null;
		}
		Map<String, Object> data = salesforceDataService.getSalesforceData(query);
		return mapToCartList(data);
	}

	private List<CartDetails> mapToCartList(Map<String, Object> data) {
		List<CartDetails> cartList = new ArrayList<>();
		ObjectMapper objectMapper = new ObjectMapper();
		List<Map<String, Object>> records = (List<Map<String, Object>>) data.get("records");
//		log.info("records" + records.toString());
		for (Map<String, Object> record : records) {
			CartDetails cart = objectMapper.convertValue(record, CartDetails.class);
			cartList.add(cart);
		}
		return cartList;
	}

	public Map<String, Object> createCart(Map<String, Object> newCart) throws Exception {
		Map<String, Object> newCartData = cartDetailsObjectFieldMapped(newCart);
		try {
			String objectName = "cartDetails__c";
			salesforceDataService.createSalesforceRecord(objectName, newCartData);

		} catch (Exception e) {

			throw new Exception(
					"Failed to create cart: " + e.getMessage().replaceAll("^.*\"message\":\"(.*?)\".*$", "$1"));
		}

		return null;
	}

	public Map<String, Object> createCart(CartDetails newCart) throws Exception {
		Map<String, Object> newCartData = cartDetailsObjectFieldMapped(newCart);
		try {
			String objectName = "cartDetails__c";
			salesforceDataService.createSalesforceRecord(objectName, newCartData);

		} catch (Exception e) {

			throw new Exception(
					"Failed to create cart: " + e.getMessage().replaceAll("^.*\"message\":\"(.*?)\".*$", "$1"));
		}

		return null;
	}

	// mapping to proper field --helper function to create new record
	private Map<String, Object> cartDetailsObjectFieldMapped(Map<String, Object> newCart) {
		Map<String, Object> customerData = new HashMap<>();
		customerData.put("customerEmail__c", newCart.get("customerEmail"));
		customerData.put("customerId__c", newCart.get("customerId"));
		// Serialize the "items" object into a JSON string
		String itemsJson = new Gson().toJson(newCart.get("items"));
		customerData.put("Items__c", itemsJson);
		customerData.put("Nearest_Db_Location__c", newCart.get("nearestDbLocation"));
		customerData.put("Status__c", newCart.get("status"));
		customerData.put("Total_Amount__c", newCart.get("totalAmount"));

		return customerData;
	}

	// update customerDetails serviceRB
	public Map<String, String> updateCartDetailsSF(CartDetails cartDetailsToBeUpdated) throws Exception {

		Map<String, String> response = new HashMap<>();
		Map<String, Object> cartData = cartDetailsObjectFieldMapped(cartDetailsToBeUpdated);
		try {

			List<CartDetails> cartToBeUpdated = getParticularCartDetail(cartDetailsToBeUpdated.getCustomerEmail(),
					"emailId");

			// Check if the productToBeUpdated list is empty or null
			if (cartToBeUpdated == null || cartToBeUpdated.isEmpty()) {
				log.error("No customer found in sf db with the given customerId or emailId.");
				throw new Exception("No customer found with the given customerId or emailId.");

			}

			String objectName = "cartDetails__c";
			String recordId = cartToBeUpdated.get(0).getSalesforceId();
			salesforceDataService.updateSalesforceRecord(objectName, recordId, cartData);
			response.put("msg", "customer Details updated .");

		} catch (Exception e) {
			throw new Exception("Failed to update customerDetails in salesforce: "
					+ e.getMessage().replaceAll("^.*\"message\":\"(.*?)\".*$", "$1"));
		}

		return response;
	}

	// update customerDetails serviceRB
	public Map<String, Object> resetCartitemsAndTotalAmountSF(String emailId) throws Exception {

		Map<String, Object> response = new HashMap<>();
		Map<String, Object> cartData = new HashMap<>();
		cartData.put("Items__c", "");
		cartData.put("Total_Amount__c", 0);
		try {

			List<CartDetails> cartToBeUpdated = getParticularCartDetail(emailId, "emailId");

			// Check if the productToBeUpdated list is empty or null
			if (cartToBeUpdated == null || cartToBeUpdated.isEmpty()) {
				log.error("No customer found in sf db with the given customerId or emailId.");
				throw new Exception("No customer found with the given customerId or emailId.");

			}

			String objectName = "cartDetails__c";
			String recordId = cartToBeUpdated.get(0).getSalesforceId();
			salesforceDataService.updateSalesforceRecord(objectName, recordId, cartData);
			response.put("msg", "customer Details updated .");

		} catch (Exception e) {
			throw new Exception("Failed to update customerDetails in salesforce: "
					+ e.getMessage().replaceAll("^.*\"message\":\"(.*?)\".*$", "$1"));
		}

		return response;
	}

	// mapping to proper field --helper function to create new record
	private Map<String, Object> cartDetailsObjectFieldMapped(CartDetails newCart) {
		Map<String, Object> customerData = new HashMap<>();
		customerData.put("customerEmail__c", newCart.getCustomerEmail());
		customerData.put("customerId__c", newCart.getCustomerId());
		// Serialize the "items" object into a JSON string
		String itemsJson = new Gson().toJson(newCart.getItems());
		customerData.put("Items__c", itemsJson);
		customerData.put("Nearest_Db_Location__c", newCart.getNearestDbLocation());
		customerData.put("Status__c", newCart.getStatus());
		System.out.println(newCart.getTotalAmount());
		customerData.put("Total_Amount__c", newCart.getTotalAmount());

		return customerData;
	}

}
