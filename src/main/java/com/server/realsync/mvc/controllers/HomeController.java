package com.server.realsync.mvc.controllers;

import java.util.Optional;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestBody;

import com.server.realsync.entity.Account;
import com.server.realsync.entity.User;
import com.server.realsync.entity.Customer;
import com.server.realsync.entity.Product;
import com.server.realsync.services.CustomerService;
import com.server.realsync.services.ProductService;
import com.server.realsync.services.UserService;
import com.server.realsync.util.CustomerMessageService;
import com.server.realsync.util.GmailSender;
import com.server.realsync.util.SecurityUtil;

import io.swagger.v3.oas.annotations.tags.Tag;

@Controller
@RequestMapping("/")
@Tag(name = "Home API", description = "HomeController APIs")
public class HomeController {

	@Autowired
	private UserService userService;
	@Autowired
	private CustomerService customerService;
	@Autowired
	CustomerMessageService customerMessageService;

	@Autowired
	GmailSender gmailSender;

	@GetMapping
	public String getWebHomePage(Model model) {
		Account account = SecurityUtil.getCurrentAccountId();
		model.addAttribute("accountId", account.getId());
		return "remindme/index.html";
	}

	@GetMapping({ "/login.html", "/signin.html" })
	public String getLogin(@RequestParam(value = "error", required = false) String error, Model model) {
		if ("true".equals(error)) {
			model.addAttribute("errorMessage", "Invalid username or password");
		}
		return "remindmeui/login";
		// return "realsync/index";
	}

	@GetMapping("/signup.html")
	public String getRegister(@RequestParam(value = "refAccId", required = false) String refAccId,
			Model model) {

		model.addAttribute("refAccId", refAccId);
		return "remindme/signup";
	}

	@GetMapping("/home.html")
	public String getAdminDashboard(Model model) {

		return "remindmeui/home";
	}

	@GetMapping("/customers.html")
	public String getCustomers(Model model) {
		Account account = SecurityUtil.getCurrentAccountId();

		List<Customer> customers = customerService.getAllByAccount(account.getId());

		model.addAttribute("customers", customers);
		return "remindmeui/customers";
	}

	@PostMapping("/api/customers")
	@ResponseBody
	public Customer createCustomer(@RequestBody Customer customer) {
		Account account = SecurityUtil.getCurrentAccountId();
		customer.setAccountId(account.getId());

		return customerService.save(customer);
	}

	@GetMapping("/customer-detail.html")
	public String getCustomerDetail(Model model) {

		return "remindmeui/customer-detail";
	}

	@GetMapping("/engagement.html")
	public String getEngagement(Model model) {

		return "remindmeui/engagement";
	}

	@GetMapping("/promotions.html")
	public String getPromotions(Model model) {

		return "remindmeui/promotions";
	}

	@GetMapping("/reminder-detail.html")
	public String getReminderDetail(Model model) {

		return "remindmeui/reminder-detail";
	}

	@GetMapping("/greeting-detail.html")
	public String getGreetingDetail(Model model) {

		return "remindmeui/greeting-detail";
	}

	@GetMapping("/promo-landing.html")
	public String getPromoLanding(Model model) {

		return "remindmeui/promo-landing";
	}

	@GetMapping("/catalog.html")
	public String getCatalog(Model model) {

		return "remindmeui/catalog.html";
	}

	@GetMapping("/reports.html")
	public String getAdminReport(Model model) {

		return "remindme/reports";
	}

	@GetMapping("/settings.html")
	public String getSettings(Model model) {

		// User user = SecurityUtil.getLoggedInUser();
		// model.addAttribute("userRole", user.getRole().getName());
		// model.addAttribute("user", user);
		return "remindmeui/settings";
	}

	@GetMapping("index.html")
	public String getIndexPage() {

		return "remindme/index.html";
	}

}