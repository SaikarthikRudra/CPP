package com.exavalu.customer.product.portal.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.exavalu.customer.product.portal.entities.dto.order.OrderRequestPayload;
import com.exavalu.customer.product.portal.entities.mongodb.Product;
import com.exavalu.customer.product.portal.entities.salesforce.ItemsFromCartDetails;
import com.exavalu.customer.product.portal.entities.salesforce.OrderDetails;
import com.exavalu.customer.product.portal.reusable.service.GetCurrentDate;
import com.exavalu.customer.product.portal.service.mongodbservice.ProductMongoDBService;
import com.exavalu.customer.product.portal.service.salesforceservice.OrderDetailsSFService;

@Service
public class OrderService {
	private static final Logger log = LogManager.getLogger(OrderService.class);

	@Autowired
	private OrderDetailsSFService orderDetailsSFService;

	@Autowired
	private GetCurrentDate getCurrentDate;

	@Autowired
	private ProductMongoDBService productMongoDBService;

	public Map<String, Object> orderServiceProcess(OrderRequestPayload orderRequestPayload) {
		Map<String, Object> response = new HashMap<>();

		List<OrderDetails> fetchedOrderDetails = orderDetailsSFService
				.getParticularOrder(orderRequestPayload.getOrderId(), "orderId");
		// check if orderId is valid or not
		if (fetchedOrderDetails.isEmpty()) {
			response.put("status", 404);
			response.put("errorMsg", "Order Details not found with orderId: " + orderRequestPayload.getOrderId());
			return response;
		}
		OrderDetails orderDetails = new OrderDetails();
		orderDetails = fetchedOrderDetails.get(0);

		// check if status is in In-Transit
		if (!orderDetails.getStatus().equals("In-Transit")) {
			response.put("status", 400);
			response.put("errorMsg",
					"Order Details status should be 'In-Transit' but found to be: " + orderDetails.getStatus());
			return response;
		}
		switch (orderRequestPayload.getStatus()) {
		case "delivered": {
			try {
				orderDetails.setStatus("delivered");
				orderDetails.setDeliveryDate(getCurrentDate.getCurrentDate());
				orderDetailsSFService.updateOrderStatus(orderDetails);
				response.put("status", 200);
				response.put("delivery", "Order Delivered");
				response.put("delivery-date", getCurrentDate.getCurrentDate());
			} catch (Exception e) {
				log.error(e.toString());
				response.put("status", 500);
				response.put("errorMsg", e.toString());
				return response;
			}

			break;
		}
		case "cancelled": {
			try {
				orderDetails.setStatus("cancelled");
				orderDetails.setDeliveryDate("Cancelled:" + getCurrentDate.getCurrentDate());
				
				// update the quantity in db
				boolean isQtyUpdated = updateProductQtyInLocalDB(orderDetails);
				
				if(!isQtyUpdated) {
					response.put("status", 500);
					response.put("errorMsg", "failed to update qty in local db pleasee check log!");
					return response;
					
				}

				orderDetailsSFService.updateOrderStatus(orderDetails);
				response.put("status", 200);
				response.put("delivery", "Order Cancelled");
				response.put("cancelled-date", getCurrentDate.getCurrentDate());

				
			} catch (Exception e) {
				log.error(e.toString());
				response.put("status", 500);
				response.put("errorMsg", e.toString());
				return response;
			}

			break;
		}

		}

		return response;
	}

	private boolean updateProductQtyInLocalDB(OrderDetails orderDetails) {

		for (ItemsFromCartDetails orderedItem : orderDetails.getItems().values()) {

			String location = orderedItem.getShippingWareHouseLocation();
			String productTitle = orderedItem.getProductTitle();
			Product product = new Product();

			// find particular product to update
			try {
				product = productMongoDBService.findParticularProduct(location, productTitle);
				if (product == null) {
					log.error("product:"+ productTitle+" not found in local db at location:"+ location);
					return false;
				}
				int updatedQty = orderedItem.getQuantity() + product.getQuantity();
				product.setQuantity(updatedQty);
				productMongoDBService.updateProduct(product, location, productTitle);
				log.info("Product updated back to local db upon order cancellation!");
				
			} catch (Exception e) {
				log.error(e.getMessage().replaceAll("^.*\"message\":\"(.*?)\".*$", "$1"));
				
				return false;
			}

			log.info("Product: " + productTitle + " qty updated in MongoDB: updated qty = " + orderedItem.getQuantity());

		}
		return true;
	}

}
