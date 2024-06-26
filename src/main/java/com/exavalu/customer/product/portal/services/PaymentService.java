package com.exavalu.customer.product.portal.services;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import com.exavalu.customer.product.portal.entities.dto.payment.PaymentRequestPayload;
import com.exavalu.customer.product.portal.entities.mongodb.CardDetails;
import com.exavalu.customer.product.portal.entities.mongodb.Customer;
import com.exavalu.customer.product.portal.entities.mongodb.Product;
import com.exavalu.customer.product.portal.entities.salesforce.CartDetails;
import com.exavalu.customer.product.portal.entities.salesforce.CashbackCPP;
import com.exavalu.customer.product.portal.entities.salesforce.CustomerSF;
import com.exavalu.customer.product.portal.entities.salesforce.ItemsFromCartDetails;
import com.exavalu.customer.product.portal.entities.salesforce.OrderDetails;
import com.exavalu.customer.product.portal.reusable.service.CardValidatorService;
import com.exavalu.customer.product.portal.reusable.service.EmailService;
import com.exavalu.customer.product.portal.reusable.service.EncryptionService;
import com.exavalu.customer.product.portal.reusable.service.UPIValidatorService;
import com.exavalu.customer.product.portal.service.mongodbservice.CustomerMongoDBService;
import com.exavalu.customer.product.portal.service.mongodbservice.ProductMongoDBService;
import com.exavalu.customer.product.portal.service.salesforceservice.CartDetailsSFService;
import com.exavalu.customer.product.portal.service.salesforceservice.CashbackCPPSFService;
import com.exavalu.customer.product.portal.service.salesforceservice.CustomerDetailsSFService;
import com.exavalu.customer.product.portal.service.salesforceservice.OrderDetailsSFService;
import com.exavalu.customer.product.portal.services.utility.PdfGenerator;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class PaymentService {
	private static final Logger log = LogManager.getLogger(PaymentService.class);

	@Autowired
	private CustomerMongoDBService customerMongoDBService;
	@Autowired
	private CustomerDetailsSFService customerDetailsSFService;
	@Autowired
	private CartDetailsSFService cartDetailsSFService;
	@Autowired
	private EncryptionService encryptionService;
	@Autowired
	private CardValidatorService cardValidatorService;
	@Autowired
	private UPIValidatorService upiValidatorService;
	@Autowired
	private OrderDetailsSFService orderDetailsSFService;
	@Autowired
	private ProductMongoDBService productMongoDBService;
	@Autowired
	private CashbackCPPSFService cashbackCPPSFService;
	@Autowired
	private PdfGenerator pdfGenerator;
	@Autowired
	private EmailService emailService;
	@Autowired
	private EmailAuthenticationService emailAuthenticationService;

	public Map<String, Object> processPayment(PaymentRequestPayload paymentRequestPayload) {

		Map<String, Object> response = new HashMap<>();

		// check if emailId is verified or not
		if (paymentRequestPayload.getCustomerDetails().getEmailId() != null) {
			String emailToCheck = paymentRequestPayload.getCustomerDetails().getEmailId();
			boolean isEmailVerified = emailAuthenticationService.checkIfEmailIsAlreadyVerifiedorNot(emailToCheck);
			if (!isEmailVerified) {
				response.put("msg", "Email is not verified,Please verify your email first!");
				return response;
			}
		}
		List<CustomerSF> fetchedCustomerList = new ArrayList<CustomerSF>();
		try {
			fetchedCustomerList = customerDetailsSFService
					.getParticularCustomer(paymentRequestPayload.getCustomerDetails().getCustomerId(), "customerId");

		} catch (Exception e) {
			log.error(e.getMessage().replaceAll("^.*\"message\":\"(.*?)\".*$", "$1"));
			response.put("status", 500);
			response.put("errorMsg",
					"Internal Server Error: " + e.getMessage().replaceAll("^.*\"message\":\"(.*?)\".*$", "$1"));
			return response;

		}
		if (fetchedCustomerList.isEmpty()) {
			return response;
		}

		CustomerSF fetchedCustomerSF = new CustomerSF();
		fetchedCustomerSF = fetchedCustomerList.get(0);
		// check if emailId is verified or not--------------------------------------------------------
		if (fetchedCustomerSF.getCustomerId() != null) {
			String emailToCheck = fetchedCustomerSF.getEmailId();
			boolean isEmailVerified = emailAuthenticationService.checkIfEmailIsAlreadyVerifiedorNot(emailToCheck);
			if (!isEmailVerified) {
				response.put("msg", "Email is not verified,Please verify your email first!");
				return response;
			}
		}
		// check if emailId is verified or not end----------------------------------------------------
		PaymentRequestPayload.Payment paymentDetails = paymentRequestPayload.getPaymentDetails();
		int amountProvidedForDebit = paymentDetails.getTotalAmount();
		// first check payment details
		// validity---------------------------------------------------------
		switch (paymentDetails.getPaymentMode()) {
		case "card": {
			if (paymentDetails.getCardId() == 0) {
				String decryptedCardNumber = encryptionService.decrypt(paymentDetails.getCardNumber());
				String decryptedCVV = encryptionService.decrypt(paymentDetails.getCvv());
				boolean isCardNumberValid = cardValidatorService.isValidCardNumber(decryptedCardNumber);
				boolean isCVVValid = cardValidatorService.isValidCVV(decryptedCVV);
				decryptedCardNumber = null;
				decryptedCVV = null;

				if (!isCardNumberValid || !isCVVValid) {
					response.put("status", 400);
					response.put("errorMsg", "Either Card Number or CVV is invalid!");
					log.warn("Either Card Number or CVV is invalid!");
					return response;
				}

			}

			break;
		}
		case "UPI": {
			boolean isUPIIdValid = upiValidatorService.validateUpiId(paymentDetails.getUpiId());
			if (!isUPIIdValid) {
				response.put("status", 400);
				response.put("errorMsg", "UPI Id is not valid!");
				log.warn("UPI Id is not valid!");
				return response;
			}

			break;
		}
		}

		// check for cashback points available
		CashbackCPP cashbackCPP = new CashbackCPP();
		int cashbackAmount = 0;
		if (paymentDetails.isUseCashback()) {
			if (paymentRequestPayload.getCustomerDetails().getCustomerId() != null) {
				List<CashbackCPP> cashbackWalletList = cashbackCPPSFService.getParticularCashbackWallet(
						paymentRequestPayload.getCustomerDetails().getCustomerId(), "customerId");
				if (cashbackWalletList.isEmpty()) {
					response.put("status", 404);
					response.put("errorMsg", "cashback wallet not found for the customer:"
							+ paymentRequestPayload.getCustomerDetails().getCustomerId());
					return response;
				}
				cashbackCPP = cashbackWalletList.get(0);
				cashbackAmount = cashbackCPP.getCashbackWallet();
				log.info("cashbackAmount: " + cashbackAmount);
				int totalAmount = paymentDetails.getTotalAmount() + cashbackAmount;
				log.info(totalAmount);
				paymentDetails.setTotalAmount(totalAmount);

			}
		}

		// check for guest user

		if (paymentRequestPayload.getCustomerDetails().getCustomerId() == null) {

			Customer tempcustomerFromLocalDb;
			tempcustomerFromLocalDb = customerMongoDBService.findParticularCustomer(
					paymentRequestPayload.getCustomerDetails().getLocation(),
					(paymentRequestPayload.getCustomerDetails().getEmailId()), "emailId");
			if (tempcustomerFromLocalDb != null) {
				response.put("status", 400);
				response.put("errorMsg", "emailId already exist! please use you customerId for payment.");
				return response;
			}

			response = createNewCustomerUsingGuest(paymentRequestPayload);
			String status = response.getOrDefault("status", "400").toString();

			if (status != "200") {

				return response;
			}
			if (response.containsKey("customerId")) {
				String customerId = response.get("customerId").toString();
				paymentRequestPayload.getCustomerDetails().setCustomerId(customerId);
			}

		}

//		List<CustomerSF> fetchedCustomerList = new ArrayList<CustomerSF>();
//		try {
//			fetchedCustomerList = customerDetailsSFService
//					.getParticularCustomer(paymentRequestPayload.getCustomerDetails().getCustomerId(), "customerId");
//
//		} catch (Exception e) {
//			log.error(e.getMessage().replaceAll("^.*\"message\":\"(.*?)\".*$", "$1"));
//			response.put("status", 500);
//			response.put("errorMsg",
//					"Internal Server Error: " + e.getMessage().replaceAll("^.*\"message\":\"(.*?)\".*$", "$1"));
//			return response;
//
//		}
//		if (fetchedCustomerList.isEmpty()) {
//			return response;
//		}
//
//		CustomerSF fetchedCustomerSF = new CustomerSF();
//		fetchedCustomerSF = fetchedCustomerList.get(0);

		Customer customerFromLocalDb = new Customer();

		try {
			customerFromLocalDb = customerMongoDBService.findParticularCustomer(fetchedCustomerSF.getLocation(),
					fetchedCustomerSF.getCustomerId(), "customerId");
			if (customerFromLocalDb == null) {
				response.put("status", 400);
				response.put("errorMsg", "Either Customer does not exist at this location or CustomerId is invalid.");
				return response;
			}
			// validate cardId---------------------------------------------------
			if (paymentDetails.getCardId() != 0) {
				String cardId = String.valueOf(paymentDetails.getCardId());
				if (customerFromLocalDb.getCardDetails().containsKey(cardId)) {
					String decryptedCVV = encryptionService.decrypt(paymentDetails.getCvv());
					boolean isCVVValid = cardValidatorService.isValidCVV(decryptedCVV);
					decryptedCVV = null;

					if (!isCVVValid) {
						response.put("status", 400);
						response.put("errorMsg", "CVV is invalid!");
						log.warn("CVV is invalid!");
						return response;
					}

				}
			}

		} catch (Exception e) {

			response.put("status", 500);
			response.put("errorMsg", "unable to fetch customer from Local DB: "
					+ e.getMessage().replaceAll("^.*\"message\":\"(.*?)\".*$", "$1"));
			log.error(e.getMessage().replaceAll("^.*\"message\":\"(.*?)\".*$", "$1"));
			return response;
		}

		List<CartDetails> fetchedCartDetails = new ArrayList<CartDetails>();
		try {
			fetchedCartDetails = cartDetailsSFService.getParticularCartDetail(fetchedCustomerSF.getEmailId(),
					"emailId");
		} catch (Exception e) {
			fetchedCartDetails = null;
			response.put("status", 500);
			response.put("errorMsg",
					"unable to fetch cartDetails: " + e.getMessage().replaceAll("^.*\"message\":\"(.*?)\".*$", "$1"));
			log.error(e.getMessage().replaceAll("^.*\"message\":\"(.*?)\".*$", "$1"));
			return response;
		}
		if (fetchedCartDetails.isEmpty()) {
			response.put("status", 400);
			response.put("errorMsg", "unable to fetch cartDetails with emailId " + fetchedCustomerSF.getEmailId());

			return response;
		}
		CartDetails fetchedCart = fetchedCartDetails.get(0);
		if (fetchedCart.getItems() == null || fetchedCart.getTotalAmount() == 0) {
			response.put("status", 200);
			response.put("errorMsg", "your cart is  empty!");
			return response;

		}

		// check total cart amount and payment amount matches or not

		if (fetchedCart.getTotalAmount() != paymentRequestPayload.getPaymentDetails().getTotalAmount()) {
			response.put("errorMsg",
					"Please provide Total Amount as Total Cart Amount or if using cashback then adjust the point based on the points available in wallet to total cart amount! (1 point = Re 1)");
			response.put("status", 400);
			response.put("payment", "failed!");
			return response;
		}

		// update qty in local db
		response = updateQtyInLocalDB(fetchedCart, response);
		if (!response.get("status").equals(200)) {
			return response;
		}

// now prepare object for orderDetails------------------------------------------------------------------
		OrderDetails newOrder = new OrderDetails();

		newOrder.setCustomerId(fetchedCustomerSF.getCustomerId());
		newOrder.setDeliveryDate("Yet to delivered!"); // delivered date will be update by /orderDelivered end point
		newOrder.setFeedback("");
		newOrder.setOrderDate(getCurrentDate());
		newOrder.setPaymentMode(paymentDetails.getPaymentMode());
		newOrder.setStatus("In-Transit");
		newOrder.setTotalPrice(Integer.toString(fetchedCart.getTotalAmount()));

		// Prepare items for the new order
		Map<String, ItemsFromCartDetails> items = new HashMap<>();
		for (ItemsFromCartDetails cartItem : fetchedCart.getItems().values()) {
			ItemsFromCartDetails itemDetails = new ItemsFromCartDetails();
			itemDetails.setProductTitle(cartItem.getProductTitle());
			itemDetails.setWarranty(cartItem.getWarranty());
			itemDetails.setQuantity(cartItem.getQuantity());
			itemDetails.setPrice(cartItem.getPrice());
			itemDetails.setDeliveryCharge(cartItem.getDeliveryCharge());
			itemDetails.setTotalPrice(cartItem.getTotalPrice());
			itemDetails.setShippingWareHouseLocation(cartItem.getShippingWareHouseLocation());
			itemDetails.setAddToCartDate(cartItem.getAddToCartDate());
			itemDetails.setExpectedDelivery(cartItem.getExpectedDelivery());
			itemDetails.setNearestDbLocation(cartItem.getNearestDbLocation());

			items.put(String.valueOf(items.size() + 1), itemDetails);
		}

		newOrder.setItems(items);
		boolean isOrderCreated = false;
		Map<String, Object> responseOfRecordcreation = new HashMap<>();
		try {
			responseOfRecordcreation = orderDetailsSFService.createOrder(newOrder);
			if (responseOfRecordcreation.containsKey("salesforceId")) {
				log.info("orderCreated:SalesforceId->"
						+ responseOfRecordcreation.getOrDefault("salesforceId", "").toString());
				isOrderCreated = true;
			}
		} catch (Exception e) {
			response.put("status", 500);
			response.put("errorMsg", e.getMessage().replaceAll("^.*\"message\":\"(.*?)\".*$", "$1"));
			log.error(response.get("errorMsg").toString());
			return response;
		}
		// remove item from cart
		if (isOrderCreated) {

			try {
				cartDetailsSFService.resetCartitemsAndTotalAmountSF(fetchedCart.getCustomerEmail());

				// if cashback is used set cashbackwallet to 0
				if (cashbackAmount != 0) {
					cashbackCPP.setCashbackWallet(0);
					cashbackCPPSFService.updateCashbackWalletActive(cashbackCPP);
				}

			} catch (Exception e) {
				// delete created order
				try {
					String salesforceIdOfRecordToDelete = responseOfRecordcreation.getOrDefault("salesforceId", "")
							.toString();
					orderDetailsSFService.deleteOrder(salesforceIdOfRecordToDelete);
					// reset product qty in local db
					resetQtyInLocalDB(fetchedCart, response);
					// reset cashbackpoint
					cashbackCPP.setCashbackWallet(cashbackAmount);
					cashbackCPPSFService.updateCashbackWalletActive(cashbackCPP);

				} catch (Exception e1) {
					log.error(e1.toString());
				}
				response.put("status", 500);
				response.put("errorMsg", e.getMessage().replaceAll("^.*\"message\":\"(.*?)\".*$", "$1"));
				log.error(e.toString());
				return response;

			}
		}
		// generate response on successfull payment----------------------------
		String orderId = "";
		if (responseOfRecordcreation.containsKey("salesforceId")) {

			String salesforceId = responseOfRecordcreation.get("salesforceId").toString();
			List<OrderDetails> orderDetailsList = orderDetailsSFService.getParticularOrder(salesforceId,
					"salesforceId");
			log.info("orderDetailsList:" + orderDetailsList.toString());
			OrderDetails orderDetailsForInvoice = new OrderDetails();
			if (!orderDetailsList.isEmpty()) {
				orderId = orderDetailsList.get(0).getOrderId();
				orderDetailsForInvoice = orderDetailsList.get(0);
				// generate pdf and send to mail
				boolean isInvoiceGeneratedAndSendToMail = false;
				try {
					isInvoiceGeneratedAndSendToMail = generateInvoice(orderDetailsForInvoice,fetchedCustomerSF.getEmailId());
					if (isInvoiceGeneratedAndSendToMail) {
						response.put("Invoice", "Invoice has been sent to emailId:" + fetchedCustomerSF.getEmailId());
					}
				} catch (Exception e) {
					log.error(e.toString());
				}
			}

		}

		response.put("status", 200);
		response.put("paymentDate", getCurrentDate());
		if ("cash".equals(paymentDetails.getPaymentMode())) {
			response.put("payment", "Cash on delivery!");
			response.remove("paymentDate");
		} else {
			response.put("payment",
					"Congratulation payment through:" + paymentDetails.getPaymentMode() + " is successfull!");
		}

		response.put("msg", "you can check orderDtails using your customerId or orderId: " + orderId);
		return response;

	}

	private Map<String, Object> createNewCustomerUsingGuest(PaymentRequestPayload paymentRequestPayload) {

		Map<String, String> response = new HashMap<>();
		Map<String, Object> returnResponse = new HashMap<>();
		String location = paymentRequestPayload.getCustomerDetails().getLocation();
		if (location == null) {
			return null;
		}
		Customer customer = paymentRequestPayload.getCustomerDetails();
		PaymentRequestPayload.Payment paymentDetails = paymentRequestPayload.getPaymentDetails();
		// Create a new CardDetails object and set its properties
		CardDetails newCardDetail = new CardDetails();
		newCardDetail.setBankName(paymentDetails.getBankName());
		newCardDetail.setCardNumber(paymentDetails.getCardNumber());

		// Create a new map to hold card details and add the new card to it
		Map<String, CardDetails> cardDetails = new HashMap<>();
		cardDetails.put("1", newCardDetail); // Use a unique key for the card

		// Set the cardDetails map in the customer object
		customer.setCardDetails(cardDetails);

		response = customerMongoDBService.createNewCustomer(customer, location);
		try {
			Thread.sleep(1000); // sleep for 1 sec to get synchronised with salesforce db
		} catch (InterruptedException e) {
			log.error(e.toString());
		}
		returnResponse.putAll(response);
		return returnResponse;
	}

	private String getCurrentDate() {
		LocalDate currentDate = LocalDate.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		return currentDate.format(formatter);
	}

	private String getDeliveryDate(int days) {
		LocalDate currentDate = LocalDate.now();
		LocalDate deliveryDate = currentDate.plusDays(days);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		return deliveryDate.format(formatter);
	}

	private Map<String, Object> updateQtyInLocalDB(CartDetails fetchedCart, Map<String, Object> response) {
		boolean allItemsAvailable = true;
		List<String> insufficientItems = new ArrayList<>();

		// First pass: Check availability of all items
		for (ItemsFromCartDetails cartItem : fetchedCart.getItems().values()) {
			if (cartItem == null) {
				log.warn("cartItem is null while checking availability in local product database!");
				continue;
			}

			String location = cartItem.getShippingWareHouseLocation();
			String productTitle = cartItem.getProductTitle();
			Product product;

			try {
				product = productMongoDBService.findParticularProduct(location, productTitle);
			} catch (Exception e) {
				log.error(e.getMessage().replaceAll("^.*\"message\":\"(.*?)\".*$", "$1"));
				allItemsAvailable = false;
				continue;
			}

			if (product == null || product.getQuantity() < cartItem.getQuantity()) {
				allItemsAvailable = false;
				insufficientItems.add(productTitle);
			}
		}

		// If any item is not available in sufficient quantity, return error response
		if (!allItemsAvailable) {
			response.put("status", 400);
			response.put("errorMsg",
					"Insufficient quantity for the following items: " + String.join(", ", insufficientItems));
			response.put("payment", "failed");
			return response;
		}

		// Second pass: Update quantities for all items
		for (ItemsFromCartDetails cartItem : fetchedCart.getItems().values()) {
			if (cartItem == null) {
				continue;
			}

			String location = cartItem.getShippingWareHouseLocation();
			String productTitle = cartItem.getProductTitle();
			Product product;

			try {
				product = productMongoDBService.findParticularProduct(location, productTitle);
			} catch (Exception e) {
				log.error(e.getMessage().replaceAll("^.*\"message\":\"(.*?)\".*$", "$1"));
				continue;
			}

			int updatedQty = product.getQuantity() - cartItem.getQuantity();
			product.setQuantity(updatedQty);

			try {
				productMongoDBService.updateProduct(product, location, productTitle);
			} catch (Exception e) {
				log.error(e.getMessage().replaceAll("^.*\"message\":\"(.*?)\".*$", "$1"));
				response.put("status", 500);
				response.put("errorMsg", "Internal Server Error");
				return response;
			}

			log.info("Product: " + cartItem.getProductTitle() + " qty updated in MongoDB: updated qty = " + updatedQty);
		}

		response.put("status", 200);
		response.put("msg", "Payment Successful dated:" + getCurrentDate());
		return response;
	}

	private void resetQtyInLocalDB(CartDetails fetchedCart, Map<String, Object> response) throws Exception {
		// Second pass: Update quantities for all items
		for (ItemsFromCartDetails cartItem : fetchedCart.getItems().values()) {
			if (cartItem == null) {
				continue;
			}

			String location = cartItem.getShippingWareHouseLocation();
			String productTitle = cartItem.getProductTitle();
			Product product;

			try {
				product = productMongoDBService.findParticularProduct(location, productTitle);
			} catch (Exception e) {
				log.error(e.getMessage().replaceAll("^.*\"message\":\"(.*?)\".*$", "$1"));
				continue;
			}

			int updatedQty = product.getQuantity() + cartItem.getQuantity();
			product.setQuantity(updatedQty);

			try {
				productMongoDBService.updateProduct(product, location, productTitle);
			} catch (Exception e) {
				log.error(e.getMessage().replaceAll("^.*\"message\":\"(.*?)\".*$", "$1"));
				response.put("status", 500);
				response.put("errorMsg", "Internal Server Error");
				throw e;
			}

			log.info("Product: " + cartItem.getProductTitle() + " qty updated in MongoDB: updated qty = " + updatedQty);
		}

		return;
	}

	private boolean generateInvoice(OrderDetails orderDetails,String emailId) throws Exception {

		byte[] pdfBytes;
		try {
			pdfBytes = pdfGenerator.generateInvoice(orderDetails);
		} catch (IOException e) {
			log.error(e.toString());
			throw e;
		}

		// Send the email with the PDF attached
		try {
			sendInvoiceEmail("rajababu4we@gmail.com", pdfBytes);
		} catch (MessagingException e) {
			log.error(e.toString());
			throw e;
		}
		return true;
	}

	private void sendInvoiceEmail(String to, byte[] pdfBytes) throws MessagingException {
		MimeMessage message = emailService.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true);

		helper.setTo(to);
		helper.setSubject("Invoice Copy for your recent purchase");
		String body = emailService.generateInvoiceEmailBody();
        helper.setText(body, true);
		ByteArrayResource byteArrayResource = new ByteArrayResource(pdfBytes);
		helper.addAttachment("Invoice.pdf", byteArrayResource);

		emailService.sendMimeMessage(message);
	}

}
