package com.ashfaq.sample.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ashfaq.sample.entity.Fruit;
import com.ashfaq.sample.repo.FruitRepository;

@Service
public class FruitService {

    @Autowired
    private FruitRepository fruitRepository;

    public List<Fruit> getAllFruits() {
        return fruitRepository.findAll();
    }

    public Fruit createFruit(Fruit fruit) {
        return fruitRepository.save(fruit);
    }

    public Fruit updateFruit(String id, Fruit fruitDetails) {
        Fruit fruit = fruitRepository.findById(id).orElseThrow(() -> new RuntimeException("Fruit not found"));
        fruit.setName(fruitDetails.getName());
        fruit.setOrigin(fruitDetails.getOrigin());
        fruit.setPrice(fruitDetails.getPrice());
        return fruitRepository.save(fruit);
    }

    public void deleteFruit(String id) {
        fruitRepository.deleteById(id);
    }
}