package com.exavalu.customer.product.portal.service.salesforceservice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.exavalu.customer.product.portal.entities.salesforce.OrderDetails;
import com.exavalu.customer.product.portal.salesforce.connectivity.SalesforceDataService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

@Service
public class OrderDetailsSFService {

	private static final Logger log = LogManager.getLogger(CustomerDetailsSFService.class);
	Map<String, String> response = new HashMap<>();

	@Autowired
	private SalesforceDataService salesforceDataService;

	// get particular order using orderId or
	// customerId---------------------------------------------------------------------------------------
	public List<OrderDetails> getParticularOrder(String id, String orderIdOrCustomerId) {

		String query = "";
		switch (orderIdOrCustomerId) {
		case "orderId": {
			query = "SELECT Id,Name,customerId__c,deliverd_Date__c,feedback__c,order_date__c,payment_Mode__c,status__c,total_Price__c,items__c FROM orderDetails__c WHERE Name = '"
					+ id + "'";
			break;
		}
		case "customerId": {
			query = "SELECT Id,Name,customerId__c,deliverd_Date__c,feedback__c,order_date__c,payment_Mode__c,status__c,total_Price__c,items__c FROM orderDetails__c WHERE customerId__c = '"
					+ id + "'";
			break;
		}
		case "salesforceId": {
			query = "SELECT Id,Name,customerId__c,deliverd_Date__c,feedback__c,order_date__c,payment_Mode__c,status__c,total_Price__c,items__c FROM orderDetails__c WHERE Id = '"
					+ id + "'";
			break;
		}
		default:
			break;
		}
		Map<String, Object> data = salesforceDataService.getSalesforceData(query);
		return mapToOrderList(data);
	}

	public Map<String, Object> createOrder(OrderDetails newOrder) throws Exception {

		Map<String, Object> response = new HashMap<>();
		Map<String, Object> customerData = customerDetailsObjectFieldMapped(newOrder);

		try {

			String objectName = "orderDetails__c";
			response = salesforceDataService.createSalesforceRecord(objectName, customerData);

			return response;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw e;
		}
	}

	// delete particular order method
	public boolean deleteOrder(String salesforceIdToDelete) throws Exception {
		try {
			String objectName = "orderDetails__c";
			salesforceDataService.deleteSalesforceRecord(objectName, salesforceIdToDelete);
			return true;

		} catch (Exception e) {
			log.error(e.getMessage());
			throw e;
		}
	}

	// update particular order method--delivery agent
	public boolean updateOrderStatus(OrderDetails orderDetails) throws Exception {
		Map<String,Object> orderDetailsStatus = new HashMap<>();
		orderDetailsStatus.put("status__c",orderDetails.getStatus());
		orderDetailsStatus.put("deliverd_Date__c",orderDetails.getDeliveryDate());
		
		try {
			String objectName = "orderDetails__c";
			String recordId = orderDetails.getSalesforceId();
			salesforceDataService.updateSalesforceRecord(objectName,recordId,orderDetailsStatus);
			return true;

		} catch (Exception e) {
			log.error(e.getMessage());
			throw e;
		}
	}
	
	
	
	public boolean refundReplaceOrderStatus(OrderDetails orderDetails) throws Exception {
		Map<String,Object> orderDetailsStatus = new HashMap<>();
		orderDetailsStatus.put("status__c",orderDetails.getStatus());
		
		try {
			String objectName = "orderDetails__c";
			String recordId = orderDetails.getSalesforceId();
			salesforceDataService.updateSalesforceRecord(objectName,recordId,orderDetailsStatus);
			return true;

		} catch (Exception e) {
			log.error(e.getMessage());
			throw e;
		}
	}
	
	
	// mapping
	// functionality-----------------------------------------------------------------------------

	private List<OrderDetails> mapToOrderList(Map<String, Object> data) {
		List<OrderDetails> orderList = new ArrayList<>();
		ObjectMapper objectMapper = new ObjectMapper();
		List<Map<String, Object>> records = (List<Map<String, Object>>) data.get("records");
		for (Map<String, Object> record : records) {
			OrderDetails order = objectMapper.convertValue(record, OrderDetails.class);
			orderList.add(order);
		}
		return orderList;
	}

	// mapping to proper field --helper function to create new record

	private Map<String, Object> customerDetailsObjectFieldMapped(OrderDetails newOrder) {
		Map<String, Object> orderData = new HashMap<>();
		orderData.put("customerId__c", newOrder.getCustomerId());
		orderData.put("deliverd_Date__c", newOrder.getDeliveryDate());
//		orderData.put("feedback__c", newOrder.getFeedback());

		// Serialize the "items" object into a JSON string
		String itemsJson = new Gson().toJson(newOrder.getItems());

		orderData.put("items__c", itemsJson);
		orderData.put("order_date__c", newOrder.getOrderDate());
		orderData.put("payment_Mode__c", newOrder.getPaymentMode());
		orderData.put("total_Price__c", newOrder.getTotalPrice());
		orderData.put("status__c", newOrder.getStatus());

		return orderData;
	}

}
