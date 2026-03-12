package com.server.realsync.services;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.server.realsync.dto.AccountPlanUsageDto;
import com.server.realsync.entity.Account;
import com.server.realsync.entity.AccountPlan;
import com.server.realsync.entity.Invoice;
import com.server.realsync.repo.AccountPlanRepository;
import com.server.realsync.util.GmailSender;

@Service
public class AccountPlanService {

    @Autowired
    private AccountPlanRepository repository;
    
    @Autowired
    private PlanService planService;
    
    
    @Autowired 
    private GmailSender gmailSender;
    
    @Autowired
    private InvoiceService invoiceService;

    public List<AccountPlan> findAll() {
        return repository.findAll();
    }

    public Optional<AccountPlan> findById(Integer id) {
        return repository.findById(id);
    }

    public AccountPlan save(AccountPlan accountPlan) {
        return repository.save(accountPlan);
    }

    public void deleteById(Integer id) {
        repository.deleteById(id);
    }
    
    public Optional<AccountPlan> getAccountPlanUsage(Integer accountId) {
        return repository.findByAccountId(accountId);
    }
    
    private AccountPlanUsageDto mapToUsageDto(AccountPlan accountPlan) {
        AccountPlanUsageDto dto = new AccountPlanUsageDto();
        dto.setStartDate(accountPlan.getStartDate());
        dto.setEndDate(accountPlan.getEndDate());
        return dto;
    }
    

    public AccountPlan updateAccountPlanUsage(AccountPlan accountPlan, Account account, Integer planId) {
		//Invoice invoice = new Invoice();
		//invoice.setAccountId(account.getId());
		

		if (planId == 1) {
			accountPlan.setBalance(accountPlan.getBalance()+999.0);
			accountPlan.setPlan(planService.findById(1).get());
			accountPlan.setStartDate(LocalDate.now(ZoneId.of("Asia/Kolkata")));
			accountPlan.setEndDate(LocalDate.now(ZoneId.of("Asia/Kolkata")).plusMonths(2));
			//invoice.setAmount(999.0);
			//invoice.setPlanId(1);
			//transaction.setTransactionAmount(999.0);
		} else if (planId == 2) {
			accountPlan.setBalance(accountPlan.getBalance()+4999.0);
			accountPlan.setPlan(planService.findById(2).get());
			accountPlan.setStartDate(LocalDate.now(ZoneId.of("Asia/Kolkata")));
			accountPlan.setEndDate(LocalDate.now(ZoneId.of("Asia/Kolkata")).plusMonths(12));
			//invoice.setAmount(4999.0);
			//invoice.setPlanId(2);
			//transaction.setTransactionAmount(4999.0);
		} 
		//transaction.setBalanceAfterTransaction(accountPlan.getBalance());
		//transaction.setAccountPlanId(accountPlan.getId());
		//transaction.setTransactionDate(LocalDateTime.now(ZoneId.of("Asia/Kolkata")));
		//transaction.setTransactionDirection("CREDIT");
		//transaction.setTransactionType("RECHARGE");
		//this.createTransaction(transaction);
		//invoice.setStartDate(accountPlan.getStartDate());
		//invoice.setEndDate(accountPlan.getEndDate());
		//invoice.setModeOfPayment("GPAY");
		//invoiceService.saveInvoice(invoice);
		repository.save(accountPlan);
		//gmailSender.accounPaymentConfirmation(account, planId, invoice);
		return accountPlan;
	}
    
    public AccountPlan updateAccountPlanUsage(AccountPlan accountPlan, Account account, Integer planId,
			Invoice invoice) {

    	boolean isInvoiceNeeded = false;
		if (invoice == null) {
			invoice = new Invoice();
			isInvoiceNeeded = true;
			invoice.setMerchantOrderId("");
			invoice.setStatus("COMPLETED");
		}
		invoice.setAccountId(account.getId());
		
		if (planId == 1) {
			accountPlan.setBalance(accountPlan.getBalance()+4999.0);
			accountPlan.setPlan(planService.findById(1).get());
			accountPlan.setStartDate(LocalDate.now(ZoneId.of("Asia/Kolkata")));
			accountPlan.setEndDate(LocalDate.now(ZoneId.of("Asia/Kolkata")).plusMonths(12));
			invoice.setAmount(4999.0);
			invoice.setPlanId(1);
		} else if (planId == 2) {
			accountPlan.setBalance(accountPlan.getBalance()+7499.0);
			accountPlan.setPlan(planService.findById(2).get());
			accountPlan.setStartDate(LocalDate.now(ZoneId.of("Asia/Kolkata")));
			accountPlan.setEndDate(LocalDate.now(ZoneId.of("Asia/Kolkata")).plusMonths(12));
			invoice.setAmount(7499.0);
			invoice.setPlanId(2);
		} else if (planId == 3) {
			accountPlan.setBalance(accountPlan.getBalance()+9999.0);
			accountPlan.setPlan(planService.findById(3).get());
			accountPlan.setStartDate(LocalDate.now(ZoneId.of("Asia/Kolkata")));
			accountPlan.setEndDate(LocalDate.now(ZoneId.of("Asia/Kolkata")).plusMonths(12));
			invoice.setAmount(9999.0);
			invoice.setPlanId(3);
		} else if (planId == 4) {
			accountPlan.setBalance(accountPlan.getBalance()+14999.0);
			accountPlan.setPlan(planService.findById(4).get());
			accountPlan.setStartDate(LocalDate.now(ZoneId.of("Asia/Kolkata")));
			accountPlan.setEndDate(LocalDate.now(ZoneId.of("Asia/Kolkata")).plusMonths(12));
			invoice.setAmount(14999.0);
			invoice.setPlanId(4);
		} 
		repository.save(accountPlan);
		if (isInvoiceNeeded) {
			invoice.setStartDate(accountPlan.getStartDate());
			invoice.setModeOfPayment("GPAY");
			invoiceService.create(invoice);
		}
		//gmailSender.accounPaymentConfirmation(account, planId, invoice);
		return accountPlan;
	}
}
