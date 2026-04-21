package com.server.realsync.mvc.controllers;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.server.realsync.entity.Account;
import com.server.realsync.entity.Customer;
import com.server.realsync.entity.Greeting;
import com.server.realsync.services.AccountService;
import com.server.realsync.services.CustomerService;
import com.server.realsync.services.GreetingService;
import com.server.realsync.util.SecurityUtil;

@Controller
@RequestMapping("/")
public class GreetingPageController {

    @Autowired
    private GreetingService greetingService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private AccountService accountService;

    @PostMapping("/greetings/edit/{id}")
    public String updateGreeting(@PathVariable Integer id,
            @ModelAttribute Greeting updated) {

        Account account = SecurityUtil.getCurrentAccountId();

        Greeting existing = greetingService
                .getById(id, account.getId())
                .orElseThrow();

        // update fields
        existing.setGreetingType(updated.getGreetingType());
        existing.setGreetingDate(updated.getGreetingDate());
        existing.setGreetingTime(updated.getGreetingTime());
        existing.setMessage(updated.getMessage());

        greetingService.save(existing);

        return "redirect:/greeting-detail.html?id=" + id;
    }

    @PostMapping("/greetings/delete/{id}")
    public String deleteGreeting(@PathVariable Integer id) {

        Account loggedIn = SecurityUtil.getCurrentAccountId();
        Account account = accountService.getById(loggedIn.getId());

        greetingService.delete(id, account.getId());

        return "redirect:/engagement.html";
    }

    @PostMapping("/greetings/send/{id}")
    public String sendGreeting(@PathVariable Integer id) {

        Account loggedIn = SecurityUtil.getCurrentAccountId();
        Account account = accountService.getById(loggedIn.getId());

        Greeting greeting = greetingService
                .getById(id, account.getId())
                .orElseThrow(() -> new RuntimeException("Greeting not found"));

        greeting.setStatus("Sent");
        greetingService.save(greeting);

        return "redirect:/greeting-detail.html?id=" + id;
    }
}