package com.ashfaq.sample.repo;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.ashfaq.sample.entity.Fruit;

public interface FruitRepository extends MongoRepository<Fruit, String> {
}