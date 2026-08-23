package com.ashfaq.dev.cache;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.ashfaq.dev.model.Product;
import com.ashfaq.dev.service.ProductServiceImpl;

import jakarta.annotation.PostConstruct;

@Configuration
@EnableCaching
@EnableScheduling
public class CacheConfig {

	
	@Autowired
	private CacheManager cacheManager;
	
	@Autowired
	private ProductServiceImpl productService;
	
	
	
	//so now for the frist time we dont want to call the DB , so we have to to load all the data from the database and then add it to the cache 
	@PostConstruct//this is for the first time when we load the application  this function will be called
	public void preloadCache() {
		
		Cache cache = cacheManager.getCache("applicationCache");
		
		System.out.println("****** Initializing Cache");
		
		List<Product> productList = productService.getAllProducts();
		
		for (Product Product : productList) {
			cache.put(Product.getId(), Product);
		}
		
		System.out.println("****** Cache Initialized");
	}
	
	
	
	@Scheduled(fixedRate=15000, initialDelay=15000)
	public void clearCache() {
		System.out.println("****** clearing the Cache");
		cacheManager.getCacheNames().stream().forEach(
				name -> cacheManager.getCache(name).clear()
				);
	}
	
	
	
	
	
	
	
}
