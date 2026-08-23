package com.ashfaq.dev.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.ashfaq.dev.model.Product;
import com.ashfaq.dev.repository.ProductRepository;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

//    @Cacheable(value = "products cache", key = "#id")//the cacheble items should be unique so id we have given
//    @Override
//    public Optional<Product> getProductById(Long id) {
//        return productRepository.findById(id);
//    }
//    ** here for above meth we have given the name for value as products cache 
    
    @Cacheable(value = "applicationCache", key = "#id")//the cacheble items should be unique so id we have given and the name is taken from the cacheconfig
    //and to load the data when the app starts and when api called no called to the DB
    @Override
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
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
        } else {
            throw new RuntimeException("Product not found");
        }
    }

    @Override
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
    
    @Override
    public Product patchProduct(Long id, Map<String, Object> updates) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            updates.forEach((key, value) -> {
                switch (key) {
                    case "name":
                        product.setName((String) value);
                        break;
                    case "description":
                        product.setDescription((String) value);
                        break;
                    case "price":
                        product.setPrice((Double) value);
                        break;
                    case "quantity":
                        product.setQuantity((Integer) value);
                        break;
                    case "category":
                        product.setCategory((String) value);
                        break;
                }
            });
            return productRepository.save(product);
        } else {
            throw new RuntimeException("Product not found");
        }
    }
   
    
    //will be mapped to controller to be called to clear cache
    @CacheEvict(value="applicationCache", allEntries=true)//so when this is called the data is loaded from the DB when the app starts will be cleared
    //and again 1st time it will call the DB when app loads and again no calls to DB all the data will  be stored in cache unless we call this function
	public void clearAllCache() {
		System.out.println("**** All cache evicted ***");
	}
    
    
    @CacheEvict(value="applicationCache", key="#id")
	public void clearDataFromCache(Long id) {
		System.out.println("**** Data evicted from Cache : "+id);
	}
    
}
