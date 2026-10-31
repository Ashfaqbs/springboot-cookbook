package com.ashfaq.example.lifecyclemethods.eg1;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Service
public class ProductService {

	@Autowired
	private ProductRepository productRepository;

	// Constructor: Just for object creation but its empty 
	public ProductService() {
		System.out.println("Constructor: ProductService created");
	}

	// @PostConstruct: Called after the bean is fully initialized
	@PostConstruct
	public void loadInitialData() { //the already created object will get its variables initialized here
		System.out.println("PostConstruct: Loading initial products into the database");

		// Check if there are any products; if not, load some default data
		if (productRepository.count() == 0) {
			Product product1 = new Product(1L, "Laptop", 800.0);
			Product product2 = new Product(2L, "Smartphone", 500.0);
			productRepository.saveAll(List.of(product1, product2));
			System.out.println("PostConstruct: Default products loaded");
		}
	}

	// CRUD methods
	public List<Product> getAllProducts() {
		return productRepository.findAll();
	}

	public Product getProductById(Long id) {
		return productRepository.findById(id).orElse(null);
	}

	public Product createProduct(Product product) {
		return productRepository.save(product);
	}

	public void deleteProduct(Long id) {
		productRepository.deleteById(id);
	}

	// @PreDestroy: Called just before the bean is destroyed (e.g., on app shutdown)
	@PreDestroy
	public void cleanUp() {
		System.out.println("PreDestroy: Cleaning up resources before shutdown");
		// Here, you could flush data to the database or close resources manually
	}
}
