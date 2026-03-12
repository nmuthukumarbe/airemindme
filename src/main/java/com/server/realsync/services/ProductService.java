/**
 * 
 */
package com.server.realsync.services;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import com.server.realsync.entity.Product;
import com.server.realsync.repo.ProductRepository;

@Service
public class ProductService {

	@Autowired
    private ProductRepository productRepository;

    public Product save(Product product) {
        return productRepository.save(product);
    }

    public List<Product> getByAccount(Integer accountId) {
        return productRepository.findByAccountId(accountId);
    }

    public void delete(Integer id) {
        productRepository.deleteById(id);
    }
}