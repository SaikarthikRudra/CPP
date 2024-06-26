package com.exavalu.customer.product.portal.service.distanceapi;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class DistanceServiceAPI {

	@Value("${distanceAPIurl}")
	private String distanceAPIurl;

	private static final Logger log = LogManager.getLogger(DistanceServiceAPI.class);

	private final WebClient webClient;

	public DistanceServiceAPI(WebClient.Builder webClientBuilder) {
		this.webClient = webClientBuilder.build();
	}

	public double getDistance(String from, String to) {
		try {
			// Prepare the URL
			String url = String.format(distanceAPIurl + "distance-from-%s-in-to-%s-in", from.replace(" ", "-"),
					to.replace(" ", "-"));

			// Fetch the HTML page
			String response = this.webClient.get().uri(url).retrieve().bodyToMono(String.class).block();

			Document doc = Jsoup.parse(response);
			// Locate the distance element based on actual structure
			Element distanceElement = doc.selectFirst("p strong:contains(kilometers)");
			if (distanceElement == null) {
				throw new RuntimeException("Distance element not found in the response HTML.");
			}

			// Extract the distance value
			String distanceText = distanceElement.text();
			log.info("Extracted distance text: " + distanceText);

			double distance = getDistanceValue(distanceText);

			return distance;
		} catch (WebClientResponseException e) {
			log.error(e.getMessage().replaceAll("^.*\"message\":\"(.*?)\".*$", "$1"));
			return 0.0;
		} catch (Exception e) {
			log.error(e.getMessage().replaceAll("^.*\"message\":\"(.*?)\".*$", "$1"));
			return 0.0;
		}
	}

	public List<Double> getDistances(String from, List<String> locations) {
		List<Double> distances = new ArrayList<>();

		for (String location : locations) {
			distances.add(getDistance(from, location));
		}
		return distances;
	}

	public static double getDistanceValue(String distancestr) {
		String input = distancestr;

		// Define the regular expression pattern to match numbers
		Pattern pattern = Pattern.compile("\\d[\\d,]*");

		// Match the pattern against the input string
		Matcher matcher = pattern.matcher(input);

		// Initialize a double variable to store the extracted number
		double distance = 0.0;

		// Check if the pattern is found in the input string
		if (matcher.find()) {
			// Get the matched substring
			String matchedString = matcher.group();

			// Remove commas from the matched string
			String numberString = matchedString.replaceAll(",", "");

			// Parse the string to double
			distance = Double.parseDouble(numberString);
		}

		return distance;
	}
}
