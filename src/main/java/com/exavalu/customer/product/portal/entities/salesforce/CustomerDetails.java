package com.exavalu.customer.product.portal.entities.salesforce;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

//unused class -----------only for reference will delete in future
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomerDetails {
	
	@JsonProperty("Name")
	private String Name;
	private String address__c;
	private String cardDetails__c;
	private String emailId__c;
	private String firstName__c;
	private String lastName__c;
	private String gender__c;
	private String location__c;
	private String phoneNumber__c;
	private String pincode__c;
	
	public String getName() {
		return Name;
	}
	public String getAddress__c() {
		return address__c;
	}
	public String getCardDetails__c() {
		return cardDetails__c;
	}
	public String getEmailId__c() {
		return emailId__c;
	}
	public String getFirstName__c() {
		return firstName__c;
	}
	public String getLastName__c() {
		return lastName__c;
	}
	public String getGender__c() {
		return gender__c;
	}
	public String getLocation__c() {
		return location__c;
	}
	public String getPhoneNumber__c() {
		return phoneNumber__c;
	}
	public String getPincode__c() {
		return pincode__c;
	}
	public void setName(String name) {
		this.Name = name;
	}
	public void setAddress__c(String address__c) {
		this.address__c = address__c;
	}
	public void setCardDetails__c(String cardDetails__c) {
		this.cardDetails__c = cardDetails__c;
	}
	public void setEmailId__c(String emailId__c) {
		this.emailId__c = emailId__c;
	}
	public void setFirstName__c(String firstName__c) {
		this.firstName__c = firstName__c;
	}
	public void setLastName__c(String lastName__c) {
		this.lastName__c = lastName__c;
	}
	public void setGender__c(String gender__c) {
		this.gender__c = gender__c;
	}
	public void setLocation__c(String location__c) {
		this.location__c = location__c;
	}
	public void setPhoneNumber__c(String phoneNumber__c) {
		this.phoneNumber__c = phoneNumber__c;
	}
	public void setPincode__c(String pincode__c) {
		this.pincode__c = pincode__c;
	}
	
	
}
