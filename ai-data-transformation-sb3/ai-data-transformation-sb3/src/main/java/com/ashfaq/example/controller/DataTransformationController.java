package com.ashfaq.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ashfaq.example.service.DataTransformationService;

@RestController
@RequestMapping("/api/data-transform")
public class DataTransformationController {

	@Autowired
	private DataTransformationService dataTransformationService;

	@PostMapping("/validate-transform")
	public ResponseEntity<String> validateAndTransform(@RequestBody TransformationRequest request) {
		String response = dataTransformationService.validateAndTransformData(request.getSourceData(),
				request.getTargetSchema());
		return ResponseEntity.ok(response);
	}

	// DTO class for the request
	public static class TransformationRequest {
		private String sourceData;
		private String targetSchema;

		// Getters and Setters
		public String getSourceData() {
			return sourceData;
		}

		public void setSourceData(String sourceData) {
			this.sourceData = sourceData;
		}

		public String getTargetSchema() {
			return targetSchema;
		}

		public void setTargetSchema(String targetSchema) {
			this.targetSchema = targetSchema;
		}
	}
}
