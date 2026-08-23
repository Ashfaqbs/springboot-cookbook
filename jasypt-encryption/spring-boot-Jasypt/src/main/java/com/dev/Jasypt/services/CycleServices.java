/**
 * Code developed by Ashfaq (© [Year])
 * GitHub: https://github.com/DarkSharkAsh
 */

package com.dev.Jasypt.services;

import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dev.Jasypt.controller.CycleController;
import com.dev.Jasypt.dao.CycleDAO;
import com.dev.Jasypt.model.Cycle;

@Service
public class CycleServices {

	@Autowired
	private CycleDAO cycleDAO;
	private static final Logger logger = LogManager.getLogger(CycleController.class);

//	public List<Cycle> getAllCyles() {
//		return cycleDAO.findAll().isEmpty() ? cycleDAO.findAll() : cycleDAO.findAll();
//	}
	// Updated one from X
	public List<Cycle> getAllCyles() {
		List<Cycle> cycles = cycleDAO.findAll();
		if (cycles.isEmpty()) {
			logger.info("No cycles found.");
		} else {
			logger.info("Found {} cycles.", cycles.size());
		}
		return cycles;
	}

//	public Cycle getCyclesById(int id) {
//
//		Cycle cycle = null;
//		try {
//
//			cycle = cycleDAO.findById(id).get();
//
//		} catch (Exception e) {
//			// TODO: handle exception
//			System.out.println("Object not found");
//		}
//		return cycle;
//
//	}

	public Cycle getCyclesById(int id) {
		Optional<Cycle> cycle = cycleDAO.findById(id);
		if (cycle.isPresent()) {
			logger.info("Found cycle with ID: {}", id);
			return cycle.get();
		} else {
			logger.warn("Cycle with ID {} not found.", id);
			return null;
		}
	}

//	public Cycle addCycle(Cycle cycle) {
//
//		Cycle saveCycle = null;
//		try {
//
//			saveCycle = cycleDAO.save(cycle);
//
//		} catch (Exception e) {
//			// TODO: handle exception
//			System.out.println("Object not Saved");
//		}
//		return saveCycle;
//
//	}
	public Cycle addCycle(Cycle cycle) {
		try {
			Cycle savedCycle = cycleDAO.save(cycle);
			logger.info("Added a new cycle with ID: {}", savedCycle.getId());
			return savedCycle;
		} catch (Exception e) {
			logger.error("Error occurred while adding a cycle: {}", e.getMessage());
			return null;
		}
	}

//	public boolean deleteCycle(int cycleID) {
//		boolean response = false;
//		try {
//			cycleDAO.deleteById(cycleID);
//			response = true;
//
//		} catch (Exception e) {
//			// TODO: handle exception
//			System.out.println("Object not deleted");
//		}
//		return response;
//
//	}
	public boolean deleteCycle(int cycleID) {
		try {
			cycleDAO.deleteById(cycleID);
			logger.info("Deleted cycle with ID: {}", cycleID);
			return true;
		} catch (Exception e) {
			logger.error("Error occurred while deleting a cycle with ID {}: {}", cycleID, e.getMessage());
			return false;
		}
	}

//	public List<Cycle> getCyclesByBrand(String brand) {
//		List<Cycle> cycles = cycleDAO.findByCyclebrand(brand);
//		return cycles;
//	}
//	

	public List<Cycle> getCyclesByBrand(String brand) {
		List<Cycle> cycles = cycleDAO.findByCyclebrand(brand);
		if (cycles.isEmpty()) {
			logger.info("No cycles found for brand: {}", brand);
		} else {
			logger.info("Found {} cycles for brand: {}", cycles.size(), brand);
		}
		return cycles;
	}
}
