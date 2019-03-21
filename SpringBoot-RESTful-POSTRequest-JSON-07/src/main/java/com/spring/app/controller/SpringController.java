package com.spring.app.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.spring.app.model.Customer;

@RestController
public class SpringController {
	
	@PostMapping(path="save-cust-info")
	public String customerInformation(@RequestBody Customer cust) {
		// DAO Logic goes here
		return "Customer information Saved Successfully: " +cust.getCustNo() + " " +cust.getName() + " " +cust.getCountry();
	}
}