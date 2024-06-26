package com.exavalu.customer.product.portal.entities.salesforce;

import com.exavalu.customer.product.portal.entities.salesforce.deserializer.EmailAuthenticationSFDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonDeserialize(using = EmailAuthenticationSFDeserializer.class)
public class EmailAuthentication {
	
	private String customerId;
	private String customerEmail;
	private boolean isEmailVerified;
	private String salesforceId;

}
