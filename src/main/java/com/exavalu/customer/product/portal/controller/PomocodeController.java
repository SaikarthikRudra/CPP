package com.exavalu.customer.product.portal.controller;

import java.util.ArrayList;
import java.util.HashMap;
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

import com.exavalu.customer.product.portal.entities.dto.cart.ApplyPromocodeRequestpayload;
import com.exavalu.customer.product.portal.entities.salesforce.PromocodeCPP;
import com.exavalu.customer.product.portal.service.salesforceservice.PromocodeCPPSFService;
import com.exavalu.customer.product.portal.service.salesforceservice.PromocodeRecordsCPPSFService;
import com.exavalu.customer.product.portal.services.PromocodeService;

@RestController
public class PomocodeController {

	@Autowired
	private PromocodeCPPSFService promocodeCPPSFService;

	@Autowired
	private PromocodeRecordsCPPSFService promocodeRecordsCPPSFService;

	@Autowired
	private PromocodeService promocodeService;

	@GetMapping("/admin/promocode")
	public ResponseEntity<Object> getCashback(@RequestParam(value = "promocode", required = true) String promocode) {
		List<PromocodeCPP> fetchedPromocode = new ArrayList<>();
		fetchedPromocode = promocodeCPPSFService.getParticularpromocodeDetails(promocode);

		if (fetchedPromocode.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No promo code found!");
		}

		return ResponseEntity.status(HttpStatus.OK).body(fetchedPromocode);
	}

	@PutMapping("/admin/changestatus")
	public ResponseEntity<Object> putCashback(@RequestBody PromocodeCPP promocode) throws Exception {

		Map<String, Object> promocodeResponse = promocodeCPPSFService.updatepromocodeStatus(promocode);

		return ResponseEntity.status(HttpStatus.OK).body(promocodeResponse);
	}

	@PostMapping("/admin/createpromocode")
	public ResponseEntity<Object> createPromocode(@RequestBody PromocodeCPP promocodeCPP) throws Exception {
		Map<String, Object> response = new HashMap<>();
		if(promocodeCPP.getCashback() == null || promocodeCPP.getPromoCode() == null || promocodeCPP.getStatus()== null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("required fields are missing!");
		}
		response = promocodeCPPSFService.createPromocode(promocodeCPP);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
//for customer to apply promo code
	@PostMapping("/v1/applypromocode")
	public ResponseEntity<Object> putCashback(@RequestBody ApplyPromocodeRequestpayload applyPromocodeRequestpayload)
			throws Exception {
		Map<String, Object> response = new HashMap<>();
//		promocodeRecordsCPPSFService.updateAddUsedPrommocodeToPromocodeRecords(applyPromocodeRequestpayload);
//		Map<String, Object> promocodeResponse = promocodeCPPSFService.updatepromocodeStatus(promocode);
		response = promocodeService.PromocodeServiceProcess(applyPromocodeRequestpayload);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

//	@PostMapping("/createpromocode")
//	public ResponseEntity<Object> createCashback(@RequestBody PromocodeCPP promocode) throws Exception {
//
//		Map<String, Object> cashbackResponse = cashbackCPPSFService.createCashbackWallet(cashback);
//
//		return ResponseEntity.status(HttpStatus.OK).body(cashbackResponse);
//	}

}
