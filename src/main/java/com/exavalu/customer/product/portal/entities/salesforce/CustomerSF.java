package com.exavalu.customer.product.portal.entities.salesforce;

import com.exavalu.customer.product.portal.entities.salesforce.deserializer.CustomerSFDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = CustomerSFDeserializer.class)
public class CustomerSF {

	private String customerId;
	private String address;
	private String cardDetails;
	private String emailId;
	private String firstName;
	private String lastName;
	private String gender;
	private String location;
	private String phoneNumber;
	private String pincode;
	private String salesforceId;

	public String getSalesforceId() {
		return salesforceId;
	}

	public void setSalesforceId(String salesforceId) {
		this.salesforceId = salesforceId;
	}

	public CustomerSF() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getCustomerId() {
		return customerId;
	}

	public String getAddress() {
		return address;
	}

	public String getCardDetails() {
		return cardDetails;
	}

	public String getEmailId() {
		return emailId;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getGender() {
		return gender;
	}

	public String getLocation() {
		return location;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public String getPincode() {
		return pincode;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public void setCardDetails(String cardDetails) {
		this.cardDetails = cardDetails;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public void setPincode(String pincode) {
		this.pincode = pincode;
	}

	public CustomerSF(String customerId, String address, String cardDetails, String emailId, String firstName,
			String lastName, String gender, String location, String phoneNumber, String pincode,String salesforceId) {
		super();
		this.customerId = customerId;
		this.address = address;
		this.cardDetails = cardDetails;
		this.emailId = emailId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.gender = gender;
		this.location = location;
		this.phoneNumber = phoneNumber;
		this.pincode = pincode;
		this.salesforceId = salesforceId;
	}

}
