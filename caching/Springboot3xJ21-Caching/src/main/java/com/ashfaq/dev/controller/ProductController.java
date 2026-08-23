package com.ashfaq.dev.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ashfaq.dev.model.Product;
import com.ashfaq.dev.service.ProductService;
import com.ashfaq.dev.service.ProductServiceImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Product API", description = "curd Operations on products API")

public class ProductController {

    @Autowired
    private ProductService productService;
    
    @Autowired
    private ProductServiceImpl productServiceimpl;//just to call the clearcache

    @PostMapping
    public Product createProduct(@RequestBody Product product) {
        return productService.createProduct(product);
    }

    @Operation(summary = "Get a product by ID", description = "Endpoint to retrieve a product by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the product."),
            @ApiResponse(responseCode = "404", description = "product not found.")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@Parameter(description = "ID of the product to retrieve") @PathVariable Long id) {
        Optional<Product> product = productService.getProductById(id);
        return product.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        try {
            Product updatedProduct = productService.updateProduct(id, product);
            return ResponseEntity.ok(updatedProduct);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
    
    
    @PatchMapping("/{id}")
    public ResponseEntity<Product> patchProduct(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        try {
            Product updatedProduct = productService.patchProduct(id, updates);
            return ResponseEntity.ok(updatedProduct);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    
    @RequestMapping(method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> options() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Allow", "GET, POST, PUT, DELETE, PATCH, OPTIONS");
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }
    
    
//    This informs you that the GET, POST, PUT, DELETE, PATCH, and OPTIONS methods are supported by the /api/products endpoint. This can be particularly useful for clients to understand what actions they can perform on the endpoint without making actual requests.


    
    //Headers
    
    @GetMapping("/exampleone")
    public ResponseEntity<String> exampleEndpoint(HttpServletResponse response) {
        response.addHeader("Custom-Header", "CustomValue");
        return ResponseEntity.ok("Header added to response");
    }
    @GetMapping("/exampletwo")
    public ResponseEntity<String> exampleEndpoint() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Custom-Header", "CustomValue");
        return new ResponseEntity<>("Header added to response", headers, HttpStatus.OK);
    }


    

	@GetMapping("/prod/clearAllCache")
	public String clearAllCache() {
		productServiceimpl.clearAllCache();
		return "Data cleared from cache";
	}
	
	
	@GetMapping("/prod/clearAllCache/{id}")
	public String clearDataFromCache(@PathVariable long id) {
		productServiceimpl.clearDataFromCache(id);
		return id+" cleared from cache";
	}
	




}
