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

	@GetMapping({"/login.html", "/signin.html"})
	public String getLogin(@RequestParam(value = "error", required = false) String error, Model model) {
		if ("true".equals(error)) {
			model.addAttribute("errorMessage", "Invalid username or password");
		}
		return "remindme/login";
		// return "realsync/index";
	}

	@GetMapping("/signup.html")
	public String getRegister(@RequestParam(value = "refAccId", required = false) String refAccId,
			Model model) {

		model.addAttribute("refAccId", refAccId);
		return "remindme/signup";
	}

	@GetMapping("/dashboard.html")
	public String getAdminDashboard(Model model) {

		return "remindme/dashboard";
	}
	
	@GetMapping("/reports.html")
	public String getAdminReport(Model model) {

		return "remindme/reports";
	}

	@GetMapping("/dashboard1.html")
	public String getDashboard(Model model) {

		User user = SecurityUtil.getLoggedInUser();
		model.addAttribute("userRole", user.getRole().getName());

		Account account = SecurityUtil.getCurrentAccountId();
		Pageable pageable = PageRequest.of(0, 100, Sort.by("visitDate").descending());

		boolean isAdminOrManager = user.getRole().getName().equalsIgnoreCase("Admin")
				|| user.getRole().getName().equalsIgnoreCase("Manager");

		String role = user.getRole().getName();
		long leadCount;
		long visitCount;

		model.addAttribute("totalPromptCount", 0);
		model.addAttribute("imagePromptCount", 0);
		model.addAttribute("videoPromptCount", 0);

		model.addAttribute("leadCount", 0);
		model.addAttribute("propertyCount", 0);

		return "realsync/rs/dashboard";
	}

	@GetMapping("/users.html")
	public String getUsers(Model model) {

		Account account = SecurityUtil.getCurrentAccountId();
		//
		model.addAttribute("userCount", userService.countByAccountId(account.getId()));
		model.addAttribute("adminCount", userService.countByRoleId(1l));
		model.addAttribute("sourceCount", userService.countByRoleId(2l));
		model.addAttribute("managerCount", userService.countByRoleId(3l));
		//
		model.addAttribute("userList", userService.getUsersByAccountId(account.getId()));
		//
		User user = SecurityUtil.getLoggedInUser();
		model.addAttribute("userRole", user.getRole().getName());
		return "realsync/rs/users";
	}

	@GetMapping("/user-create.html")
	public String getUserCreate(Model model, @RequestParam(required = false) Long id) {

		User usr = null;

		if (id != null && id > 0) {
			Optional<User> userOpt = userService.findUserById(id);
			if (userOpt.isPresent()) {
				usr = userOpt.get();
			}
		}

		model.addAttribute("user", usr != null ? usr : null);

		User user = SecurityUtil.getLoggedInUser();
		model.addAttribute("userRole", user.getRole().getName());
		return "realsync/rs/user-create";
	}

	@GetMapping("/user-view.html")
	public String viewUser(Model model, @RequestParam Long id) {
		Optional<User> userOpt = userService.findUserById(id);

		if (userOpt.isEmpty()) {
			// Redirect to list page or show error
			return "redirect:/users.html";
		}

		User usr = userOpt.get();
		model.addAttribute("user", usr);

		User user = SecurityUtil.getLoggedInUser();
		model.addAttribute("userRole", user.getRole().getName());

		return "realsync/rs/user-view";
	}

	@GetMapping("/settings.html")
	public String getSettings(Model model) {

		User user = SecurityUtil.getLoggedInUser();
		model.addAttribute("userRole", user.getRole().getName());
		model.addAttribute("user", user);
		return "realsync/rs/settings";
	}

	@GetMapping("index.html")
	public String getIndexPage() {

		return "remindme/index.html";
	}

}