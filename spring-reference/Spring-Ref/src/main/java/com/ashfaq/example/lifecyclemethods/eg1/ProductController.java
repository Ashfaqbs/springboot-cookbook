package com.ashfaq.example.lifecyclemethods.eg1;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
public class ProductController {

	@Autowired
	private ProductService productService;

	@GetMapping
	public List<Product> getAllProducts() {
		return productService.getAllProducts();
	}

	@GetMapping("/{id}")
	public Product getProduct(@PathVariable Long id) {
		return productService.getProductById(id);
	}

	@PostMapping
	public Product createProduct(@RequestBody Product product) {
		return productService.createProduct(product);
	}

	@DeleteMapping("/{id}")
	public void deleteProduct(@PathVariable Long id) {
		productService.deleteProduct(id);
	}
}
