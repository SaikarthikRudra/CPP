package com.exavalu.customer.product.portal.salesforce.connectivity;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.exavalu.customer.product.portal.service.salesforceservice.CustomerDetailsSFService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Service
public class SalesforceAuthenticatorService {

	private static final Logger log = LogManager.getLogger(CustomerDetailsSFService.class);
	
	@Value("${salesforce.authenticator.baseUrl}")
	private String baseUrl;
	@Value("${salesforce.authenticator.username}")
	private String username;
	@Value("${salesforce.authenticator.password}")
	private String password;
	@Value("${salesforce.authenticator.client_id}")
	private String client_id;
	@Value("${salesforce.authenticator.client_secret}")
	private String client_secret;
	@Value("${salesforce.authenticator.grant_type}")
	private String grant_type;

	public Map<String,String> getAccessToken() {
		
		Map<String,String> sfAccessToken = new HashMap<>();
		try {
			URI uri = new URI(baseUrl);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

			MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
			params.add("username", username);
			params.add("password", password);
			params.add("client_secret", client_secret);
			params.add("client_id",client_id);
			params.add("grant_type", grant_type);

			HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(params,
					headers);

			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<Map> response = restTemplate.postForEntity(uri, request, Map.class);

			Map<String, String> responseBody = response.getBody();
			sfAccessToken.put("accessToken", responseBody.get("access_token"));
			sfAccessToken.put("instanceUrl", responseBody.get("instance_url"));

		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return sfAccessToken;
	}
	
}
