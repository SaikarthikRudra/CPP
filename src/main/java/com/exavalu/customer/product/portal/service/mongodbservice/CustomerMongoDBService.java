package com.exavalu.customer.product.portal.service.mongodbservice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.exavalu.customer.product.portal.entities.mongodb.CardDetails;
import com.exavalu.customer.product.portal.entities.mongodb.Customer;
import com.exavalu.customer.product.portal.entities.salesforce.CustomerSF;
import com.exavalu.customer.product.portal.service.queueservice.QueueItemWrapper;
import com.exavalu.customer.product.portal.service.queueservice.QueueServiceEvent;

@Service
public class CustomerMongoDBService {
	private static final Logger log = LogManager.getLogger(CustomerMongoDBService.class);

	@Autowired
	private QueueServiceEvent queueServiceEvent;

	@Autowired
	@Qualifier("MumbaiMongoTemplate")
	private MongoTemplate mumbaiMongoTemplate;

	@Autowired
	@Qualifier("KolkataMongoTemplate")
	private MongoTemplate kolkataMongoTemplate;

	@Autowired
	@Qualifier("HyderabadMongoTemplate")
	private MongoTemplate hyderabadMongoTemplate;

	@Autowired
	@Qualifier("BangaloreMongoTemplate")
	private MongoTemplate bangaloreMongoTemplate;

	public List<Customer> findAll(String location) {

		switch (location) {
		case "Mumbai":
			return mumbaiMongoTemplate.findAll(Customer.class);
		case "Kolkata":
			return kolkataMongoTemplate.findAll(Customer.class);
		case "Hyderabad":
			return hyderabadMongoTemplate.findAll(Customer.class);
		case "Bangalore":
			return bangaloreMongoTemplate.findAll(Customer.class);
		default:
			return null;
		}

	}

	public Customer findParticularCustomer(String location, String id, String usingCustomerIdOrEmailId) {

		Query query = new Query();
		switch (usingCustomerIdOrEmailId) {
		case "emailId": {
			query.addCriteria(Criteria.where("emailId").is(id));
			break;
		}
		case "customerId": {
			query.addCriteria(Criteria.where("CustomerId").is(id));
			break;
		}
		default:
			return null;
		}

		switch (location) {
		case "Mumbai":
			return mumbaiMongoTemplate.findOne(query, Customer.class);
		case "Kolkata":
			return kolkataMongoTemplate.findOne(query, Customer.class);
		case "Hyderabad":
			return hyderabadMongoTemplate.findOne(query, Customer.class);
		case "Bangalore":
			return bangaloreMongoTemplate.findOne(query, Customer.class);
		default:
			return null;
		}

	}

//create new customer in mongodb
	public Map<String, String> createNewCustomer(Customer customer, String location) {
		Map<String, String> response = new HashMap<>();
		switch (location) {
		case "Mumbai":
			mumbaiMongoTemplate.save(customer);
			break;
		case "Kolkata":
			kolkataMongoTemplate.save(customer);
			break;
		case "Hyderabad":
			hyderabadMongoTemplate.save(customer);
			break;
		case "Bangalore":
			bangaloreMongoTemplate.save(customer);
			break;
		default:
			response.put("msg", "invalid location!");
			break;
		}
		Customer newCustomer = findParticularCustomer(location, customer.getEmailId(), "emailId");
		if (newCustomer != null) {
			log.info("Customer created in mongodb");
			addToQueueMethod(newCustomer, "create");
			response.put("customerId", newCustomer.getCustomerId());
			response.put("emailId", newCustomer.getEmailId());
			response.put("status", "200");
		}
		return response;

	}

//update existing customer in mongodb
	public Map<String, String> updateCustomer(Customer customer, String location) {

		Map<String, String> response = new HashMap<>();
		Customer customerToUpdate = findParticularCustomer(location, customer.getEmailId(), "emailId");
		if (customerToUpdate == null) {
			response.put("httpStatus", "400");
			response.put("msg", "customer does not exist at this location: " + location);
			return response;
		}

		Query query = new Query(Criteria.where("emailId").is(customer.getEmailId()));
		Update update = new Update();

//		if (customer.getEmailId() != null)
//			update.set("emailId", customer.getEmailId());
		if (customer.getFirstName() != null)
			update.set("firstName", customer.getFirstName());
		if (customer.getLastName() != null)
			update.set("lastName", customer.getLastName());
		if (customer.getGender() != null)
			update.set("gender", customer.getGender());
		if (customer.getAddress() != null)
			update.set("address", customer.getAddress());
		if (customer.getPhoneNumber() != null)
			update.set("phoneNumber", customer.getPhoneNumber());
		if (customer.getPincode() != null)
			update.set("pincode", customer.getPincode());
		if (customer.getCardDetails() != null)
			update.set("cardDetails", customer.getCardDetails());
//		if (customer.getLocation() != null)
//			update.set("location", customer.getLocation());

		switch (location) {
		case "Mumbai":
			mumbaiMongoTemplate.updateFirst(query, update, Customer.class);
			break;
		case "Kolkata":
			kolkataMongoTemplate.updateFirst(query, update, Customer.class);
			break;
		case "Hyderabad":
			hyderabadMongoTemplate.updateFirst(query, update, Customer.class);
			break;
		case "Bangalore":
			bangaloreMongoTemplate.updateFirst(query, update, Customer.class);
			break;
		default:
			response.put("msg", "invalid location!");
			return response;
		}

		Customer updatedCustomer = findParticularCustomer(location, customer.getEmailId(), "emailId");
		if (updatedCustomer != null) {
			log.info("Customer updated in mongodb");
			addToQueueMethod(updatedCustomer, "update");
			response.put("msg", "Success! customer data updated");
			response.put("customerId", updatedCustomer.getCustomerId());
			response.put("emailId", updatedCustomer.getEmailId());
		}
		return response;
	}

	// Queue service add method with operation type
	@Async
	public void addToQueueMethod(Customer customer, String operation) {
		CustomerSF newCustomersf = mapNewCustomerSF(customer);
		queueServiceEvent.addToQueue(new QueueItemWrapper<>(newCustomersf, operation));
		log.info("Customer data added to queue! operation: " + operation);
	}

	private CustomerSF mapNewCustomerSF(Customer customer) {
		CustomerSF newCustomersf = new CustomerSF();
		newCustomersf.setCustomerId(customer.getCustomerId());
		newCustomersf.setAddress(customer.getAddress());

		Map<String, CardDetails> cardDetailsMap = customer.getCardDetails();
		String cardDetailsString = "{" + cardDetailsMap.entrySet().stream()
				.map(entry -> "\"" + entry.getKey() + "\":" + "{\"bankName\":\"" + entry.getValue().getBankName()
						+ "\",\"cardNumber\":\"" + entry.getValue().getCardNumber() + "\"}")
				.collect(Collectors.joining(", ")) + "}";
		newCustomersf.setCardDetails(cardDetailsString);

		newCustomersf.setEmailId(customer.getEmailId());
		newCustomersf.setFirstName(customer.getFirstName());
		newCustomersf.setLastName(customer.getLastName());
		newCustomersf.setGender(customer.getGender());
		newCustomersf.setLocation(customer.getLocation());
		newCustomersf.setPhoneNumber(customer.getPhoneNumber());
		newCustomersf.setPincode(customer.getPincode());
		return newCustomersf;
	}

}
