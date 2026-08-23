package com.ashfaq.dev.service;


import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.ashfaq.dev.model.Product;

public interface ProductService {
    Product createProduct(Product product);
    Optional<Product> getProductById(Long id);
    List<Product> getAllProducts();
    Product updateProduct(Long id, Product product);
    void deleteProduct(Long id);
	Product patchProduct(Long id, Map<String, Object> updates);
}
