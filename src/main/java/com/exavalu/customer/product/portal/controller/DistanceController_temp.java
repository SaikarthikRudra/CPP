package com.exavalu.customer.product.portal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.exavalu.customer.product.portal.service.distanceapi.DistanceServiceMain;
@RestController
public class DistanceController_temp {
	
	@Autowired
	private DistanceServiceMain distanceServiceMain;

	@GetMapping("/getNearestLocations")
	public ResponseEntity<Object> getNearestLocations(@RequestParam String location) {
	    return ResponseEntity.ok(distanceServiceMain.getNearestLocations(location));
	}

}
