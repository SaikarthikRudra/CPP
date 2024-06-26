package com.exavalu.customer.product.portal.entities.salesforce.deserializer;

import java.io.IOException;

import com.exavalu.customer.product.portal.entities.salesforce.Enquiry;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class EnquiryDeserializer extends JsonDeserializer<Enquiry> {

    @Override
    public Enquiry deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        ObjectMapper mapper = (ObjectMapper) parser.getCodec();
        ObjectNode root = mapper.readTree(parser);
        Enquiry enquiry = new Enquiry();

        // Manually map each field from the JSON response to the corresponding field in the CustomerSF object
        if (root.has("emailId__c")) {
        	enquiry.setEmailId(root.get("emailId__c").asText());
        }else if (root.has("emailId")) {
        	enquiry.setEmailId(root.get("emailId").asText());
        }
        
        if (root.has("productTitle__c")) {
        	enquiry.setProductTitle(root.get("productTitle__c").asText());
        }else if (root.has("productTitle")) {
        	enquiry.setProductTitle(root.get("productTitle").asText());
        }
        
        if (root.has("quantity__c")) {
        	enquiry.setQuantity(root.get("quantity__c").asInt());
        }else if (root.has("quantity")) {
        	enquiry.setQuantity(root.get("quantity").asInt());
        }
        
        if (root.has("Id")) {
        	enquiry.setSalesforceId(root.get("Id").asText());
        }
        
        
        return enquiry;
    }
    @Override
    public boolean isCachable() {
        return true;
    }
}
