package com.exavalu.customer.product.portal.entities.salesforce.deserializer;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.exavalu.customer.product.portal.entities.salesforce.PromocodeRecordsCPP;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class PromocodeRecordsCPPDeserializer extends JsonDeserializer<PromocodeRecordsCPP> {

	@Override
	public PromocodeRecordsCPP deserialize(JsonParser parser, DeserializationContext context) throws IOException {
		ObjectMapper mapper = (ObjectMapper) parser.getCodec();
		ObjectNode root = mapper.readTree(parser);
		PromocodeRecordsCPP promocodeRecords = new PromocodeRecordsCPP();

		// Manually map each field from the JSON response to the corresponding field in
		// the CustomerSF object
		if (root.has("customerId__c")) {
			promocodeRecords.setCustomerId(root.get("customerId__c").asText());
		} else if (root.has("customerId")) {
			promocodeRecords.setCustomerId(root.get("customerId").asText());
		}
		if (root.has("usedPromocodes__c")) {

			String usedPromocodesAsString = root.get("usedPromocodes__c").asText();
			if (!usedPromocodesAsString.equals("null")) {
				List<String> usedPromocodeList = Arrays.asList(usedPromocodesAsString.split(","));
				promocodeRecords.setUsedPromocodes(usedPromocodeList);
			}

		}
		if (root.has("Id")) {
			promocodeRecords.setSalesforceId(root.get("Id").asText());
		}

		return promocodeRecords;
	}

	@Override
	public boolean isCachable() {
		return true;
	}
}
