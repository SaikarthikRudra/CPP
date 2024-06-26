package com.exavalu.customer.product.portal.entities.dto.orderFeedbackDetails;

import com.exavalu.customer.product.portal.entities.salesforce.deserializer.FeedbackSFDeserializer;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = FeedbackSFDeserializer.class)
public class FeedbackDetails {
	private String productTitle;
	private String display;
	private String camera;
	private String performance;
	private String overall;
	private String description;
	
	
	public FeedbackDetails() {
		super();
		// TODO Auto-generated constructor stub
	}
	public String getProductTitle() {
		return productTitle;
	}
	public void setProductTitle(String productTitle) {
		this.productTitle = productTitle;
	}
	public String getDisplay() {
		return display;
	}
	public void setDisplay(String display) {
		this.display = display;
	}
	public String getCamera() {
		return camera;
	}
	public void setCamera(String camera) {
		this.camera = camera;
	}
	public String getPerformance() {
		return performance;
	}
	public void setPerformance(String performance) {
		this.performance = performance;
	}
	public String getOverall() {
		return overall;
	}
	public void setOverall(String overall) {
		this.overall = overall;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public FeedbackDetails(String productTitle, String display, String camera, String performance, String overall,
			String description) {
		super();
		this.productTitle = productTitle;
		this.display = display;
		this.camera = camera;
		this.performance = performance;
		this.overall = overall;
		this.description = description;
	}
	@Override
	public String toString() {
		return "FeedbackDetails [productTitle=" + productTitle + ", display=" + display + ", camera=" + camera
				+ ", performance=" + performance + ", overall=" + overall + ", description=" + description + "]";
	}


}
