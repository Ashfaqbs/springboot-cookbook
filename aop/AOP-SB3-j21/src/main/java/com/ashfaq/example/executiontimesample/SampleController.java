package com.ashfaq.example.executiontimesample;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SampleController {

	@Autowired
	OrderService orderService;

	@GetMapping("/order")
	public void processOrder() {
		orderService.processOrder();
	}

//	curl http://localhost:8080/order
//	Processing order...
//	Order processed.
//	void com.ashfaq.example.executiontimesample.OrderService.processOrder() executed in 3001ms

}
