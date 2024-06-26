package com.exavalu.customer.product.portal.service.salesforceservice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.exavalu.customer.product.portal.entities.dto.orderFeedbackDetails.FeedbackDetails;
import com.exavalu.customer.product.portal.entities.dto.orderFeedbackDetails.OrderDetailsRequest;
import com.exavalu.customer.product.portal.entities.salesforce.ItemsFromCartDetails;
import com.exavalu.customer.product.portal.entities.salesforce.OrderDetails;
import com.exavalu.customer.product.portal.salesforce.connectivity.SalesforceDataService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Service
public class FeedbackSalesforceService {
	private static final Logger log = LogManager.getLogger(ProductSalesforceService.class);

	@Autowired
	private SalesforceDataService salesforceDataService;

	@Autowired
	private OrderDetailsSFService orderDetailsSFService;

	String objectName = "orderDetails__c";

	// Add feedback
	public boolean feedbackServiceProcess(OrderDetailsRequest newFeedback) throws Exception {
		Map<String, Object> feedbackStatus = new HashMap<>();
		String orderId = newFeedback.getOrderId();
		FeedbackDetails getFeedback = newFeedback.getFeedback();
		String productTitle = getFeedback.getProductTitle();
		List<OrderDetails> orderDetailsList = orderDetailsSFService.getParticularOrder(orderId, "orderId");

		List<String> productTitleOrder = new ArrayList<>();
		List<String> feedbackTitle = new ArrayList<>();
		JsonNode feedbackDetails = null;

		if (orderDetailsList == null) {
			throw new Exception("Order history doesn't exist! ");

		}

		for (OrderDetails orderDetails : orderDetailsList) {

			Map<String, ItemsFromCartDetails> orderItems = orderDetails.getItems();
			String feedback = orderDetails.getFeedback();

			if (feedback != null && !feedback.isEmpty() && feedback != "null") {
				feedbackDetails = extractFeedbackdata(feedback);
				feedbackTitle.addAll(getFeedbackTitle(feedbackDetails));
			}
			for (Map.Entry<String, ItemsFromCartDetails> entry : orderItems.entrySet()) {
				productTitleOrder.add(entry.getValue().getProductTitle());
			}

			if (isBlacklistedProduct(productTitle, feedbackTitle)) {
				throw new Exception("Feedback already added for mentioned product title - " + productTitle);
			}

			if (orderDetails.getStatus().equalsIgnoreCase("Delivered")) {
				if (!isProductInOrder(productTitle, productTitleOrder)) {
					throw new Exception("Product is not valid for the orderID");
				}
				String addOrUpdateFeedback = addOrUpdateFeedback(orderDetails, feedbackDetails, getFeedback);
				feedbackStatus.put("Name", orderId);
				feedbackStatus.put("feedback__c", addOrUpdateFeedback);
				callSalesforce(orderDetails.getSalesforceId(), feedbackStatus);
				log.info("Add feedback successfull! ");
				return true;
			} else {
				throw new Exception("Product is not delivered yet! ");
			}

		}

		return true;
	}

	// Check if the product is in the order list or not
	private boolean isProductInOrder(String productTitle, List<String> productTitleOrder) {
		for (String productList : productTitleOrder) {
			if (productList.equals(productTitle)) {
				log.info("Product details belongs to the order details! ");
				return true;
			}
		}
		log.error("Product details doesn't belongs to the order details! ");
		return false;
	}

	// Check the feedback already exist for the product or not
	private boolean isBlacklistedProduct(String productTitle, List<String> productTitleFeedback) {
		for (String blacklistedProduct : productTitleFeedback) {
			if (blacklistedProduct.equals(productTitle)) {
				log.info("Feedback exist for product " + productTitle);
				return true;
			}
		}
		log.error("Feedback doesn't exist! ");
		return false;
	}

	// Get product title from feedback
	private List<String> getFeedbackTitle(JsonNode feedbackDetails) {
		List<String> productTitleFeedback = new ArrayList<>();
		Iterator<Map.Entry<String, JsonNode>> fields = feedbackDetails.fields();
		while (fields.hasNext()) {
			Map.Entry<String, JsonNode> entry = fields.next();
			String key = entry.getKey();
			JsonNode value = entry.getValue();
			productTitleFeedback.add(value.get("productTitle").asText());
			log.info("Product title fetched from feedback ! ");
		}
		return productTitleFeedback;

	}

	// Call salesforce to add feedback
	private boolean callSalesforce(String recordId, Map<String, Object> feedbackStatus) throws Exception {
		try {
			salesforceDataService.updateSalesforceRecord(objectName, recordId, feedbackStatus);
			log.info("Feedback added to Salesforce! ");
			return true;
		} catch (Exception e) {
			log.info("Feedback adding failed to Salesforce! ");
			throw new Exception("Feedback update failed! ");
		}
	}

	// Add feedback based on previous value
	private String addOrUpdateFeedback(OrderDetails orderDetails, JsonNode feedbackDetails,
			FeedbackDetails inputFeedbackDetails) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode finalObject = mapper.createObjectNode();

		String updatedFeedbackJsonString;
		if (feedbackDetails != null && !feedbackDetails.isEmpty()) {
			finalObject.setAll((ObjectNode) feedbackDetails);
		}

		ObjectNode feedbackStatusNode = mapper.createObjectNode();
		String key;

		if (feedbackDetails == null) {
			key = "1";

		} else {
			key = String.valueOf(countInstances(feedbackDetails) + 1);
		}
		feedbackStatusNode.set(key, mapper.valueToTree(inputFeedbackDetails));
		finalObject.setAll(feedbackStatusNode);

		updatedFeedbackJsonString = mapper.writeValueAsString(finalObject);
		log.info("Feedback detail is converted to String! ");
		return updatedFeedbackJsonString;

	}

	// Count number of feedback present in orderdetails
	public int countInstances(JsonNode feedbackDetails) {
		int count = 0;
		Iterator<Map.Entry<String, JsonNode>> fields = feedbackDetails.fields();
		while (fields.hasNext()) {
			Map.Entry<String, JsonNode> entry = fields.next();
			String key = entry.getKey();
			JsonNode value = entry.getValue();
			count++;
		}
		log.info("Feedback count is Salesforce: " + count);
		return count;
	}

	// convert feedback - text to Json
	private JsonNode extractFeedbackdata(String feedback) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode feedbackOrderNode = mapper.readTree(feedback);
		log.info("Extract feedback from the object is doone! ");
		return feedbackOrderNode;

	}
}