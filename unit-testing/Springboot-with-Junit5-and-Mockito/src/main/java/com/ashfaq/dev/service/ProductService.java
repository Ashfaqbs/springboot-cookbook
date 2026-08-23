package com.ashfaq.dev.service;


import java.util.List;

import com.ashfaq.dev.model.Product;

public interface ProductService {
    Product createProduct(Product product);
    Product getProductById(Long id);
    List<Product> getAllProducts();
    Product updateProduct(Long id, Product product);
    void deleteProduct(Long id);
    
    
    
    List<Product> getProductsByName(String name);
    List<Product> getProductsByPriceRange(Double minPrice, Double maxPrice);
}

