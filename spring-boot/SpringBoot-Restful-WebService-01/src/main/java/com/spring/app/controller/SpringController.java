package com.spring.app.controller;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
public class SpringController {

	@RequestMapping("/")
	public String indexPage() {
		return "Welcome to Spring Boot Tutorials!";
	}
	
	@RequestMapping("/admin")
	public String adminPage() {
		return "Welcome to Admin Page!";
	}
}