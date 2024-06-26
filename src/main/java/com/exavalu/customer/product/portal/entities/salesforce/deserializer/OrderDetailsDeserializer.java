package com.exavalu.customer.product.portal.entities.salesforce.deserializer;
import com.exavalu.customer.product.portal.entities.salesforce.ItemsFromCartDetails;
import com.exavalu.customer.product.portal.entities.salesforce.OrderDetails;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class OrderDetailsDeserializer extends JsonDeserializer<OrderDetails> {

    @Override
    public OrderDetails deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        ObjectMapper mapper = (ObjectMapper) parser.getCodec();
        ObjectNode root = mapper.readTree(parser);
        OrderDetails order = new OrderDetails();

        // Manually map each field from the JSON response to the corresponding field in the OrderDetails object
        if (root.has("Name")) {
           order.setOrderId(root.get("Name").asText());
        }
        
        if (root.has("customerId__c")) {
        	order.setCustomerId(root.get("customerId__c").asText());
        }else if (root.has("customerId")) {
        	order.setCustomerId(root.get("customerId").asText());
        	System.out.println("customerId: "+ order.getCustomerId());
        }
        if (root.has("deliverd_Date__c")) {
        	order.setDeliveryDate(root.get("deliverd_Date__c").asText());
        }else if (root.has("deliveryDate")) {
        	order.setDeliveryDate(root.get("deliveryDate").asText());
        }
        
        if (root.has("feedback__c")) {
        	 order.setFeedback(root.get("feedback__c").asText());
        }else if (root.has("feedback")) {
        	order.setFeedback(root.get("feedback").asText());
        }
        
        if (root.has("order_date__c")) {
        	order.setOrderDate(root.get("order_date__c").asText());
        }else if (root.has("orderDate")) {
        	order.setOrderDate(root.get("orderDate").asText());
        }
        
        if (root.has("payment_Mode__c")) {
        	order.setPaymentMode(root.get("payment_Mode__c").asText());
        }else if (root.has("paymentMode")) {
        	order.setPaymentMode(root.get("paymentMode").asText());
        }
        
        if (root.has("status__c")) {
        	order.setStatus(root.get("status__c").asText());
        }else if (root.has("status")) {
        	order.setStatus(root.get("status").asText());
        }
        
        if (root.has("total_Price__c")) {
        	order.setTotalPrice(root.get("total_Price__c").asText());
        }else if (root.has("totalPrice")) {
        	order.setTotalPrice(root.get("totalPrice").asText());
        }
        if (root.has("Id")) {
        	order.setSalesforceId(root.get("Id").asText());
        }
        
        if (root.has("items__c")) {
			String itemsJson = root.get("items__c").asText();
			Map<String, ItemsFromCartDetails> itemsMap = parseItems(itemsJson, mapper);
			order.setItems(itemsMap);
		} else if (root.has("items")) {
			String itemsJson = root.get("items").toString();
			Map<String, ItemsFromCartDetails> itemsMap = parseItems(itemsJson, mapper);
			order.setItems(itemsMap);
		}
        
        return order;
    }
    @Override
    public boolean isCachable() {
        return true;
    }
    
    private Map<String, ItemsFromCartDetails> parseItems(String itemsJson, ObjectMapper mapper)
			throws JsonProcessingException {
		Map<String, ItemsFromCartDetails> itemsMap = new HashMap<>();
		// Parse the items JSON string into a Map
		Map<String, Object> itemsData = mapper.readValue(itemsJson, Map.class);

		// Iterate over the itemsData map and convert each item to ItemsFromCartDetails
		if (itemsData != null) {
			for (Map.Entry<String, Object> entry : itemsData.entrySet()) {
				String key = entry.getKey();
				Object value = entry.getValue();
				ItemsFromCartDetails item = mapper.convertValue(value, ItemsFromCartDetails.class);
				itemsMap.put(key, item);
			}
		}

		return itemsMap;
	}
}

