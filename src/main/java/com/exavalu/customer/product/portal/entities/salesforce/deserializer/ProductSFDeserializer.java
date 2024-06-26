package com.exavalu.customer.product.portal.entities.salesforce.deserializer;

import java.io.IOException;

import com.exavalu.customer.product.portal.entities.salesforce.ProductSF;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ProductSFDeserializer extends JsonDeserializer<ProductSF> {
	@Override
	public ProductSF deserialize(JsonParser parser, DeserializationContext context) throws IOException {
		ObjectMapper mapper = (ObjectMapper) parser.getCodec();
		ObjectNode root = mapper.readTree(parser);
		ProductSF product = new ProductSF();

		// Manually map each field from the JSON response to the corresponding field in
		// the product object
		if (root.has("productTitle__c")) {
			product.setProductTitle(root.get("productTitle__c").asText());
		} else if (root.has("productTitle")) {
			product.setProductTitle(root.get("productTitle").asText());
		}
		if (root.has("brandName__c")) {
			product.setBrandName(root.get("brandName__c").asText());
		} else if (root.has("brandName")) {
			product.setBrandName(root.get("brandName").asText());
		}
		if (root.has("seriesName__c")) {
			product.setSeriesName(root.get("seriesName__c").asText());
		} else if (root.has("seriesName")) {
			product.setSeriesName(root.get("seriesName").asText());
		}
		if (root.has("price__c")) {
			product.setPrice(root.get("price__c").asInt());
		} else if (root.has("price")) {
			product.setPrice(root.get("price").asInt());
		}
		if (root.has("ram__c")) {
			product.setRam(root.get("ram__c").asText());
		}
		if (root.has("rom__c")) {
			product.setRom(root.get("rom__c").asText());
		} else if (root.has("rom")) {
			product.setRom(root.get("rom").asText());
		}
		if (root.has("warranty__c")) {
			product.setWarranty(root.get("warranty__c").asText());
		} else if (root.has("warranty")) {
			product.setWarranty(root.get("warranty").asText());
		}
		if (root.has("quantity__c")) {
			product.setQuantity(root.get("quantity__c").asInt());
		} else if (root.has("quantity")) {
			product.setQuantity(root.get("quantity").asInt());
		}
		if (root.has("location__c")) {
			product.setLocation(root.get("location__c").asText());
		} else if (root.has("location")) {
			product.setLocation(root.get("location").asText());
		}
		if (root.has("compositeId__c")) {
			product.setCompositeId(root.get("compositeId__c").asText());
		}
		if (root.has("Id")) {
			product.setSalesforceId(root.get("Id").asText());
		}

		return product;
	}

	@Override
	public boolean isCachable() {
		return true;
	}
}
