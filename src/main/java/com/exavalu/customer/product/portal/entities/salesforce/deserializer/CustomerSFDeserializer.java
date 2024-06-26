package com.exavalu.customer.product.portal.entities.salesforce.deserializer;
import com.exavalu.customer.product.portal.entities.salesforce.CustomerSF;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;

public class CustomerSFDeserializer extends JsonDeserializer<CustomerSF> {

    @Override
    public CustomerSF deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        ObjectMapper mapper = (ObjectMapper) parser.getCodec();
        ObjectNode root = mapper.readTree(parser);
        CustomerSF customer = new CustomerSF();

        // Manually map each field from the JSON response to the corresponding field in the CustomerSF object
        if (root.has("Name")) {
            customer.setCustomerId(root.get("Name").asText());
        }else if (root.has("customerId")) {
            customer.setCustomerId(root.get("customerId").asText());
        }
        
        if (root.has("address__c")) {
            customer.setAddress(root.get("address__c").asText());
        }else if (root.has("address")) {
            customer.setAddress(root.get("address").asText());
        }
        
        if (root.has("cardDetails__c")) {
            customer.setCardDetails(root.get("cardDetails__c").asText());
        }else if (root.has("cardDetails")) {
            customer.setCardDetails(root.get("cardDetails").asText());
        }
        
        if (root.has("emailId__c")) {
            customer.setEmailId(root.get("emailId__c").asText());
        }else if (root.has("emailId")) {
            customer.setEmailId(root.get("emailId").asText());
        }
        
        if (root.has("firstName__c")) {
            customer.setFirstName(root.get("firstName__c").asText());
        }else if (root.has("firstName")) {
            customer.setFirstName(root.get("firstName").asText());
        }
        
        if (root.has("lastName__c")) {
            customer.setLastName(root.get("lastName__c").asText());
        }else if (root.has("lastName")) {
            customer.setLastName(root.get("lastName").asText());
        }
        
        if (root.has("gender__c")) {
            customer.setGender(root.get("gender__c").asText());
        }else if (root.has("gender")) {
            customer.setGender(root.get("gender").asText());
        }
        
        if (root.has("location__c")) {
            customer.setLocation(root.get("location__c").asText());
        }else if (root.has("location")) {
            customer.setLocation(root.get("location").asText());
        }
        
        if (root.has("phoneNumber__c")) {
            customer.setPhoneNumber(root.get("phoneNumber__c").asText());
        }else if (root.has("phoneNumber")) {
            customer.setPhoneNumber(root.get("phoneNumber").asText());
        }
        
        if (root.has("pincode__c")) {
            customer.setPincode(root.get("pincode__c").asText());
        }else if (root.has("pincode")) {
            customer.setPincode(root.get("pincode").asText());
        }
        if (root.has("Id")) {
            customer.setSalesforceId(root.get("Id").asText());
        }

        return customer;
    }
    @Override
    public boolean isCachable() {
        return true;
    }
}

