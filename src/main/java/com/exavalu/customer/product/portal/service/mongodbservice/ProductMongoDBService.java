package com.exavalu.customer.product.portal.service.mongodbservice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import com.exavalu.customer.product.portal.entities.mongodb.Product;
import com.exavalu.customer.product.portal.entities.salesforce.ProductSF;
import com.exavalu.customer.product.portal.service.queueservice.QueueItemWrapper;
import com.exavalu.customer.product.portal.service.queueservice.QueueServiceEvent;

@Service
public class ProductMongoDBService {
	private static final Logger log = LogManager.getLogger(ProductMongoDBService.class);

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

	Map<String, String> response = new HashMap<>();

	// Get all products
	public List<Product> findAll(String location) {
		switch (location) {
		case "Mumbai":
			return mumbaiMongoTemplate.findAll(Product.class);
		case "Kolkata":
			return kolkataMongoTemplate.findAll(Product.class);
		case "Hyderabad":
			return hyderabadMongoTemplate.findAll(Product.class);
		case "Bangalore":
			return bangaloreMongoTemplate.findAll(Product.class);
		default:
			return null;
		}

	}

	// Get particular product
	public Product findParticularProduct(String location, String productTitle) {
		Query query = new Query();
		query.addCriteria(Criteria.where("productTitle").is(productTitle));
		switch (location) {
		case "Mumbai":
			mumbaiMongoTemplate.findOne(query, Product.class);
		case "Kolkata":
			return kolkataMongoTemplate.findOne(query, Product.class);
		case "Hyderabad":
			return hyderabadMongoTemplate.findOne(query, Product.class);
		case "Bangalore":
			return bangaloreMongoTemplate.findOne(query, Product.class);
		default:
			return null;
		}

	}

	// Update product based on product title and location
	public Map<String, String> updateProduct(Product product, String location, String productTitle) throws Exception {
		Query query = new Query();
		Map<String, String> updateProductResponse;
		int productQuantity = product.getQuantity();
		query.addCriteria(Criteria.where("productTitle").is(productTitle));

		switch (location) {
		case "Mumbai":
			MongoTemplate MumbaiMongoTemplate = mumbaiMongoTemplate;
			updateProductResponse = updateProduct(MumbaiMongoTemplate, query, productQuantity);
			break;
		case "Kolkata":
			MongoTemplate KolkataMongoTemplate = kolkataMongoTemplate;
			updateProductResponse = updateProduct(KolkataMongoTemplate, query, productQuantity);
			break;
		case "Hyderabad":
			MongoTemplate HyderabadMongoTemplate = hyderabadMongoTemplate;
			updateProductResponse = updateProduct(HyderabadMongoTemplate, query, productQuantity);
			break;
		case "Bangalore":

			MongoTemplate BangaloreMongoTemplate = bangaloreMongoTemplate;
			updateProductResponse = updateProduct(BangaloreMongoTemplate, query, productQuantity);
			break;
		default:
			log.error("Invalid location provided");
			throw new Exception("Failed to update product in MongoDB. Please provide correct location!");

		}

		addToQueue(location, productTitle, "update");

		return updateProductResponse;
	}

	// Create product
	public Map<String, String> createProduct(Product product) throws Exception {
		if (product.getProductTitle() == null || product.getLocation() == null) {
			throw new Exception("Product title or location is empty!");
		}

		String location = product.getLocation();
		String productTitle = product.getProductTitle();
		Query query = new Query();
		query.addCriteria(Criteria.where("productTitle").is(productTitle));

		Map<String, String> saveProduct = null;
		switch (location) {
		case "Mumbai":
			MongoTemplate MumbaiMongoTemplate = mumbaiMongoTemplate;
			saveProduct = saveProduct(MumbaiMongoTemplate, product, query);
			break;
		case "Kolkata":
			MongoTemplate KolkataMongoTemplate = kolkataMongoTemplate;
			saveProduct = saveProduct(KolkataMongoTemplate, product, query);
			break;
		case "Hyderabad":
			MongoTemplate HyderabadMongoTemplate = hyderabadMongoTemplate;
			saveProduct = saveProduct(HyderabadMongoTemplate, product, query);
			break;
		case "Bangalore":
			MongoTemplate BangaloreMongoTemplate = bangaloreMongoTemplate;
			saveProduct = saveProduct(BangaloreMongoTemplate, product, query);
			break;
		default:
			log.error("Invalid location provided");
			throw new Exception("Failed to create product in MongoDB. Please provide correct location!");
		}

		addToQueue(location, productTitle, "create");

		return saveProduct;

	}

	// Update product in MongoDB
	private Map<String, String> updateProduct(MongoTemplate locationMongoTemplate, Query query, int productQuantity)
			throws Exception {
		Update update = new Update();
		try {
			Product existingProduct = locationMongoTemplate.findOne(query, Product.class);
			if (existingProduct != null) {
				int quantity = existingProduct.getQuantity();
				int totalQuantity = quantity + productQuantity;
				update.set("quantity", totalQuantity);
				locationMongoTemplate.updateFirst(query, update, Product.class);
				response.put("message", "Product quantity successfully modified.");
				log.info("product updated successfully in MongoDB");
				return response;
			} else {
				throw new Exception("Product doesn't exist!");
			}

		} catch (Exception e) {
			throw new Exception("Failed to update product in MongoDB!");

		}

	}

	// Save product in mongoDB
	private Map<String, String> saveProduct(MongoTemplate locationMongoTemplate, Product product, Query query)
			throws Exception {
		Product existingProduct;
		try {
			existingProduct = locationMongoTemplate.findOne(query, Product.class);
			if (existingProduct == null) {
				locationMongoTemplate.save(product);
				response.put("message", "New product added.");
				log.info("Product created successfully in MongoDB");
				return response;
			} else {
				throw new Exception("Product exist!");
			}

		} catch (Exception e) {
			throw new Exception("Failed to create product in MongoDB!");
		}
	}

	//	Product added to queue
	private void addToQueue(String location, String productTitle, String operation) {
		Product newProduct = findParticularProduct(location, productTitle);
		if (newProduct != null) {
			addToQueueMethod(newProduct, operation);
			log.info("Product is added to queue");
		} else {
			log.error("Unable to add to queue invalid operation.");
		}

	}

	// Async call to update data in Salesforce
	@Async
	private void addToQueueMethod(Product product, String operation) {
		ProductSF newProductsf = mapNewProductSF(product);
		queueServiceEvent.addToQueue(new QueueItemWrapper<>(newProductsf, operation));
	}

	private ProductSF mapNewProductSF(Product product) {
		ProductSF newProductsf = new ProductSF();
		newProductsf.setProductTitle(product.getProductTitle());
		newProductsf.setPrice(product.getPrice());
		newProductsf.setBrandName(product.getBrandName());
		newProductsf.setLocation(product.getLocation());
		newProductsf.setQuantity(product.getQuantity());
		newProductsf.setRam(product.getRam());
		newProductsf.setLocation(product.getLocation());
		newProductsf.setRom(product.getRom());
		newProductsf.setSeriesName(product.getSeriesName());
		newProductsf.setWarranty(product.getWarranty());
		newProductsf.setCompositeId(product.getProductTitle() + "-" + product.getLocation());
		return newProductsf;
	}

}
