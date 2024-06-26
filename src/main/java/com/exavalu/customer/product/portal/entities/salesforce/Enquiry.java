package com.exavalu.customer.product.portal.entities.salesforce;

import com.exavalu.customer.product.portal.entities.salesforce.deserializer.EnquiryDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonDeserialize(using = EnquiryDeserializer.class )
public class Enquiry {
	
	private String emailId;
	private String productTitle;
	private int quantity;
	private String salesforceId;

}
