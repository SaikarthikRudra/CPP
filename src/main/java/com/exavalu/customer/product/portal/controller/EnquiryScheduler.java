package com.exavalu.customer.product.portal.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import com.exavalu.customer.product.portal.services.EnquiryService;

import jakarta.annotation.PostConstruct;

public class EnquiryScheduler {

	private static final Logger log = LogManager.getLogger(EnquiryScheduler.class);

	@Autowired
	private EnquiryService enquiryService;
	
	//The onStartup method runs immediately after the application starts.
	
//	@PostConstruct
//    public void onStartup() {
//        log.info("Running initial enquiry processing task on application startup");
//        enquiryService.processReceivedEnquiry();
//        log.info("Completed initial enquiry processing task on application startup");
//    }

	@Scheduled(cron = "0 0 2 * * ?") // run everyday at 2 am
	public void dailyEnquiryProcessingTask() {
		
		log.info("Starting daily scheduled task to process enquiries at 2 AM");

		enquiryService.processReceivedEnquiry();

		log.info("Completed daily scheduled task to process enquiries");
	}
}
