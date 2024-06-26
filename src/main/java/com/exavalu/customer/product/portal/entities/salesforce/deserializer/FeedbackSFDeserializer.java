package com.exavalu.customer.product.portal.entities.salesforce.deserializer;

import java.io.IOException;

import com.exavalu.customer.product.portal.entities.dto.orderFeedbackDetails.FeedbackDetails;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class FeedbackSFDeserializer extends JsonDeserializer<FeedbackDetails>{

	@Override
	public FeedbackDetails deserialize(JsonParser parser, DeserializationContext context) throws IOException {
		ObjectMapper mapper = (ObjectMapper) parser.getCodec();
		ObjectNode root = mapper.readTree(parser);
		com.exavalu.customer.product.portal.entities.dto.orderFeedbackDetails.FeedbackDetails feedback = new FeedbackDetails();

		// Manually map each field from the JSON response to the corresponding field in
		// the feedbackDetails object
		if (root.has("productTitle")) {
			feedback.setProductTitle(root.get("productTitle").asText());
		}

		if (root.has("Display")) {
			feedback.setDisplay(root.get("Display").asText());
		} 
		
		if (root.has("Camera")) {
			feedback.setCamera(root.get("Camera").asText());
		}

		if (root.has("Performance")) {
			feedback.setPerformance(root.get("Performance").asText());
		} 

		if (root.has("Overall")) {
			feedback.setOverall(root.get("Overall").asText());
		} 

		if (root.has("description")) {
			feedback.setDescription(root.get("description").asText());
		} 



		return feedback;
	}
}
