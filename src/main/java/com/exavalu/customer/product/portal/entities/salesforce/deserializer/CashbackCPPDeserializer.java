package com.exavalu.customer.product.portal.entities.salesforce.deserializer;

import com.exavalu.customer.product.portal.entities.salesforce.CashbackCPP;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;

public class CashbackCPPDeserializer extends JsonDeserializer<CashbackCPP> {

    @Override
    public CashbackCPP deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        ObjectMapper mapper = (ObjectMapper) parser.getCodec();
        ObjectNode root = mapper.readTree(parser);
        CashbackCPP cashback = new CashbackCPP();

        // Manually map each field from the JSON response to the corresponding field in the CustomerSF object
        if (root.has("Name")) {
        	cashback.setCustomerId(root.get("Name").asText());
        }else if (root.has("customerId")) {
        	cashback.setCustomerId(root.get("customerId").asText());
        }
        
        if (root.has("customerEmail__c")) {
        	cashback.setCustomerEmail(root.get("customerEmail__c").asText());
        }else if (root.has("customerEmail")) {
        	cashback.setCustomerEmail(root.get("customerEmail").asText());
        }
        if (root.has("cashbackWallet__c")) {
        	cashback.setCashbackWallet(root.get("cashbackWallet__c").asInt());
        }else if (root.has("cashbackWallet")) {
        	cashback.setCashbackWallet(root.get("cashbackWallet").asInt());
        }
        if (root.has("Id")) {
        	cashback.setSalesforceId(root.get("Id").asText());
        }
        if (root.has("cashbackWalletPending__c")) {
        	cashback.setCashbackWallet(root.get("cashbackWalletPending__c").asInt());
        }else if (root.has("cashbackWalletPending")) {
        	cashback.setCashbackWallet(root.get("cashbackWalletPending").asInt());
        }
        
        return cashback;
    }
    @Override
    public boolean isCachable() {
        return true;
    }
}
