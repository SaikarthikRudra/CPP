package com.exavalu.customer.product.portal.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

import com.exavalu.customer.product.portal.entities.salesforce.CashbackCPP;
import com.exavalu.customer.product.portal.service.salesforceservice.CashbackCPPSFService;

@RestController
@RequestMapping("v1")
public class CashbackController {
	
	@Autowired
	private CashbackCPPSFService cashbackCPPSFService;
	
	@GetMapping("/cashback")
	public ResponseEntity<Object> getCashback(@RequestParam(value = "customerId", required = false) String customerId,
			@RequestParam(value = "emailId", required = false) String emailId) {
		List<CashbackCPP> cashback = new ArrayList<>();
		if(customerId == null) {
			cashback = cashbackCPPSFService.getParticularCashbackWallet(emailId,"emailId");
		}else if(emailId == null) {
			cashback = cashbackCPPSFService.getParticularCashbackWallet(customerId,"customerId");
		}
		if(cashback.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No cashback wallet found!");
		}
		
		return ResponseEntity.status(HttpStatus.OK).body(cashback);
	}


}
