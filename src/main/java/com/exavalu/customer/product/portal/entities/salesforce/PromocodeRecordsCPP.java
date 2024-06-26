package com.exavalu.customer.product.portal.entities.salesforce;

import java.util.List;

import com.exavalu.customer.product.portal.entities.salesforce.deserializer.PromocodeRecordsCPPDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = PromocodeRecordsCPPDeserializer.class)
public class PromocodeRecordsCPP {
	
	private String customerId;
	private List<String> usedPromocodes;
	private String salesforceId;
	
	
	public String getCustomerId() {
		return customerId;
	}
	public List<String> getUsedPromocodes() {
		return usedPromocodes;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	public void setUsedPromocodes(List<String> usedPromocodes) {
		this.usedPromocodes = usedPromocodes;
	}
	public PromocodeRecordsCPP(String customerId, List<String> usedPromocodes,String salesforceId) {
		super();
		this.customerId = customerId;
		this.usedPromocodes = usedPromocodes;
		this.salesforceId = salesforceId;
	}
	public PromocodeRecordsCPP() {
		super();
	}
	public String getSalesforceId() {
		return salesforceId;
	}
	public void setSalesforceId(String salesforceId) {
		this.salesforceId = salesforceId;
	}
	

}
