package com.exavalu.customer.product.portal.salesforce.connectivity;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class SalesforceDataService {
	private static final Logger log = LogManager.getLogger(SalesforceDataService.class);
	SalesforceAuthenticator salesforceAuthenticator = SalesforceAuthenticator.getSalesforceToken();

	String instanceUrl = salesforceAuthenticator.instanceUrl;
	String accessToken = salesforceAuthenticator.accessToken;

	RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());

	HttpHeaders headers = new HttpHeaders();

	public Map getSalesforceData(String query) {
		try {
			String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8.toString());

			// endpoint URL for getting records
			final String baseUrl = instanceUrl + "/services/data/v52.0/query/?q=" + encodedQuery;
			URI uri = new URI(baseUrl);

			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			headers.add(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", salesforceAuthenticator.accessToken));
			HttpEntity<?> request = new HttpEntity<Object>(headers);
			ResponseEntity<Map> response = null;
			try {
				response = restTemplate.exchange(uri, HttpMethod.GET, request, Map.class);
			} catch (HttpClientErrorException e) {
				log.error(e.getMessage());
			}
			return response.getBody();
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return Collections.emptyMap();
	}

	public Map<String,Object> createSalesforceRecord(String objectName, Map<String, Object> requestBody) throws Exception {
		try {
			Map<String,Object> response = new HashMap<>();
			// endpoint URL for creating records
			final String baseUrl = instanceUrl + "/services/data/v52.0/sobjects/" + objectName + "/";
			URI uri = new URI(baseUrl);

			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.set("Authorization", "Bearer " + accessToken);

			// Prepare the request entity with customer data
			HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
			System.out.println("requestBody" + requestBody);
			// Send the request to Salesforce
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<String> responseEntity = restTemplate.exchange(uri, HttpMethod.POST, request, String.class);
//			System.out.println("responseEntity" + responseEntity);
			
			log.info("ID: " + responseEntity.getBody().substring(responseEntity.getBody().indexOf(":\"") + 2,
					responseEntity.getBody().indexOf("\",")));
			
			// Check the response status
			if (responseEntity.getStatusCode().is2xxSuccessful()) {
				String salesforceId = responseEntity.getBody().substring(responseEntity.getBody().indexOf(":\"") + 2,
						responseEntity.getBody().indexOf("\","));
				response.put("salesforceId",salesforceId);
				log.info("SalesforceId:" + salesforceId);
				log.info("Record created successfully in salesforce.");
				return response;
			} else {
				log.info("Failed to create new record. Response: " + responseEntity.getBody());
				return response;
			}
		} catch (HttpClientErrorException e) {
			log.error("Error creating record: " + " - " + e.getStatusText());
			throw e;
		} catch (Exception e) {
			log.error("Exception creating record: " + e.getMessage());
			throw e;
		}
	}

	// update salesforce record
	public void updateSalesforceRecord(String objectName, String recordId, Map<String, Object> requestBody)
			throws Exception {
		try {
			// endpoint URL for updating records
			final String baseUrl = instanceUrl + "/services/data/v52.0/sobjects/" + objectName + "/" + recordId;

			URI uri = new URI(baseUrl);

			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.set("Authorization", "Bearer " + accessToken);

			// Prepare the request entity with customer data
			HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

			// Send the request to Salesforce
//			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<String> responseEntity = restTemplate.exchange(uri, HttpMethod.PATCH, request, String.class);

			// Check the response status
			if (responseEntity.getStatusCode().is2xxSuccessful()) {
				log.info("Record updated successfully in salesforce.");
			} else {
				log.info("Failed to update the record. Response: " + responseEntity.getBody());
			}
		} catch (HttpClientErrorException e) {
			log.error("Error updating record: " + " - " + e.getStatusText());
			throw e;
		} catch (Exception e) {
			log.error("Exception updating record: " + e.getMessage());
			throw e;
		}
	}

	// delete salesforce record
	public void deleteSalesforceRecord(String objectName, String recordId) throws Exception {
		try {
			// endpoint URL for deleting records
			final String baseUrl = instanceUrl + "/services/data/v52.0/sobjects/" + objectName + "/" + recordId;
			URI uri = new URI(baseUrl);

			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.set("Authorization", "Bearer " + accessToken);

			// Prepare the request entity
			HttpEntity<Void> request = new HttpEntity<>(headers);

			// Send the request to Salesforce
			ResponseEntity<String> responseEntity = restTemplate.exchange(uri, HttpMethod.DELETE, request,
					String.class);

			// Check the response status
			if (responseEntity.getStatusCode().is2xxSuccessful()) {
				log.info("Record deleted successfully from Salesforce.");
			} else {
				log.info("Failed to delete the record. Response: " + responseEntity.getBody());
			}
		} catch (HttpClientErrorException e) {
			log.error("Error deleting record: " + " - " + e.getStatusText());
			throw e;
		} catch (Exception e) {
			log.error("Exception deleting record: " + e.getMessage());
			throw e;
		}
	}
}
