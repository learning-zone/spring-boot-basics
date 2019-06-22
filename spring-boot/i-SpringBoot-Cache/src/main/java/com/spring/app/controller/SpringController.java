package com.spring.app.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.spring.app.model.Customer;

@RestController
public class SpringController {
	
	@RequestMapping(path="get-cust-info")
	@Cacheable(value="cacheCustInfo")
	public List customerInformation() {
		
		System.out.println("Customer Inforamtion Controller Called");	
		List custDetails = Arrays.asList(
			new Customer(100, "Bank of India", "India"),
			new Customer(101, "Bank of America", "USA")		
		);
	  return custDetails;
	}
}