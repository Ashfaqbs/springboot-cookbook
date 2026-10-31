package com.ashfaq.example.controller;

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

import com.ashfaq.example.VehicleService.VehicleService;
import com.ashfaq.example.entity.Vehicle;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

	@Autowired
	private VehicleService vehicleService;

	// Custom counters for tracking total and rate of API calls
	private final Counter getVehicleByIdTotalCounter;
	private final Timer getVehicleByIdTimer;

	@Autowired
	public VehicleController(VehicleService vehicleService, MeterRegistry meterRegistry) {
		this.vehicleService = vehicleService;

		// Counter for total API calls
		this.getVehicleByIdTotalCounter = Counter.builder("api.get.vehicle.by.id.total")
				.description("Total number of calls to getVehicleById API").register(meterRegistry);

		// Timer for tracking call rate
		this.getVehicleByIdTimer = Timer.builder("api.get.vehicle.by.id.timer")
				.description("Timer for getVehicleById API call rate").publishPercentiles(0.5, 0.95) // Publish
																										// percentiles
																										// (optional)
				.register(meterRegistry);
	}

	@GetMapping
	public List<Vehicle> getAllVehicles() {
		return vehicleService.getAllVehicles();
	}

//    @GetMapping("/{id}")
//    public ResponseEntity<Vehicle> getVehicleById(@PathVariable Long id) {
//        return vehicleService.getVehicleById(id)
//            .map(ResponseEntity::ok)
//            .orElse(ResponseEntity.notFound().build());
//    }

	@GetMapping("/{id}")
	public ResponseEntity<Vehicle> getVehicleById(@PathVariable Long id) {
		getVehicleByIdTotalCounter.increment(); // Increment total counter

		// Timer to measure the duration and count calls per minute
		return getVehicleByIdTimer.record(() -> {
			return vehicleService.getVehicleById(id).map(ResponseEntity::ok).orElseGet(() -> {
				// Return a default Vehicle object if not found
				Vehicle defaultVehicle = new Vehicle();
				defaultVehicle.setId(-1L); // Example: set an ID to indicate a default object
				defaultVehicle.setMake("Default Vehicle"); // Example default values
				defaultVehicle.setModel("Default Model");
				defaultVehicle.setYear(0);
				// Add any other default properties here

				return ResponseEntity.ok(defaultVehicle);
			});
		});
	}

	@PostMapping
	public Vehicle createVehicle(@RequestBody Vehicle vehicle) {
		return vehicleService.saveVehicle(vehicle);
	}

	@PutMapping("/{id}")
	public ResponseEntity<Vehicle> updateVehicle(@PathVariable Long id, @RequestBody Vehicle vehicleDetails) {
		return ResponseEntity.ok(vehicleService.updateVehicle(id, vehicleDetails));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteVehicle(@PathVariable Long id) {
		vehicleService.deleteVehicle(id);
		return ResponseEntity.noContent().build();
	}
}
