package com.exavalu.customer.product.portal.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.exavalu.customer.product.portal.entities.dto.cart.ApplyPromocodeRequestpayload;
import com.exavalu.customer.product.portal.entities.salesforce.CashbackCPP;
import com.exavalu.customer.product.portal.entities.salesforce.PromocodeCPP;
import com.exavalu.customer.product.portal.entities.salesforce.PromocodeRecordsCPP;
import com.exavalu.customer.product.portal.service.salesforceservice.CashbackCPPSFService;
import com.exavalu.customer.product.portal.service.salesforceservice.PromocodeCPPSFService;
import com.exavalu.customer.product.portal.service.salesforceservice.PromocodeRecordsCPPSFService;

@Service
public class PromocodeService {
	private static final Logger log = LogManager.getLogger(PromocodeService.class);
	@Autowired
	private PromocodeCPPSFService promocodeCPPSFService;
	@Autowired
	private PromocodeRecordsCPPSFService promocodeRecordsCPPSFService;

	@Autowired
	private CashbackCPPSFService cashbackCPPSFService;

	public Map<String, Object> PromocodeServiceProcess(ApplyPromocodeRequestpayload promocodePayload) {

		Map<String, Object> response = new HashMap<>();

		// check if promocode is valid or not
		List<PromocodeCPP> fethcedPromocodeInfo = promocodeCPPSFService
				.getParticularpromocodeDetails(promocodePayload.getPromocode());
		if (fethcedPromocodeInfo.isEmpty()) {
			response.put("msg", "Promocode is not valid!");
			return response;
		}
		PromocodeCPP promocodeInfo = new PromocodeCPP();
		promocodeInfo = fethcedPromocodeInfo.get(0);

		// check if promocode is active or not
		if (promocodeInfo.getStatus().equals("Inactive")) {
			response.put("msg", "promocode: " + promocodePayload.getPromocode() + " is no longer active");
			return response;
		}

		// check for promocode record does customer is eligible for promocode or not
		List<PromocodeRecordsCPP> fetchedPromocodeRecordList = promocodeRecordsCPPSFService
				.getParticularCustomerUsedPromocodeRecordDetails(promocodePayload.getCustomerId());
		if (fetchedPromocodeRecordList.isEmpty()) {
			response.put("msg", " customer does not have used promocode records! not eligible.");
			return response;
		}

		// check if promocode is already used by customer or not
		PromocodeRecordsCPP fetchedPromocodeRecord = new PromocodeRecordsCPP();
		fetchedPromocodeRecord = fetchedPromocodeRecordList.get(0);
		if (fetchedPromocodeRecord.getUsedPromocodes() != null) {
			if (fetchedPromocodeRecord.getUsedPromocodes().contains(promocodePayload.getPromocode())) {
				response.put("msg",
						"promocode is already used by customer with customerId:" + promocodePayload.getCustomerId());
				return response;
			}
		}
		// prepare object to add used promocode by customerId in promocodeRecord object
		// in salesforce
		PromocodeRecordsCPP promocodeRecord = new PromocodeRecordsCPP();
		promocodeRecord.setCustomerId(promocodePayload.getCustomerId());
		List<String> promocodeUsedList = new ArrayList<>();
		promocodeUsedList.add(promocodePayload.getPromocode());
		promocodeRecord.setUsedPromocodes(promocodeUsedList);

		try {
			promocodeRecordsCPPSFService.updateAddUsedPrommocodeToPromocodeRecords(promocodeRecord);
		} catch (Exception e) {
			log.error(e.toString());
			
		}

		// fetch cashback wallet to update the cashpack point back to wallet
		CashbackCPP cashbackwalletUpdate = new CashbackCPP();
		List<CashbackCPP> fetchedCashbackWallet = cashbackCPPSFService
				.getParticularCashbackWallet(promocodePayload.getCustomerId(), "customerId");
		cashbackwalletUpdate = fetchedCashbackWallet.get(0);
		double cashbackDoubleFormat = Double.parseDouble(promocodeInfo.getCashback());
		int cashback = ((int) cashbackDoubleFormat) + cashbackwalletUpdate.getCashbackWallet();
		cashbackwalletUpdate.setCashbackWallet(cashback);

		try {
			cashbackCPPSFService.updateCashbackWalletActive(cashbackwalletUpdate);
			response.put("cashback received: ", (int) cashbackDoubleFormat);
			response.put("msg", "check cashback wallet for more total cashback available.");

		} catch (Exception e) {
			log.error(e.toString());
		}

		return response;
	}

}
