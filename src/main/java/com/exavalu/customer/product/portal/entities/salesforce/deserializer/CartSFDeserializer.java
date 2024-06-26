package com.exavalu.customer.product.portal.entities.salesforce.deserializer;

import com.exavalu.customer.product.portal.entities.salesforce.CartDetails;
import com.exavalu.customer.product.portal.entities.salesforce.ItemsFromCartDetails;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CartSFDeserializer extends JsonDeserializer<CartDetails> {

	@Override
	public CartDetails deserialize(JsonParser parser, DeserializationContext context)
			throws IOException, JsonProcessingException {
		ObjectMapper mapper = (ObjectMapper) parser.getCodec();
		ObjectNode root = mapper.readTree(parser);
		CartDetails cart = new CartDetails();

		// Manually map each field from the JSON response to the corresponding field in
		// the CartDetails object
		if (root.has("customerEmail__c")) {
			cart.setCustomerEmail(root.get("customerEmail__c").asText());
		} else if (root.has("customerEmail")) {
			cart.setCustomerEmail(root.get("customerEmail").asText());
		}

		if (root.has("customerId__c")) {
			cart.setCustomerId(root.get("customerId__c").asText());
		} else if (root.has("customerId")) {
			cart.setCustomerId(root.get("customerId").asText());
		}

		if (root.has("Items__c")) {
			String itemsJson = root.get("Items__c").asText();
			Map<String, ItemsFromCartDetails> itemsMap = parseItems(itemsJson, mapper);
			cart.setItems(itemsMap);
		} else if (root.has("items")) {
			String itemsJson = root.get("items").toString();
			Map<String, ItemsFromCartDetails> itemsMap = parseItems(itemsJson, mapper);
			cart.setItems(itemsMap);
		}

		if (root.has("Nearest_Db_Location__c")) {
			cart.setNearestDbLocation(root.get("Nearest_Db_Location__c").asText());
		} else if (root.has("nearestDbLocation")) {
			cart.setNearestDbLocation(root.get("nearestDbLocation").asText());
		}

		if (root.has("Status__c")) {
			cart.setStatus(root.get("Status__c").asText());
		} else if (root.has("status")) {
			cart.setStatus(root.get("status").asText());
		}

		if (root.has("Total_Amount__c")) {
			cart.setTotalAmount(root.get("Total_Amount__c").asInt());
		} else if (root.has("totalAmount")) {
			cart.setTotalAmount(root.get("totalAmount").asInt());
		}
		if (root.has("Id")) {
			cart.setSalesforceId(root.get("Id").asText());
		}

		return cart;
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

	@Override
	public boolean isCachable() {
		return true;
	}
}
