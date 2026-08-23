package com.ashfaq.dev.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ashfaq.dev.model.Product;
import com.ashfaq.dev.model.ProductModel;
import com.ashfaq.dev.service.ProductService;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/hapi/products")
@Tag(name = "Haetoes", description = "Operations to understand Haetoes products API")

public class HaetoesController {

    @Autowired
    private ProductService productService;

    @PostMapping
    public ResponseEntity<EntityModel<ProductModel>> createProduct(@RequestBody Product product) {
        Product createdProduct = productService.createProduct(product);
        ProductModel productModel = toModel(createdProduct);

        EntityModel<ProductModel> entityModel = EntityModel.of(productModel);
        entityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(HaetoesController.class).getProductById(createdProduct.getId())).withSelfRel());

        return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(entityModel);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<ProductModel>> getProductById(@PathVariable Long id) {
        Optional<Product> product = productService.getProductById(id);
        ProductModel productModel = toModel(product.get());

        EntityModel<ProductModel> entityModel = EntityModel.of(productModel);
        entityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(HaetoesController.class).getProductById(id)).withSelfRel());
        entityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(HaetoesController.class).getAllProducts()).withRel("products"));

        return ResponseEntity.ok(entityModel);
    }

    @GetMapping
    public ResponseEntity<List<EntityModel<ProductModel>>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        List<EntityModel<ProductModel>> productModels = products.stream()
                .map(this::toModel)
                .map(productModel -> {
                    EntityModel<ProductModel> entityModel = EntityModel.of(productModel);
                    entityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(HaetoesController.class).getProductById(productModel.getId())).withSelfRel());
                    return entityModel;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(productModels);
    }

    // Other methods (PUT, DELETE, PATCH) would be implemented similarly

    private ProductModel toModel(Product product) {
        ProductModel productModel = new ProductModel();
        productModel.setId(product.getId());
        productModel.setName(product.getName());
        productModel.setDescription(product.getDescription());
        productModel.setPrice(product.getPrice());
        productModel.setQuantity(product.getQuantity());
        productModel.setCategory(product.getCategory());
        return productModel;
    }
}

