package com.exavalu.customer.product.portal.entities.salesforce.deserializer;

import java.io.IOException;

import com.exavalu.customer.product.portal.entities.salesforce.Complaint;
import com.exavalu.customer.product.portal.entities.salesforce.Complaint.ComplaintCategory;
//import com.exavalu.Complaint.product.portal.entities.salesforce.ComplaintSF;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ComplaintSFDeserializer extends JsonDeserializer<Complaint> {

	@Override
	public Complaint deserialize(JsonParser parser, DeserializationContext context) throws IOException {
		ObjectMapper mapper = (ObjectMapper) parser.getCodec();
		ObjectNode root = mapper.readTree(parser);
		Complaint complaint = new Complaint();

		// Manually map each field from the JSON response to the corresponding field in
		// the product object
		System.out.println(root);
		if (root.has("Name")) {
			complaint.setName(root.get("Name").asText());
		}
		if (root.has("action__c")) {
			complaint.setAction(root.get("action__c").asText());
		} else if (root.has("action")) {
			complaint.setAction(root.get("action").asText());
		}
		if (root.has("complaint_category__c")) {
			complaint.setComplaint_category(root.get("complaint_category__c").asText());
		} else if (root.has("complaint_category")) {
			complaint.setComplaint_category(root.get("complaint_category").asText());
		}
//		if (root.has("complaint_date__c")) {
//			complaint.setComplaint_date(root.get("complaint_date__c").asText());
//		}
//		else if(root.has("complaint_date")) {
//			complaint.setComplaint_date(root.get("complaint_date").asText());
//		}
		if (root.has("customerId__c")) {
			complaint.setCustomerId(root.get("customerId__c").asText());
		} else if (root.has("customerId")) {
			complaint.setCustomerId(root.get("customerId").asText());
		}
		if (root.has("orderId__c")) {
			complaint.setOrderId(root.get("orderId__c").asText());
		} else if (root.has("orderId")) {
			complaint.setOrderId(root.get("orderId").asText());
		}
		if (root.has("productTitle__c")) {
			complaint.setProductTitle(root.get("productTitle__c").asText());
		} else if (root.has("productTitle")) {
			complaint.setProductTitle(root.get("productTitle").asText());
		}
		if (root.has("quantity__c")) {
			complaint.setQuantity(root.get("quantity__c").asInt());
		} else if (root.has("quantity")) {
			complaint.setQuantity(root.get("quantity").asInt());
		}
		if (root.has("date_of_complaint__c")) {
			complaint.setDate_of_complaint(root.get("date_of_complaint__c").asText());
		} else if (root.has("date_of_complaint")) {
			complaint.setDate_of_complaint(root.get("date_of_complaint").asText());
		}
		if (root.has("description__c")) {
			complaint.setDescription(root.get("description__c").asText());
		} else if (root.has("description")) {
			complaint.setDescription(root.get("description").asText());
		}
		if (root.has("status__c")) {
			complaint.setStatus(root.get("status__c").asText());
		} else if (root.has("status")) {
			complaint.setStatus(root.get("status").asText());
		}
		if (root.has("reason__c")) {
			complaint.setReason(root.get("reason__c").asText());
		} else if (root.has("reason")) {
			complaint.setReason(root.get("reason").asText());
		}

		return complaint;
	}

}
