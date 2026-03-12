/**
 * 
 */
package com.server.realsync.services;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import com.server.realsync.entity.CustomerGroup;
import com.server.realsync.repo.CustomerGroupRepository;

/**
 * 
 */

@Service
public class CustomerGroupService {

    private final CustomerGroupRepository customerGroupRepository;

    public CustomerGroupService(CustomerGroupRepository customerGroupRepository) {
        this.customerGroupRepository = customerGroupRepository;
    }

    public CustomerGroup save(CustomerGroup group) {
        return customerGroupRepository.save(group);
    }

    public List<CustomerGroup> getByAccountId(Integer accountId) {
        return customerGroupRepository.findByAccountId(accountId);
    }

    public Optional<CustomerGroup> getById(Integer id) {
        return customerGroupRepository.findById(id);
    }

    public void delete(Integer id) {
        customerGroupRepository.deleteById(id);
    }

}