/**
 * 
 */
package com.server.realsync.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.server.realsync.entity.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
	
}
