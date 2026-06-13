package com.server.realsync.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.server.realsync.entity.CreditTransaction;
import com.server.realsync.repo.CreditTransactionRepository;

@Service
public class CreditTransactionService {

    @Autowired
    private CreditTransactionRepository repository;

    public List<CreditTransaction> findAll() {
        return repository.findAll();
    }

    public List<CreditTransaction> findByAccountId(Integer accountId) {
        return repository.findByAccountId(accountId);
    }

    @Transactional
    public CreditTransaction save(CreditTransaction transaction) {
        return repository.save(transaction);
    }

    @Transactional
    public void createTransaction(Integer accountId, Integer accountPlanId, String type, Double credits, Double balanceAfter, String remarks) {
        CreditTransaction transaction = new CreditTransaction();
        transaction.setAccountId(accountId);
        transaction.setAccountPlanId(accountPlanId);
        transaction.setType(type);
        transaction.setCredits(credits);
        transaction.setBalanceAfter(balanceAfter);
        transaction.setRemarks(remarks);
        repository.save(transaction);
    }
}
