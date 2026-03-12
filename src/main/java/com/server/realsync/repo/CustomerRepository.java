/**
 * 
 */
package com.server.realsync.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.server.realsync.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    List<Customer> findByAccountId(Integer accountId);

    Optional<Customer> findByAccountIdAndMobile(Integer accountId, String mobile);

    List<Customer> findByNameContainingIgnoreCase(String name);

}