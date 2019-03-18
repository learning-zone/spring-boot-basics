package com.spring.app.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class SpringController {

	@GetMapping("/")
	public ModelAndView showLoginPage(Model model) {
		model.addAttribute("message", "Welcome to Spring Boot Tutorials!");
		return new ModelAndView("welcomePage");
	}
}