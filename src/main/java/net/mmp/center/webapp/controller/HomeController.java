package net.mmp.center.webapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class HomeController {
	
	@RequestMapping(path = "/", method = RequestMethod.GET)
	public String main() {
		return "main";
	}
	
	@RequestMapping(path = "/main", method = RequestMethod.GET)
	public String index() {
		return "main";
	}
	
	@RequestMapping(path = "/login", method = RequestMethod.GET)
	public String login() {
		return "login";
	}
}
