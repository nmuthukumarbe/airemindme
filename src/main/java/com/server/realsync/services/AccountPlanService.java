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
import com.server.realsync.repo.CreditTransactionRepository;
import com.server.realsync.entity.CreditTransaction;
import com.server.realsync.util.GmailSender;

@Service
public class AccountPlanService {

    @Autowired
    private AccountPlanRepository repository;
    
    @Autowired
    private CreditTransactionRepository creditTransactionRepository;
    
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
    

    @org.springframework.transaction.annotation.Transactional
    public AccountPlan updateAccountPlanUsage(AccountPlan accountPlan, Account account, Integer planId) {
		if (planId != null) {
			planService.findById(planId).ifPresent(accountPlan::setPlan);
		}

		String planName = (accountPlan.getPlan() != null) ? accountPlan.getPlan().getName() : "Starter";
		double credits = 50.0;
		if ("Growth".equalsIgnoreCase(planName)) {
			credits = 150.0;
		} else if ("Business".equalsIgnoreCase(planName)) {
			credits = 300.0;
		}

		accountPlan.setTotalCredits(credits);
		accountPlan.setBalance(credits);
		accountPlan.setStartDate(LocalDate.now(ZoneId.of("Asia/Kolkata")));
		accountPlan.setEndDate(LocalDate.now(ZoneId.of("Asia/Kolkata")).plusDays(30));
		AccountPlan saved = repository.save(accountPlan);

		// Insert CreditTransaction
		CreditTransaction transaction = new CreditTransaction();
		transaction.setAccountId(account.getId());
		transaction.setAccountPlanId(saved.getId());
		transaction.setType("PLAN_RENEWED");
		transaction.setCredits(credits);
		transaction.setBalanceAfter(credits);
		transaction.setRemarks(planName + " Plan Renewed");
		creditTransactionRepository.save(transaction);

		return saved;
	}
   


}
