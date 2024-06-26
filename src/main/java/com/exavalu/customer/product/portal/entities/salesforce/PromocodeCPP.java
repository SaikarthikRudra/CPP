package com.exavalu.customer.product.portal.entities.salesforce;

import com.exavalu.customer.product.portal.entities.salesforce.deserializer.PromocodeCPPDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = PromocodeCPPDeserializer.class)
public class PromocodeCPP {
	
	private String promoCode;
	private String cashback;
	private String discountType;
	private String status;
	private String usedByCustomerId;
	private String salesforceId;
	
	public String getPromoCode() {
		return promoCode;
	}
	public String getCashback() {
		return cashback;
	}
	public String getDiscountType() {
		return discountType;
	}
	public String getStatus() {
		return status;
	}
	public String getUsedByCustomerId() {
		return usedByCustomerId;
	}
	public void setPromoCode(String promoCode) {
		this.promoCode = promoCode;
	}
	public void setCashback(String cashback) {
		this.cashback = cashback;
	}
	public void setDiscountType(String discountType) {
		this.discountType = discountType;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public void setUsedByCustomerId(String usedByCustomerId) {
		this.usedByCustomerId = usedByCustomerId;
	}
	public PromocodeCPP(String promoCode, String cashback, String discountType, String status,
			String usedByCustomerId, String salesforceId) {
		super();
		this.promoCode = promoCode;
		this.cashback = cashback;
		this.discountType = discountType;
		this.status = status;
		this.usedByCustomerId = usedByCustomerId;
		this.salesforceId = salesforceId;
	}
	public PromocodeCPP() {
		super();
		
	}
	public String getSalesforceId() {
		return salesforceId;
	}
	public void setSalesforceId(String salesforceId) {
		this.salesforceId = salesforceId;
	}
	
	

}
