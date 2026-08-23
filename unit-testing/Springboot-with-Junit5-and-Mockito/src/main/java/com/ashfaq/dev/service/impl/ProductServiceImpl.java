package com.ashfaq.dev.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ashfaq.dev.model.Product;
import com.ashfaq.dev.repository.ProductRepository;
import com.ashfaq.dev.service.ProductService;

@Service
public class ProductServiceImpl implements ProductService {

	@Autowired
	private ProductRepository productRepository;

	@Override
	public Product createProduct(Product product) {
		return productRepository.save(product);
	}

	@Override
	public Product getProductById(Long id) {
		return productRepository.findById(id).orElse(null);
	}

	@Override
	public List<Product> getAllProducts() {
		return productRepository.findAll();
	}

	@Override
	public Product updateProduct(Long id, Product product) {
		if (productRepository.existsById(id)) {
			product.setId(id);
			return productRepository.save(product);
		}
		return null;
	}

	@Override
	public void deleteProduct(Long id) {
		productRepository.deleteById(id);
	}
	
	
	
	@Override
	public List<Product> getProductsByName(String name) {
		return productRepository.findByName(name);
	}
	
	
	
	@Override
	public List<Product> getProductsByPriceRange(Double minPrice, Double maxPrice) {
        return productRepository.findByPriceBetween(minPrice, maxPrice);
	}
}
