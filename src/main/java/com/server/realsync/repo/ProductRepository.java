/**
 * 
 */
package com.server.realsync.repo;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.server.realsync.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    List<Product> findByAccountId(Integer accountId);
}