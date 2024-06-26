package com.exavalu.customer.product.portal.entities.salesforce.deserializer;

import java.io.IOException;

import com.exavalu.customer.product.portal.entities.salesforce.PromocodeCPP;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class PromocodeCPPDeserializer extends JsonDeserializer<PromocodeCPP> {

    @Override
    public PromocodeCPP deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        ObjectMapper mapper = (ObjectMapper) parser.getCodec();
        ObjectNode root = mapper.readTree(parser);
        PromocodeCPP promocode = new PromocodeCPP();

        // Manually map each field from the JSON response to the corresponding field in the CustomerSF object
        if (root.has("promoCode__c")) {
        	promocode.setPromoCode(root.get("promoCode__c").asText());
        }else if (root.has("promoCode")) {
        	promocode.setPromoCode(root.get("promoCode").asText());
        }
        
        if (root.has("discountType__c")) {
        	promocode.setDiscountType(root.get("discountType__c").asText());
        }else if (root.has("discountType")) {
        	promocode.setDiscountType(root.get("discountType").asText());
        }
        
        if (root.has("cashback__c")) {
        	promocode.setCashback(root.get("cashback__c").asText());
        }else if (root.has("cashback")) {
        	promocode.setCashback(root.get("cashback").asText());
        }
        
        if (root.has("status__c")) {
        	promocode.setStatus(root.get("status__c").asText());
        }else if (root.has("status")) {
        	promocode.setStatus(root.get("status").asText());
        }
        if (root.has("usedByCustomerId__c")) {
        	promocode.setUsedByCustomerId(root.get("usedByCustomerId__c").asText());
        }else if (root.has("usedByCustomerId")) {
        	promocode.setUsedByCustomerId(root.get("usedByCustomerId").asText());
        }
        if (root.has("Id")) {
        	promocode.setSalesforceId(root.get("Id").asText());
        }
        
        
        return promocode;
    }
    @Override
    public boolean isCachable() {
        return true;
    }
}
