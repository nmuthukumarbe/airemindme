package com.server.realsync.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.server.realsync.dto.PasswordResetDto;
import com.server.realsync.entity.Account;
import com.server.realsync.entity.CustomUserDetails;
import com.server.realsync.entity.User;
import com.server.realsync.services.AccountService;
import com.server.realsync.services.UserService;


@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @Autowired
    private AccountService service;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public ResponseEntity<List<Account>> getAllAccounts() {
        List<Account> accounts = service.findAll();
        return new ResponseEntity<>(accounts, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Account> getAccountById(@PathVariable Integer id) {
        Optional<Account> account = service.findById(id);
        return account.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Account> createAccount(@RequestBody Account account) {
        Account savedAccount = service.save(account);
        return new ResponseEntity<>(savedAccount, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Account> updateAccount(@PathVariable Integer id, @RequestBody Account account) {
    	Account existingAccount = service.findById(id).get();
        if (existingAccount!=null) {
        	existingAccount.setName(account.getName());
            existingAccount.setMobile(account.getMobile());
            existingAccount.setEmail(account.getEmail());
            existingAccount.setAddress(account.getAddress());
            Account updatedAccount = service.save(existingAccount);
            return new ResponseEntity<>(updatedAccount, HttpStatus.OK);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Integer id) {
        if (service.findById(id).isPresent()) {
            service.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping("/resetAppPassword")
    public ResponseEntity<User> resetPassword(@RequestBody PasswordResetDto passwordResetDto) {
        User user = userService.findByUserId(passwordResetDto.getUserId());
    	if(passwordEncoder.matches(passwordResetDto.getCurrentPassword(),user.getPassword())) {
    		//
    		user.setPassword(new BCryptPasswordEncoder().encode(passwordResetDto.getNewPassword()));
    		userService.saveUser(user);
    		return new ResponseEntity<User>(user, HttpStatus.OK);
    	} else {
    		return new ResponseEntity<User>(user, HttpStatus.INSUFFICIENT_STORAGE);
    	}
    	
    }
    
    @PostMapping("/saveUser")
    public ResponseEntity<User> saveUser(@RequestBody User user) {
    	User existingUser = null;
    	try {
    		existingUser = userService.findByUsername(user.getUsername());
    	} catch(Exception e) {
    		System.out.println("User not found:"+user.getUsername());
    	}
    	if(existingUser == null) {
    		//create
    		user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
    		int accountId = 0;
    	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    	    if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
    	        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
    	        accountId = customUserDetails.getAccountId();
    	    }

    	    Account account = new Account();
    	    account.setId(accountId);
    		user.setAccount(account);
    		userService.saveUser(user);
    		return new ResponseEntity<User>(user, HttpStatus.OK);
    	} else {
    		//update
    		existingUser.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
    		existingUser.setFullName(user.getFullName());
    		existingUser.setRole(user.getRole());
    		existingUser.setEmail(user.getEmail());
    		userService.saveUser(existingUser);
    		return new ResponseEntity<User>(user, HttpStatus.OK);
    	}
    }
    
}
