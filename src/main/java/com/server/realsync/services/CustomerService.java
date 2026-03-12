package com.server.realsync.services;


import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.server.realsync.entity.Customer;
import com.server.realsync.repo.CustomerRepository;

/**
 * 
 */

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer save(Customer customer) {
        return customerRepository.save(customer);
    }

    public List<Customer> getAllByAccount(Integer accountId) {
        return customerRepository.findByAccountId(accountId);
    }

    public Optional<Customer> getById(Integer id) {
        return customerRepository.findById(id);
    }

    public Optional<Customer> findByMobile(Integer accountId, String mobile) {
        return customerRepository.findByAccountIdAndMobile(accountId, mobile);
    }

    public void delete(Integer id) {
        customerRepository.deleteById(id);
    }
}