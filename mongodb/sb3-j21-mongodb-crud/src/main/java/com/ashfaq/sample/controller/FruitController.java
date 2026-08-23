package com.ashfaq.sample.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ashfaq.sample.entity.Fruit;
import com.ashfaq.sample.service.FruitService;

@RestController
@RequestMapping("/api/fruits")
public class FruitController {

	@Autowired
	private FruitService fruitService;

	@GetMapping
	public List<Fruit> getAllFruits() {
		return fruitService.getAllFruits();
	}

	@PostMapping
	public Fruit createFruit(@RequestBody Fruit fruit) {
		return fruitService.createFruit(fruit);
	}

	@PutMapping("/{id}")
	public ResponseEntity<Fruit> updateFruit(@PathVariable String id, @RequestBody Fruit fruitDetails) {
		Fruit updatedFruit = fruitService.updateFruit(id, fruitDetails);
		return ResponseEntity.ok(updatedFruit);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteFruit(@PathVariable String id) {
		fruitService.deleteFruit(id);
		return ResponseEntity.noContent().build();
	}
}