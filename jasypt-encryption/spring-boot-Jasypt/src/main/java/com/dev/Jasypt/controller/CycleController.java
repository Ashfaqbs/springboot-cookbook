/**
 * Code developed by Ashfaq (© [Year])
 * GitHub: https://github.com/DarkSharkAsh
 */

package com.dev.Jasypt.controller;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

import com.dev.Jasypt.model.Cycle;
import com.dev.Jasypt.services.CycleServices;



@RestController
@RequestMapping("/api")
public class CycleController {
	private static final Logger logger = LogManager.getLogger(CycleController.class);

	@Autowired
	private CycleServices cycleServices;
	
	

	@GetMapping("/getCycles")
	public ResponseEntity<List<Cycle>> getAllCycles() {
		List<Cycle> allCyles = null;
		try {
			logger.info("Request received to get all cycles.");
			allCyles = cycleServices.getAllCyles();
			logger.debug("Testing prometheus");//debug not working check the config
			return allCyles != null ? ResponseEntity.ok(allCyles) : ResponseEntity.noContent().build();

		} catch (Exception e) {
			logger.error("Error occurred while getting all cycles: {}", e.getMessage());
			return ResponseEntity.status(500).build();

		}

	}

	@GetMapping("/getCycle/{id}")
	public ResponseEntity<Cycle> getCyclesByID(@PathVariable("id") int id) {
		Cycle Cycle = null;
		try {
			logger.info("Request received to get cycle by ID: {}", id);
			Cycle = cycleServices.getCyclesById(id);
			return Cycle != null ? ResponseEntity.ok(Cycle) : ResponseEntity.noContent().build();

		} catch (Exception e) {
			// TODO: handle exception
			logger.error("Error occurred while getting cycle by ID {}: {}", id, e.getMessage());
			return ResponseEntity.status(500).build();

		}

	}

	@PostMapping("/addCycle")
	public ResponseEntity<Cycle> SaveCycle(@RequestBody Cycle cycle) {
		Cycle addedCycle = null;
		try {
			logger.info("Request received to add a new cycle.");
			addedCycle = cycleServices.addCycle(cycle);
			return addedCycle != null ? ResponseEntity.ok(addedCycle) : ResponseEntity.noContent().build();

		} catch (Exception e) {
			// TODO: handle exception
			logger.error("Error occurred while adding a new cycle: {}", e.getMessage());
			return ResponseEntity.status(500).build();

		}

	}

	@PutMapping("/addCycle/{id}")
	public ResponseEntity<Cycle> updateCycle(@PathVariable("id") int id, @RequestBody Cycle cycle) {
		try {
			logger.info("Request received to update cycle with ID: {}", id);

			// Retrieve the existing cycle by ID
			Cycle existingCycle = cycleServices.getCyclesById(id);

			if (existingCycle == null) {
				logger.error("Cycle with ID {} not found for update.", id);
				return ResponseEntity.notFound().build();
			}

			// Update the properties of the existing cycle with the new values
			existingCycle.setCycle_name(cycle.getCycle_name()); // Replace with actual property names
			existingCycle.setCycle_Cost(cycle.getCycle_Cost()); // Replace with actual property names
			existingCycle.setCycle_brand(cycle.getCycle_brand());

			// Save the updated cycle
			Cycle updatedCycle = cycleServices.addCycle(existingCycle);

			return updatedCycle != null ? ResponseEntity.ok(updatedCycle) : ResponseEntity.noContent().build();
		} catch (Exception e) {
			logger.error("Error occurred while updating cycle with ID {}: {}", id, e.getMessage());
			return ResponseEntity.status(500).build();
		}
	}

//	
//	@PutMapping("/updateCycle/{id}")
//	public ResponseEntity<Cycle> updateCycle(@PathVariable("id") int id, @RequestBody Cycle updatedCycle) {
//	    try {
//	        logger.info("Request received to update cycle with ID: {}", id);
//
//	        // Retrieve the existing cycle by ID
//	        Optional<Cycle> existingCycleOptional = cycleServices.getCycleById(id);
//
//	        if (!existingCycleOptional.isPresent()) {
//	            logger.error("Cycle with ID {} not found for update.", id);
//	            return ResponseEntity.notFound().build();
//	        }
//
//	        // Get the existing cycle object
//	        Cycle existingCycle = existingCycleOptional.get();
//
//	        // Update the properties of the existing cycle with the new values
//	        existingCycle.setProperty1(updatedCycle.getProperty1()); // Replace with actual property names
//	        existingCycle.setProperty2(updatedCycle.getProperty2()); // Replace with actual property names
//
//	        // Save the updated cycle
//	        Cycle savedCycle = cycleServices.addCycle(existingCycle);
//
//	        return ResponseEntity.ok(savedCycle);
//	    } catch (Exception e) {
//	        logger.error("Error occurred while updating cycle with ID {}: {}", id, e.getMessage());
//	        return ResponseEntity.status(500).build();
//	    }
//	}

//	

	@DeleteMapping("/getCycle/{id}")
	public ResponseEntity<Cycle> deleteCyclesByID(@PathVariable("id") int id) {
		boolean Cycle = false;
		try {
			logger.info("Request received to delete cycle with ID: {}", id);

			Cycle = cycleServices.deleteCycle(id);
			return Cycle ? ResponseEntity.noContent().build() : ResponseEntity.internalServerError().build();
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("Error occurred while deleting cycle with ID {}: {}", id, e.getMessage());
			return ResponseEntity.status(500).build();
		}

	}

	// Custom

	@GetMapping("/getCyclesByBrand/{brand}")
	public ResponseEntity<List<Cycle>> getCyclesByBrand(@PathVariable("brand") String brand) {
		try {
			logger.debug("Testing prometheus");
			logger.info("Request received to get cycles by brand: {}", brand);
			List<Cycle> cycles = cycleServices.getCyclesByBrand(brand);
			if (cycles != null && !cycles.isEmpty()) {
				logger.info("Found {} cycles for brand: {}", cycles.size(), brand);
				return ResponseEntity.ok(cycles);
			} else {
				logger.info("No cycles found for brand: {}", brand);
				return ResponseEntity.noContent().build();
			}
		} catch (Exception e) {
			logger.error("Error occurred while getting cycles by brand: {}", e.getMessage());
			return ResponseEntity.status(500).build();
		}
	}

}
