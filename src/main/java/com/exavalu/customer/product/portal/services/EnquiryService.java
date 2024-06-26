package com.exavalu.customer.product.portal.services;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.exavalu.customer.product.portal.entities.salesforce.Enquiry;
import com.exavalu.customer.product.portal.entities.salesforce.ProductSF;
import com.exavalu.customer.product.portal.reusable.service.EmailService;
import com.exavalu.customer.product.portal.service.salesforceservice.EnquirySFService;
import com.exavalu.customer.product.portal.service.salesforceservice.ProductSalesforceService;

@Service
public class EnquiryService {

	private static final Logger log = LogManager.getLogger(EnquiryService.class);

	@Autowired
	private EnquirySFService enquirySFService;

	@Autowired
	private ProductSalesforceService productSalesforceService;

	@Autowired
	private EmailService emailService;

	public void processReceivedEnquiry() {

		// fetch enquiry list
		List<Enquiry> enquiryList = enquirySFService.getAllEnquiry();

		for (Enquiry enquiry : enquiryList) {
			// Process enquiry in parallel
			sendMailForEnquiries(enquiry); // async process

		}

	}

	@Async
	public void sendMailForEnquiries(Enquiry enquiry) {

		String productTitle = enquiry.getProductTitle();
		String emailId = enquiry.getEmailId();
		int enquiredQuantity = enquiry.getQuantity();

		// check for product qty available or not
		List<ProductSF> productList = new ArrayList<>();
		try {
			productList = productSalesforceService.findParticularProduct(productTitle);
			if (productList != null && !productList.isEmpty()) {
				int stockQty = 0;
				for (ProductSF productsf : productList) {
					stockQty = stockQty + productsf.getQuantity();
				}

				// match for product quantity
				if (stockQty > enquiredQuantity) {

					// send mail for enquiry
					emailService.sendEnquiryMail(emailId, productTitle);
					// delete the enquiry after sending mail
					enquirySFService.deleteEnquiry(enquiry.getSalesforceId());

					log.info("Email sent to: " + emailId);

				} else {
					log.info("Insufficient quantity for product: " + productTitle);
				}
			} else {
				log.info("Product not found: " + productTitle);
			}
		} catch (Exception e) {
			log.error("Error processing enquiry for: " + emailId, e);
		}
	}

	// method to create new enquiry
	@Async
	public void createNewEnquiry(Enquiry enquiry) {

		// check for already existing enquiry with emailid and product title
		List<Enquiry> enquiryList = enquirySFService.getAllEnquiryWithMailId(enquiry.getEmailId());
		boolean isExistingEnquiryFound = false;
		if (enquiryList != null || !enquiryList.isEmpty()) {
			for (Enquiry enquiryVar : enquiryList) {
				if (enquiryVar.getProductTitle() == enquiry.getProductTitle()) {
					isExistingEnquiryFound = true;
					break;
				}
			}
		}
		if (isExistingEnquiryFound) {
			log.warn("Enquiry for ProductTitle: " + enquiry.getProductTitle() + "already there!");
			return;
			// no need to create new enquiry
		}
		boolean isEnquired = false;
		try {
			isEnquired = enquirySFService.createEnquiry(enquiry);
		} catch (Exception e) {
			log.error("Error creating enquiry for: " + enquiry.getEmailId(), e);

		}
		if (!isEnquired) {
			log.error("Enquiry not created! please check logs for more detail");
		} else {
			log.info("Enquiry created for Product: " + enquiry.getProductTitle());
		}
	}

	public void deleteEnquiry() {

	}

}
