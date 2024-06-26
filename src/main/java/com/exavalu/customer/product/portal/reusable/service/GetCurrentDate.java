package com.exavalu.customer.product.portal.reusable.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;

@Service
public class GetCurrentDate {
	
	public String getCurrentDate() {
		LocalDate currentDate = LocalDate.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		return currentDate.format(formatter);
	}

}
