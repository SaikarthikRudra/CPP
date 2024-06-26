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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.exavalu.customer.product.portal.entities.dto.complaint.ComplaintDto;
import com.exavalu.customer.product.portal.entities.dto.complaint.ComplaintMapper;
import com.exavalu.customer.product.portal.entities.salesforce.Complaint;
import com.exavalu.customer.product.portal.service.salesforceservice.ComplaintSalesforceService;

@RestController
//@RequestMapping("v1/complaint")
public class ComplaintController {
	private static final Logger log = LogManager.getLogger(ProductController.class);
	@Autowired
	private ComplaintSalesforceService ComplaintSFService;

	@PostMapping("v1/complaint/create")
	public ResponseEntity<Object> addComplaint(@RequestBody Complaint newComplaint) {
		Map<String, String> response = new HashMap<>();
		try {

			response = ComplaintSFService.addComplaint(newComplaint);
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			log.error("Failed to add complaint in salesforce: ", e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(e.getMessage().replaceAll("^.*\"message\":\"(.*?)\".*$", "$1"));
		}

	}

	@GetMapping("v1/complaint/details")
	public ResponseEntity<Object> getComplaint(
			@RequestParam(value = "complaintId", required = false) String complaintId,
			@RequestParam(value = "productTitle", required = false) String productTitle,
			@RequestParam(value = "OrderId", required = false) String OrderId) {
		try {
			if (complaintId != null && !complaintId.isEmpty()) {
				List<Complaint> response = ComplaintSFService.getComplaint(complaintId);

				if (!response.isEmpty() && response != null) {
					List<ComplaintDto> complaintdto = ComplaintMapper.toDtoList(response);
					return ResponseEntity.ok(complaintdto);
				} else {
					return ResponseEntity.status(HttpStatus.NOT_FOUND)
							.body("Complaint details not found for id: " + complaintId);
				}
			} else if (productTitle != null && !productTitle.isEmpty() && OrderId != null && !OrderId.isEmpty()) {
				List<Complaint> response = ComplaintSFService.getComplaint(productTitle, OrderId);

				if (!response.isEmpty() && response != null) {
					List<ComplaintDto> complaintdto = ComplaintMapper.toDtoList(response);
					return ResponseEntity.ok(complaintdto);
				} else {
					return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
							"Complaint details not found for productTitle: " + productTitle + "OrderId" + OrderId);
				}
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body("Either provide Complaint id or (Product title and order id) both");
			}

		} catch (Exception e) {
			log.error("Error occurred: ", e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Get complaint details failed");
		}

	}

	@PutMapping("admin/complaint/close")
	public ResponseEntity<Object> closeComplaint(
			@RequestParam(value = "complaintId", required = false) String complaintId,
			@RequestParam(value = "action", required = false) String action, @RequestBody Complaint newComplaint) {
		Map<String, String> response = new HashMap<>();
		if (complaintId != null && !complaintId.isEmpty()) {
			try {
				response = ComplaintSFService.closeComplaint(newComplaint, complaintId, action);
				if (!response.isEmpty() && response != null) {
					return ResponseEntity.ok(response);
				} else {
					return ResponseEntity.status(HttpStatus.NOT_FOUND)
							.body("Complaint details not found for id: " + complaintId);
				}
			} catch (Exception e) {
				log.error("Error occurred: ", e);
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body(e.getMessage().replaceAll("^.*\"message\":\"(.*?)\".*$", "$1"));
			}
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please provide complaintId to proceed. ");
		}

	}

}
