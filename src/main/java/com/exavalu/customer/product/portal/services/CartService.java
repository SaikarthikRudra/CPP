package com.exavalu.customer.product.portal.services;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.exavalu.customer.product.portal.entities.dto.cart.AddToCartRequest;
import com.exavalu.customer.product.portal.entities.mongodb.Product;
import com.exavalu.customer.product.portal.entities.salesforce.CartDetails;
import com.exavalu.customer.product.portal.entities.salesforce.CustomerSF;
import com.exavalu.customer.product.portal.entities.salesforce.Enquiry;
import com.exavalu.customer.product.portal.entities.salesforce.ItemsFromCartDetails;
import com.exavalu.customer.product.portal.service.distanceapi.DistanceServiceMain;
import com.exavalu.customer.product.portal.service.mongodbservice.ProductMongoDBService;
import com.exavalu.customer.product.portal.service.salesforceservice.CartDetailsSFService;
import com.exavalu.customer.product.portal.service.salesforceservice.CustomerDetailsSFService;

@Service
public class CartService {
	private static final Logger log = LogManager.getLogger(CartService.class);

	@Autowired
	CustomerDetailsSFService customerDetailsSFService;
	@Autowired
	DistanceServiceMain distanceServiceMain;
	@Autowired
	ProductMongoDBService productMongoDBService;
	@Autowired
	CartDetailsSFService cartDetailsSFService;
	@Autowired
	EnquiryService enquiryService;

	public Map<String, Object> addToCartProcess(AddToCartRequest cartPayload) {
		
		
		Map<String, Object> response = new HashMap<>();
		
		for (var productRequest : cartPayload.getProducts()) {
			if(productRequest.getQuantity() < 0) {
				response.put("Invalid-quantity", "Product:"+productRequest.getProductTitle()+ "- quantity should not be negative");
				response.put("status", 400);
				return response;
			}
		}
		
		if (cartPayload.getEmailId() == null && cartPayload.getCustomerId() == null) {
			response.put("msg", "Either EmailId or CustomerId is required.");
			response.put("status", 400);
			return response;
		}
		CartDetails cart = new CartDetails();
		if (cartPayload.getCustomerId() != null) {
			List<CustomerSF> fetchedCustomer = customerDetailsSFService
					.getParticularCustomer(cartPayload.getCustomerId(), "customerId");
			if (!fetchedCustomer.isEmpty()) {
				cart.setCustomerId(cartPayload.getCustomerId());
				cartPayload.setEmailId(fetchedCustomer.get(0).getEmailId());
			} else {
				response.put("msg", "Customer does not exist with given CustomerId.");
				response.put("status", 400);
				return response;
			}
		}

//		} else {
//			List<CustomerSF> fetchedCustomer = customerDetailsSFService.getParticularCustomer(cartPayload.getEmailId(),
//					"emailId");
//			if (!fetchedCustomer.isEmpty()) {
//				cartPayload.setCustomerId(fetchedCustomer.get(0).getCustomerId());
//			} else {
//				response.put("msg", "Customer does not exist with given EmailId.");
//				response.put("status", 400);
//				return response;
//			}
//
//		}

		Map<String, Object> distanceOfWareHouses = distanceServiceMain.getNearestLocations(cartPayload.getLocation());
		log.info(distanceOfWareHouses.toString());
		

		Map<String, ItemsFromCartDetails> items = new HashMap<>();
		String nearestDBLocation = "";

		int totalProductInCartAmount = 0;
		int deliveryCharges = 0;
		int totalCartAmount = 0;

		int itemCounter = 1; // Counter for numbered keys
		for (var productRequest : cartPayload.getProducts()) {
			boolean productAdded = false;
			for (String locationKey : List.of("NearestLocation", "SecondNearestLocation", "ThirdNearestLocation",
					"FourthNearestLocation")) {
				Product product = productMongoDBService.findParticularProduct(
						distanceOfWareHouses.get(locationKey).toString(), productRequest.getProductTitle());

				if (product != null && product.getQuantity() >= productRequest.getQuantity()) {

					log.info("productLocation: " + product.getLocation());
					// logic to add it to cart

					totalProductInCartAmount = (int) Math
							.round(totalProductInCartAmount + (product.getPrice() * productRequest.getQuantity()));
					deliveryCharges = setDeliveryCharges(locationKey, deliveryCharges, distanceOfWareHouses);

					nearestDBLocation = product.getLocation();
					int totalPrice = (int) Math
							.round((product.getPrice() * productRequest.getQuantity()) + deliveryCharges);
					totalCartAmount += totalPrice;

					// Calculate expected delivery date
					double distance = Double.parseDouble(distanceOfWareHouses.get(locationKey + "Distance").toString());
					String expectedDeliveryDate = getExpectedDeliveryDate(distance);

					// Create an instance of ItemsFromCartDetails and set its fields
					ItemsFromCartDetails itemDetails = new ItemsFromCartDetails();
					itemDetails.setProductTitle(product.getProductTitle());
					itemDetails.setWarranty(product.getWarranty());
					itemDetails.setQuantity(productRequest.getQuantity());
					itemDetails.setPrice(product.getPrice());
					itemDetails.setDeliveryCharge(deliveryCharges);
					itemDetails.setTotalPrice(totalPrice);
					itemDetails.setShippingWareHouseLocation(product.getLocation());
					itemDetails.setAddToCartDate(getCurrentDate());
					itemDetails.setExpectedDelivery(expectedDeliveryDate);
					itemDetails.setNearestDbLocation(distanceOfWareHouses.get("NearestLocation").toString());

					items.put(String.valueOf(itemCounter), itemDetails); // Use itemCounter as key
					itemCounter++; // Increment counter

					log.info("Product " + productRequest.getProductTitle() + "quantity in local warehouse - "
							+ product.getLocation() + " - quantity: " + product.getQuantity());
					productAdded = true;
					break;
				}
			}

			if (!productAdded) {
				//send productTitle for enquiry
				if(cartPayload.getEmailId()!=null) {
					
					Enquiry enquiry = new Enquiry();
					enquiry.setEmailId(cartPayload.getEmailId());
					enquiry.setProductTitle(productRequest.getProductTitle());
					enquiry.setQuantity(productRequest.getQuantity());
					enquiryService.createNewEnquiry(enquiry);
				}
				response.put("StockOut",
						"Product " + productRequest.getProductTitle() + " not available in required quantity will be procssed in enuiry.");
				log.info(
						"Product " + productRequest.getProductTitle() + " not available in required quantity will process for enquiry.");
			}
		}
		cart.setCustomerEmail(cartPayload.getEmailId());
		
		cart.setNearestDbLocation(nearestDBLocation);
		cart.setStatus("Booked");
		cart.setItems(items);
		cart.setTotalAmount(totalCartAmount);

		// implment method to check if cart exist or not based on emailId
		List<CartDetails> fetchedCardDetails = cartDetailsSFService.getParticularCartDetail(cartPayload.getEmailId(),
				"emailId");

		if (fetchedCardDetails.isEmpty() || fetchedCardDetails.get(0).getStatus() == "Empty") {
			try {
				cartDetailsSFService.createCart(cart);
			} catch (Exception e) {
				System.err.println(e.toString());
			}

		} else {
			CartDetails cartToBeUpdated = fetchedCardDetails.get(0);

			// Iterate through items in the cart prepared earlier
			for (ItemsFromCartDetails cartItem : cart.getItems().values()) {
				String productTitle = cartItem.getProductTitle();
				int productPrice = cartItem.getPrice();
				int updatedDeliveryCharge = cartItem.getDeliveryCharge();
				boolean productAlreadyInCart = false;
				// Iterate through existing items in the fetched cart details
				Iterator<Map.Entry<String, ItemsFromCartDetails>> iterator = cartToBeUpdated.getItems().entrySet()
						.iterator();
				while (iterator.hasNext()) {
					Map.Entry<String, ItemsFromCartDetails> entry = iterator.next();
					ItemsFromCartDetails existingCartItem = entry.getValue();

					if (existingCartItem.getProductTitle().equals(productTitle)) {
						// Update quantity and total price if the product already exists in the cart
						int updatedQuantity = cartItem.getQuantity();
						int updatedTotalPrice = (productPrice * updatedQuantity) + updatedDeliveryCharge;

						if (updatedQuantity == 0) {
							iterator.remove(); // Remove the item if quantity is zero
							productAlreadyInCart = true; // Set this to true since we handled this product
							break;
						}

						// Update existing item details
						existingCartItem.setQuantity(updatedQuantity);
						existingCartItem.setTotalPrice(updatedTotalPrice);
						existingCartItem.setDeliveryCharge(updatedDeliveryCharge);
						existingCartItem.setShippingWareHouseLocation(cartItem.getShippingWareHouseLocation());
						existingCartItem.setNearestDbLocation(cartItem.getNearestDbLocation());

						productAlreadyInCart = true;
						break;
					}
				}

//				// Iterate through existing items in the fetched cart details
//				for (ItemsFromCartDetails existingCartItem : cartToBeUpdated.getItems().values()) {
//					if (existingCartItem.getProductTitle().equals(productTitle)) {
//						// Update quantity and total price if the product already exists in the cart
//						int updatedQuantity = cartItem.getQuantity();
//						int updatedTotalPrice = (productPrice * updatedQuantity) + UpdatedDeliveryCharge;
//
//						// Update existing item details
//						existingCartItem.setQuantity(updatedQuantity);
//						existingCartItem.setTotalPrice(updatedTotalPrice);
//						existingCartItem.setDeliveryCharge(UpdatedDeliveryCharge);
//						existingCartItem.setShippingWareHouseLocation(cartItem.getShippingWareHouseLocation());
//						existingCartItem.setNearestDbLocation(cartItem.getNearestDbLocation());
//
//						productAlreadyInCart = true;
//						break;
//					}
//				}

				// If the product doesn't exist in the cart, add it
				if (!productAlreadyInCart) {
					// Add new item to the cartToBeUpdated
					ItemsFromCartDetails newItem = new ItemsFromCartDetails();
					newItem.setProductTitle(productTitle);
					newItem.setWarranty(cartItem.getWarranty());
					newItem.setQuantity(cartItem.getQuantity());
					newItem.setPrice(productPrice);
					newItem.setDeliveryCharge(cartItem.getDeliveryCharge());
					newItem.setTotalPrice(cartItem.getTotalPrice());
					newItem.setShippingWareHouseLocation(cartItem.getShippingWareHouseLocation());
					newItem.setAddToCartDate(cartItem.getAddToCartDate());
					newItem.setExpectedDelivery(cartItem.getExpectedDelivery());
					newItem.setNearestDbLocation(cartItem.getNearestDbLocation());

					// Add the new item to the cartToBeUpdated
					cartToBeUpdated.getItems().put(String.valueOf(cartToBeUpdated.getItems().size() + 1), newItem);

				}
			}

			// Calculate the total cart amount
			int totalCartAmountUpdated = 0;
			for (ItemsFromCartDetails item : cartToBeUpdated.getItems().values()) {
				totalCartAmountUpdated += item.getTotalPrice();

			}

			cartToBeUpdated.setTotalAmount(totalCartAmountUpdated);

			// Now you can update the cart details in Salesforce
			try {

				cartDetailsSFService.updateCartDetailsSF(cartToBeUpdated);

			} catch (Exception e) {
				log.error(e.toString());
				// handle error
			}
		}
		response.put("msg", "cart Details added or updated successfully!");
		response.put("status", 201);
		return response;
	}

	public static int calculateDeliveryCharges(double distance) {
		int baseCharge = 50;
		if (distance <= 100) {
			return baseCharge;
		} else {
			int additionalCharges = (int) Math.ceil((distance - 100) / 100) * 20;
			return baseCharge + additionalCharges;
		}
	}

	public static int setDeliveryCharges(String locationKey, int deliveryCharges,
			Map<String, Object> distanceOfWareHouses) {
		deliveryCharges = calculateDeliveryCharges(
				Double.parseDouble(distanceOfWareHouses.get(locationKey + "Distance").toString()));
		return deliveryCharges;

	}

	public String getCurrentDate() {
		LocalDate currentDate = LocalDate.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		return currentDate.format(formatter);
	}

	public String getExpectedDeliveryDate(double distance) {
		LocalDate currentDate = LocalDate.now();
		int additionalDays = (int) Math.ceil(distance / 100); // Add 1 day for every 100 km
		LocalDate deliveryDate = currentDate.plusDays(2 + additionalDays);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		return deliveryDate.format(formatter);
	}
}
