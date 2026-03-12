package com.server.realsync.mvc.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @GetMapping("/login1")
    public String login() {
        return "auth/login"; // Return the view name for the login page
    }

    @GetMapping("/register")
    public String register(@RequestParam(name = "refAccId", required = false) String refAccId, Model model) {
    	model.addAttribute("refAccId", refAccId);
        return "auth/register"; // Return the view name for the login page
    }

}
