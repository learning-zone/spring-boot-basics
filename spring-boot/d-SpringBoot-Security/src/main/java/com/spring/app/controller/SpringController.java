package com.spring.app.controller;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
public class SpringController {

	@RequestMapping("/userlogin")
	public String indexPage() {
		return "User: Successfully logged in!";
	}
	@RequestMapping("/adminlogin")
	public String adminPage() {
		return "Admin: Successfully logged in!";
	}
}