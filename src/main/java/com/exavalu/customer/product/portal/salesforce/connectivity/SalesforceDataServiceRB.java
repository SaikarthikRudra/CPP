package com.exavalu.customer.product.portal.salesforce.connectivity;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class SalesforceDataServiceRB {
	private static final Logger log = LogManager.getLogger(SalesforceDataServiceRB.class);
	@Autowired
	private SalesforceAuthenticatorService salesforceAuthenticatorService;
	
	public Map getSalesforceData(String query) {

		Map<String,String> sfAccessToken = new HashMap<>();
		sfAccessToken = salesforceAuthenticatorService.getAccessToken();
		try {
			RestTemplate restTemplate = new RestTemplate();
			String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8.toString());

			final String baseUrl = sfAccessToken.get("instanceUrl") + "/services/data/v52.0/query/?q="
					+ encodedQuery;
			URI uri = new URI(baseUrl);

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			headers.add(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", sfAccessToken.get("accessToken")));

			HttpEntity<?> request = new HttpEntity<Object>(headers);
			ResponseEntity<Map> response = null;
			try {
				response = restTemplate.exchange(uri, HttpMethod.GET, request, Map.class);
			} catch (HttpClientErrorException e) {
				log.error(e.toString());
			}
			return response.getBody();
		} catch (Exception e) {
			log.error(e.toString());
		}
		return Collections.emptyMap();
	}
}
