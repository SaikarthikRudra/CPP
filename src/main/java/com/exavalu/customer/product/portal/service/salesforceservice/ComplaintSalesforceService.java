package com.exavalu.customer.product.portal.service.salesforceservice;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.exavalu.customer.product.portal.entities.salesforce.Complaint;
import com.exavalu.customer.product.portal.entities.salesforce.ItemsFromCartDetails;
import com.exavalu.customer.product.portal.entities.salesforce.OrderDetails;
import com.exavalu.customer.product.portal.salesforce.connectivity.SalesforceDataService;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ComplaintSalesforceService {
	private static final Logger log = LogManager.getLogger(ProductSalesforceService.class);

	Map<String, String> response = new HashMap<>();

	String objectName = "complaint__c";

	@Autowired
	private OrderDetailsSFService orderDetailsSFService;

	@Autowired
	private SalesforceDataService salesforceDataService;

	String queryString = "SELECT Name,customerId__c,date_of_complaint__c, description__c, complaint_category__c, status__c, action__c, productTitle__c, quantity__c, orderId__c, reason__c FROM complaint__c ";

	// Create complaint
	public Map<String, String> addComplaint(Complaint newComplaint) throws Exception {
		Map<String, Object> complaintData = complaintDetailsObjectFieldMapped(newComplaint, false);

		String orderId = newComplaint.getOrderId();
		String customerId = newComplaint.getCustomerId();
		String productTitle = newComplaint.getProductTitle();
		String complaintCategory = newComplaint.getComplaint_category().toString();

		OrderDetails orderdetail = getOrderDetails(orderId);

		List<String> productTitles = new ArrayList<>();

		ItemsFromCartDetails ItemList = null;

		String customerIdOrder = orderdetail.getCustomerId();
		String deliveryDateString = orderdetail.getDeliveryDate();
		String status = orderdetail.getStatus();

		if (orderdetail.getItems() != null && !orderdetail.getItems().isEmpty()) {
			for (Map.Entry<String, ItemsFromCartDetails> entry : orderdetail.getItems().entrySet()) {
				ItemsFromCartDetails item = entry.getValue();
				if (item != null && item.getProductTitle() != null) {
					productTitles.add(item.getProductTitle());
					if (item.getProductTitle().contains(newComplaint.getProductTitle())) {
						ItemList = entry.getValue();
					}
				}

			}
		}

		LocalDate deliveryDate = deliveryDateFormat(deliveryDateString);

		verifyCustomerId(customerId, customerIdOrder);

		int inputQuantity = newComplaint.getQuantity();

		validateProductDetails(productTitles, productTitle, inputQuantity, ItemList);

		if (((status.contains("delivered") || status.contains("Delivered"))
				&& (complaintCategory.contains("Wrong_Items") || complaintCategory.contains("Damaged_Items")))
				|| (status.contains("In-Transit") && complaintCategory.contains("Delivery_Delay"))) {

			if (deliveryDate != null && ChronoUnit.DAYS.between(deliveryDate, LocalDate.now()) <= 10) {

				List<Complaint> complaint = getComplaint(newComplaint.getProductTitle(), orderId);

				if (complaint.isEmpty()) {
					addComplaintSalesforce(complaintData);
					log.info("Added complaint details!");

				} else {
					throw new Exception("The complaint regarding the mentioned orderId " + orderId
							+ " and product title " + newComplaint.getProductTitle() + " is already present");

				}

			} else {
				throw new Exception("The return period of the mentioned item is over");
			}

		} else {
			throw new Exception("The status of the product is invalid for the given complaint category");
		}
		return response;
	}

	// Add complaint data in salesforce record
	private Map<String, String> addComplaintSalesforce(Map<String, Object> complaintData) throws Exception {
		Map<String, Object> createSalesforceRecord = salesforceDataService.createSalesforceRecord(objectName,
				complaintData);
		String salesforceId = (String) createSalesforceRecord.get("salesforceId");
		salesforceId = salesforceId.substring(0, salesforceId.length() - 3);
		response.put("message", "Complaint has been successfully saved. Your complaint ID is " + salesforceId);
		log.info("Add complaint successfull in Salesforce!");
		return response;
	}

	// Verify product details
	private void validateProductDetails(List<String> productTitles, String productTitle, int inputQuantity,
			ItemsFromCartDetails items) throws Exception {

		int sum = 0;
		if (items.getProductTitle().contains(productTitle)) {
			sum = items.getQuantity();
		}
		log.info("Given product detail is valid!");

		if (!productTitles.contains(productTitle) || inputQuantity > sum) {
			throw new Exception(
					"The given product was not bought in the order with the given OrderID or the quantity of complaint is greater than the quantity bought");
		}

	}

	// Verify customer ID
	private void verifyCustomerId(String customerId, String customerIdOrder) throws Exception {
		if (!customerId.equals(customerIdOrder)) {
			log.error("Order ID not matched");
			throw new Exception("The given orderID does not match the given customerID");

		}

	}

	// Check delivery date and if required format it
	private LocalDate deliveryDateFormat(String deliveryDateString) throws Exception {
		LocalDate deliveryDate = null;
		if (deliveryDateString != null && !deliveryDateString.isEmpty()) {
			try {
				deliveryDate = LocalDate.parse(deliveryDateString, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
				log.info("Parsing date is successfull!");
			} catch (DateTimeParseException e) {
				try {
					// If the date format is different
					deliveryDate = LocalDate.parse(deliveryDateString, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
					log.info("Parsing date is successfull!");
				} catch (DateTimeParseException ex) {
					log.error("Parsing date failed");
					throw new Exception("The item is not yet delivered. ");
				}
			}
		}

		return deliveryDate;

	}

	// Validate orderId before processing and get order details
	private OrderDetails getOrderDetails(String orderId) throws Exception {
		OrderDetails orderdetail = new OrderDetails();
		if (orderId == null || orderId.isEmpty()) {
			throw new Exception("OrderID is null or empty");
		}

		List<OrderDetails> order = orderDetailsSFService.getParticularOrder(orderId, "orderId");
		if (order.isEmpty()) {
			throw new Exception("No order found for OrderID: " + orderId);
		}
		log.info("Order found for the given order ID!");
		orderdetail = order.get(0);
		return orderdetail;
	}

	// Get complaint details
	public List<Complaint> getComplaint(String productTitle, String orderID) {
		String query = queryString + "WHERE productTitle__c = '" + productTitle + "' AND orderID__c = '" + orderID
				+ "'";
		Map<String, Object> data = salesforceDataService.getSalesforceData(query);
		log.info("Get complaint details based on product title and order id is successfull!");
		return mapToComplaintList(data);
	}

	public List<Complaint> getComplaint(String complaintId) {
		String query = queryString + "WHERE Name = '" + complaintId + "'";
		Map<String, Object> data = salesforceDataService.getSalesforceData(query);
		log.info("Get complaint details based on complaint id is successfull!");
		return mapToComplaintList(data);
	}

	// close complaint
	public Map<String, String> closeComplaint(Complaint newComplaint, String complaintId, String action)
			throws Exception {
		Map<String, Object> complaintData = complaintDetailsObjectFieldMapped(newComplaint, true);
		OrderDetails orderdetail = getOrderDetails(newComplaint.getOrderId());
		List<Complaint> complaintDetails = getComplaint(complaintId);
		complaintData.put("status__c", newComplaint.getStatus());
		complaintData.put("reason__c", newComplaint.getReason());
		if (!complaintDetails.isEmpty() && complaintDetails != null) {
			for (Complaint complaint : complaintDetails) {
				if (complaint.getStatus().equals("Active")) {
					switch (action) {
					case ("Refund"):
						complaintData.put("action__c", action);
						orderdetail.setStatus(action);
						orderDetailsSFService.refundReplaceOrderStatus(orderdetail);
						response.put("message", "Complaint with ID " + complaintId + " has been successfully closed");

						break;

					case ("Replace"):

						complaintData.put("action__c", action);
						orderdetail.setStatus("In-Transit");
						orderDetailsSFService.refundReplaceOrderStatus(orderdetail);
						response.put("message", "Complaint with ID " + complaintId + " has been successfully closed");
						break;

					default:

						complaintData.put("action__c", "None");
						response.put("message", "Complaint with ID " + complaintId + " has been successfully closed");
						break;
					}
				} else {
					throw new Exception("The complaint with the given ID is already closed. ");
				}
				salesforceDataService.updateSalesforceRecord(objectName, complaintId, complaintData);
				log.info("Close complaint successfull!");
			}
		}

		return response;

	}

	// ------ Complaint list mapping -------
	private List<Complaint> mapToComplaintList(Map<String, Object> data) {

		List<Complaint> ComplaintList = new ArrayList<>();
		ObjectMapper objectMapper = new ObjectMapper();
		List<Map<String, Object>> records = (List<Map<String, Object>>) data.get("records");
		for (Map<String, Object> record : records) {

			Complaint complaint = objectMapper.convertValue(record, Complaint.class);
			ComplaintList.add(complaint);

		}
		return ComplaintList;
	}

	// mapping to proper field --helper function to create new record
	private Map<String, Object> complaintDetailsObjectFieldMapped(Complaint newcomplaint, boolean forUpdate) {
		Map<String, Object> complaintData = new HashMap<>();
		if (forUpdate) {

			complaintData.put("status__c", newcomplaint.getStatus());
			complaintData.put("reason__c", newcomplaint.getReason());
		} else {
			complaintData.put("orderId__c", newcomplaint.getOrderId());
			complaintData.put("productTitle__c", newcomplaint.getProductTitle());
			complaintData.put("quantity__c", newcomplaint.getQuantity());
			complaintData.put("customerId__c", newcomplaint.getCustomerId());
			complaintData.put("complaint_category__c", newcomplaint.getComplaint_category());
			complaintData.put("description__c", newcomplaint.getDescription());
			complaintData.put("status__c", "Active");
			complaintData.put("date_of_complaint__c",
					LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

		}

		return complaintData;
	}

}
