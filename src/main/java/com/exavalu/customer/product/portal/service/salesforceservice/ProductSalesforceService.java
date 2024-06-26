package com.exavalu.customer.product.portal.service.salesforceservice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.exavalu.customer.product.portal.entities.salesforce.ProductSF;
import com.exavalu.customer.product.portal.salesforce.connectivity.SalesforceDataService;
import com.exavalu.customer.product.portal.service.mongodbservice.ProductMongoDBService;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ProductSalesforceService {
	private static final Logger log = LogManager.getLogger(ProductSalesforceService.class);

	Map<String, String> response = new HashMap<>();

	String objectName = "ProductData__c";

	String queryString = "SELECT Id,productTitle__c,brandName__c, seriesName__c, price__c, ram__c, rom__c, warranty__c, quantity__c, location__c,compositeId__c FROM ProductData__c ";

	@Autowired
	private SalesforceDataService salesforceDataService;

	//	Get particular product from salesforce
	public List<ProductSF> findParticularProduct(String productTitle) throws Exception {
		String query = queryString + "WHERE productTitle__c = '" + productTitle + "'";
		Map<String, Object> data = salesforceDataService.getSalesforceData(query);

		if (data != null && data.get("records") != null && !((List<Object>) data.get("records")).isEmpty()) {
			log.info("Fetching product successfull from Salesforce!");
			return mapToProductList(data);
		}
		log.error("Failed to fetch product from Salesforce!");
		return null;
	}

	//	Get all product details
	public List<ProductSF> findAll() {
		String query = queryString;
		Map<String, Object> data = salesforceDataService.getSalesforceData(query);
		List<ProductSF> products = mapToProductList(data);

		// Filter distinct products based on product title
		Set<String> uniqueProductTitles = new HashSet<>();
		List<ProductSF> distinctProducts = new ArrayList<>();

		for (ProductSF product : products) {
			if (uniqueProductTitles.add(product.getProductTitle())) {
				distinctProducts.add(product);

			}
		}
		log.info("Fetching product successfull from Salesforce!");
		return distinctProducts;
	}

	//	Create product
	public Map<String, String> createProduct(ProductSF newProduct) throws Exception {
		Map<String, Object> productData = productDetailsObjectFieldMapped(newProduct, false);
		try {
			List<ProductSF> fetchProduct = findParticularProduct(newProduct.getProductTitle().toString());
			if (fetchProduct == null) {
				salesforceDataService.createSalesforceRecord(objectName, productData);
				log.info("Creating product successfull in Salesforce!");
				response.put("message", "New product added.");
			} else {
				throw new Exception("Product is already created!");
			}

		} catch (Exception e) {
			log.error("Failed to create product in Salesforce: " + e.getMessage().replaceAll("^.*\"message\":\"(.*?)\".*$", "$1"));
			throw new Exception(
					"Failed to create product: " + e.getMessage().replaceAll("^.*\"message\":\"(.*?)\".*$", "$1"));
		}

		return response;
	}

	// update product service
	public Map<String, String> updateProduct(ProductSF newProduct, String location) throws Exception {

		Map<String, Object> productData = productDetailsObjectFieldMapped(newProduct, true);

		try {
			List<ProductSF> productToBeUpdated = findParticularProduct(newProduct.getProductTitle().toString());
			String recordId = productToBeUpdated.get(0).getSalesforceId();
			productData.put("compositeId__c", newProduct.getCompositeId());
			productData.put("price__c", newProduct.getPrice());
			productData.put("quantity__c", newProduct.getQuantity());

			salesforceDataService.updateSalesforceRecord(objectName, recordId, productData);
			response.put("message", "Product quantity successfully modified.");
			log.info("Product update successfull in salesforce");

		} catch (Exception e) {
			log.error("Product update failed in salesforce");
			throw new Exception(
					"Failed to update product: " + e.getMessage().replaceAll("^.*\"message\":\"(.*?)\".*$", "$1"));

		}

		return response;
	}

	//	Mapping to product
	private List<ProductSF> mapToProductList(Map<String, Object> data) {
		List<ProductSF> productList = new ArrayList<>();
		ObjectMapper objectMapper = new ObjectMapper();
		List<Map<String, Object>> records = (List<Map<String, Object>>) data.get("records");
		for (Map<String, Object> record : records) {
			ProductSF product = objectMapper.convertValue(record, ProductSF.class);
			productList.add(product);
		}
		return productList;
	}

	// mapping to proper field --helper function to create new record
	private Map<String, Object> productDetailsObjectFieldMapped(ProductSF newProduct, boolean forUpdate) {
		Map<String, Object> productData = new HashMap<>();
		if (forUpdate) {
			productData.put("price__c", newProduct.getPrice());
			productData.put("quantity__c", newProduct.getQuantity());
			productData.put("compositeId__c", newProduct.getCompositeId());
		} else {
			productData.put("compositeId__c", newProduct.getCompositeId());
			productData.put("productTitle__c", newProduct.getProductTitle());
			productData.put("brandName__c", newProduct.getBrandName());
			productData.put("location__c", newProduct.getLocation());
			productData.put("ram__c", newProduct.getRam());
			productData.put("rom__c", newProduct.getRom());
			productData.put("seriesName__c", newProduct.getSeriesName());
			productData.put("warranty__c", newProduct.getWarranty());
			productData.put("price__c", newProduct.getPrice());
			productData.put("quantity__c", newProduct.getQuantity());
		}

		return productData;
	}
}
