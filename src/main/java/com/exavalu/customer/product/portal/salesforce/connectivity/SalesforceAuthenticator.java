package com.exavalu.customer.product.portal.salesforce.connectivity;

import java.net.URI;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SalesforceAuthenticator {

	private static final Logger log = LogManager.getLogger(SalesforceAuthenticator.class);
	private static SalesforceAuthenticator salesforceAuthenticator = null;
	public static String accessToken;
	public static String instanceUrl;

	private SalesforceAuthenticator() {
		try {
			final String baseUrl = "https://login.salesforce.com/services/oauth2/token";
			URI uri = new URI(baseUrl);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

			MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
			params.add("username", "muleprojectgroupexavalu@gmail.com");
			params.add("password", "Muleproject1234@");
			params.add("client_secret", "F49B13CF7131A734F51A8D5107490CC3E9138B11E4CEFFE6ED5E3E656EBDB651");
			params.add("client_id",
					"3MVG9wt4IL4O5wvKiqX9I23DKB6M9xBNEE2z_lNEaz_eccXwRsKAmA9k7C0RPkstWTUOTRZTIXJcIuFn1LTbt");
			params.add("grant_type", "password");

			HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(params,
					headers);

			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<Map> response = restTemplate.postForEntity(uri, request, Map.class);

			Map<String, String> responseBody = response.getBody();

			accessToken = responseBody.get("access_token");
			instanceUrl = responseBody.get("instance_url");
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}

	public static SalesforceAuthenticator getSalesforceToken() {
		try {
			if (salesforceAuthenticator == null) {
				salesforceAuthenticator = new SalesforceAuthenticator();
				return salesforceAuthenticator;
			} else {
				return salesforceAuthenticator;
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return null;
	}
}