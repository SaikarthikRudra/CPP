package com.exavalu.customer.product.portal.service.queueservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import com.exavalu.customer.product.portal.entities.salesforce.CashbackCPP;
import com.exavalu.customer.product.portal.entities.salesforce.CustomerSF;
import com.exavalu.customer.product.portal.service.salesforceservice.CashbackCPPSFService;
import com.exavalu.customer.product.portal.service.salesforceservice.CustomerDetailsSFService;

import com.exavalu.customer.product.portal.entities.salesforce.ProductSF;
import com.exavalu.customer.product.portal.entities.salesforce.PromocodeRecordsCPP;
import com.exavalu.customer.product.portal.service.salesforceservice.ProductSalesforceService;
import com.exavalu.customer.product.portal.service.salesforceservice.PromocodeRecordsCPPSFService;

import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Component
public class QueueEventListener {
	private static final Logger log = LogManager.getLogger(QueueEventListener.class);

	@Autowired
	private QueueServiceEvent queueService;

	@Autowired
	private CustomerDetailsSFService customerDetailsSFService;

	@Autowired
	private ProductSalesforceService productSalesforceService;
	@Autowired
	private CashbackCPPSFService cashbackCPPSFService;
	@Autowired
	private PromocodeRecordsCPPSFService promocodeRecordsCPPSFService;

	@Async
	@EventListener
	public void handleQueueEvent(QueueEvent event) {

		Class<?> itemClass = event.getItemClass();

		if (CustomerSF.class.equals(itemClass)) {
			QueueItemWrapper<CustomerSF> queueItem;
			while ((queueItem = queueService.getFromQueue(CustomerSF.class)) != null) {
				CustomerSF queueCustomerData = queueItem.getItem();
				String operation = queueItem.getOperation();

				try {
					if ("create".equals(operation)) {
						customerDetailsSFService.createCustomer(queueCustomerData);
						// now add cashback wallet for new wallet creation
						CashbackCPP newcashbackwallet = mapNewCashbackCPP(queueCustomerData);
						PromocodeRecordsCPP newPromocodeRecord = mapNewPromocodeRecordCPP(queueCustomerData);
						try {
							cashbackCPPSFService.createCashbackWallet(newcashbackwallet);
							promocodeRecordsCPPSFService.createPromocodeRecordForNewCustomer(newPromocodeRecord);

						} catch (Exception e) {
							log.error("Error while creating cashbackWallet record in Salesforce: " + e.toString());
						}

					} else if ("update".equals(operation)) {
						customerDetailsSFService.updateCustomerDetailsSF(queueCustomerData);
					}
					log.info("Processed customer operation: " + operation + " for customer ID: "
							+ queueCustomerData.getCustomerId());
				} catch (Exception e) {
					log.error("Error while processing record in Salesforce: " + e.toString());
				}
			}
		}

		if (ProductSF.class.equals(itemClass)) {
			QueueItemWrapper<ProductSF> queueItem;
			while ((queueItem = queueService.getFromQueue(ProductSF.class)) != null) {
				ProductSF queueProductData = queueItem.getItem();
				String operation = queueItem.getOperation();

				try {
					if ("create".equals(operation)) {
						productSalesforceService.createProduct(queueProductData);
					} else if ("update".equals(operation)) {
						String location = queueProductData.getLocation();
						productSalesforceService.updateProduct(queueProductData, location);
					}
					log.info("Processed product operation: " + operation + " for product ID: "
							+ queueProductData.getProductTitle());
				} catch (Exception e) {
					log.error("Error while processing record in Salesforce: " + e.toString());
				}
			}
		}
	}

	private CashbackCPP mapNewCashbackCPP(CustomerSF customer) {
		CashbackCPP newCashbackwallet = new CashbackCPP();
		newCashbackwallet.setCustomerId(customer.getCustomerId());
		newCashbackwallet.setCustomerEmail(customer.getEmailId());
		return newCashbackwallet;
	}

	private PromocodeRecordsCPP mapNewPromocodeRecordCPP(CustomerSF customer) {
		PromocodeRecordsCPP promocodeRecord = new PromocodeRecordsCPP();
		promocodeRecord.setCustomerId(customer.getCustomerId());

		return promocodeRecord;
	}
}
