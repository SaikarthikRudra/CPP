package com.exavalu.customer.product.portal.entities.salesforce.deserializer;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.exavalu.customer.product.portal.entities.salesforce.EmailAuthentication;
import com.exavalu.customer.product.portal.entities.salesforce.PromocodeRecordsCPP;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class EmailAuthenticationSFDeserializer extends JsonDeserializer<EmailAuthentication> {
	
	@Override
	public EmailAuthentication deserialize(JsonParser parser, DeserializationContext context) throws IOException {
		ObjectMapper mapper = (ObjectMapper) parser.getCodec();
		ObjectNode root = mapper.readTree(parser);
		EmailAuthentication emailAuthentication = new EmailAuthentication();

		// Manually map each field from the JSON response to the corresponding field in
		// the EmailAuthentication object
		if (root.has("customerId__c")) {
			emailAuthentication.setCustomerId(root.get("customerId__c").asText());
		} else if (root.has("customerId")) {
			emailAuthentication.setCustomerId(root.get("customerId").asText());
		}
		if (root.has("customerEmail__c")) {
			emailAuthentication.setCustomerEmail(root.get("customerEmail__c").asText());
		} else if (root.has("customerEmail")) {
			emailAuthentication.setCustomerEmail(root.get("customerEmail").asText());
		}
		if (root.has("isEmailVerified__c")) {
			emailAuthentication.setEmailVerified(root.get("isEmailVerified__c").asBoolean());
		} else if (root.has("customerEmail")) {
			emailAuthentication.setEmailVerified(root.get("customerEmail").asBoolean());
		}
		
		if (root.has("Id")) {
			emailAuthentication.setSalesforceId(root.get("Id").asText());
		}

		return emailAuthentication;
	}

	@Override
	public boolean isCachable() {
		return true;
	}

}
