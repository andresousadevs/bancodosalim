package com.bancodosalim.bancodosalim.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

	@GetMapping("/")
	public String index() {
		return "index"; // Retorna o nome do arquivo HTML (sem .html) em templates/
	}
}