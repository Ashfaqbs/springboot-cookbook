package com.ashfaq.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ashfaq.example.service.ComplexDataTransformationService;

@RestController
@RequestMapping("/api/complex-data-transform")
public class ComplexDataTransformationController {

	@Autowired
	private ComplexDataTransformationService transformationService;

	@PostMapping("/validate-transform")
	public ResponseEntity<String> validateAndTransform(@RequestBody TransformationRequest request) {
		String response = transformationService.validateAndTransformData(request.getSourceData(),
				request.getTargetSchema());
		return ResponseEntity.ok(response);
	}

	public static class TransformationRequest {
		private String sourceData;
		private String targetSchema;

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
