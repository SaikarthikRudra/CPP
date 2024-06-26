package com.exavalu.customer.product.portal.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.exavalu.customer.product.portal.entities.dto.product.ProductDtoParticularItem;
import com.exavalu.customer.product.portal.entities.dto.product.ProductMapper;
import com.exavalu.customer.product.portal.entities.mongodb.Product;
import com.exavalu.customer.product.portal.entities.salesforce.ProductSF;
import com.exavalu.customer.product.portal.service.mongodbservice.ProductMongoDBService;
import com.exavalu.customer.product.portal.service.salesforceservice.ProductSalesforceService;

@RestController
public class ProductController {
	private static final Logger log = LogManager.getLogger(ProductController.class);

	@Autowired
	private ProductMongoDBService ProductMongoDBService;

	@Autowired
	private ProductSalesforceService ProductSFService;

	// MongoDB calling ---------------------------------------------
	@GetMapping("v1/product/get-product")
	public ResponseEntity<Object> getProduct(
			@RequestParam(value = "productTitle", required = false) String productTitle,
			@RequestParam("location") String location) {

		if (productTitle != null && !productTitle.isEmpty()) {
			Product product = ProductMongoDBService.findParticularProduct(location, productTitle);
			if (product != null) {
				List<ProductDtoParticularItem> productdto = ProductMapper.toDtoList(product);
				return ResponseEntity.ok(productdto);
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
			}
		} else {
			List<Product> product = ProductMongoDBService.findAll(location);
			if (product != null) {
				List<ProductDtoParticularItem> productdto = ProductMapper.toDtoList1(product);
				return ResponseEntity.ok(productdto);
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No product is available at this location");
			}
		}
	}

	@PostMapping("admin/create-product")
	public ResponseEntity<Object> createProductMongo(@RequestBody Product newProduct) {
		Map<String, String> response = new HashMap<>();
		try {
			response = ProductMongoDBService.createProduct(newProduct);
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			log.error("Error occurred: ", e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(e.getMessage().replaceAll("^.*\"message\":\"(.*?)\".*$", "$1"));
		}

	}

	@PutMapping("v1/product/update-quantity")
	public ResponseEntity<Object> updateProductMongo(@RequestParam(value = "location", required = true) String location,
			@RequestParam(value = "productTitle", required = true) String productTitle,
			@RequestBody Product updatedProduct) {
		Map<String, String> response = new HashMap<>();
		try {
			if (updatedProduct.getQuantity() > 0) {
				Product product = ProductMongoDBService.findParticularProduct(location, productTitle);
				if (product != null) {
					response = ProductMongoDBService.updateProduct(updatedProduct, location, productTitle);
					return ResponseEntity.ok(response);
				} else {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST)
							.body("Please Provide Correct ProductTitle and location");

				}

			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please Provide Correct Product Quantity");

			}
//			
		} catch (Exception e) {
			log.error("Error occurred: ", e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(e.getMessage().replaceAll("^.*\"message\":\"(.*?)\".*$", "$1"));
		}

	}

	// Salesforce calling ---------------------------------
	@GetMapping("v1/product/sf-get-product")
	public ResponseEntity<Object> getProductfromSalesforce(
			@RequestParam(value = "producttitle", required = false) String productTitle,
			@RequestParam(value = "location", required = false) String location) throws Exception {

		if (productTitle != null && !productTitle.isEmpty()) {
			List<ProductSF> particularProduct;
			try {
				particularProduct = ProductSFService.findParticularProduct(productTitle);
				if (particularProduct != null) {
					List<Object> productDto = ProductMapper.toDtoList(particularProduct, false);
					return ResponseEntity.ok(productDto);
				} else {
					
					return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
				}

			} catch (Exception e) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(e.getMessage().replaceAll("^.*\"message\":\"(.*?)\".*$", "$1"));
			}
		} else {
			List<ProductSF> allProducts = ProductSFService.findAll();
			if (allProducts != null && !allProducts.isEmpty()) {
				List<Object> productDto = ProductMapper.toDtoList(allProducts, true);
				return ResponseEntity.ok(productDto);
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No product is available");
			}
		}
	}

	@PostMapping("admin/sf-create-product")
	public ResponseEntity<Object> createProduct(@RequestBody ProductSF newproduct) {

		Map<String, String> response = new HashMap<>();
		try {
			response = ProductSFService.createProduct(newproduct);
			return ResponseEntity.status(HttpStatus.CREATED).body(response);

		} catch (Exception e) {
			log.warn(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(e.getMessage().replaceAll("^.*\"message\":\"(.*?)\".*$", "$1"));
		}
	}

}
