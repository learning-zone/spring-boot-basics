package com.spring.app.controller;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
public class SpringController {

	@RequestMapping("/login")
	public String userValidation() {
		return "User: Successfully logged in!";
	}
}