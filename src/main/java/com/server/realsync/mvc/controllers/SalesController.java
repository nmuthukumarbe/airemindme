package com.server.realsync.mvc.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.server.realsync.util.SecurityUtil;

import com.server.realsync.entity.Account;
import com.server.realsync.services.AccountService;

@Controller
public class SalesController {

    @Autowired
    private AccountService accountService;

    @GetMapping("/sales.html")
    public String getSalesPage(Model model) {

        Account loggedIn = SecurityUtil.getCurrentAccountId();
        Account account = accountService.getById(loggedIn.getId());

        model.addAttribute("account", account);

        return "remindmeui/sales";
    }

    @GetMapping("/invoice-create.html")
    public String getInvoiceCreate(Model model) {

        Account loggedIn = SecurityUtil.getCurrentAccountId();
        Account account = accountService.getById(loggedIn.getId());

        model.addAttribute("account", account);
        model.addAttribute("activePage", "sales");

        return "remindmeui/invoice-create";
    }

    @GetMapping("/invoice-detail.html")
    public String getInvoiceDetail(
            @RequestParam Long id,
            Model model) {

        Account loggedIn = SecurityUtil.getCurrentAccountId();
        Account account = accountService.getById(loggedIn.getId());

        model.addAttribute("account", account);
        model.addAttribute("activePage", "sales");

        return "remindmeui/invoice-detail";
    }
}