### Simple Examples

URL : localhost:8080/api/data-transform/validate-transform

EG 1




{
  "sourceData": "{\"name\": \"John\", \"age\": 30, \"email\": \"john.doe@example.com\"}",
  "targetSchema": "{\"name\": \"string\", \"age\": \"integer\", \"email\": \"string\"}"
}




Successfully transformed data:  Based on the provided source data and target schema, here's the cleaned data:

{"name": "John", "age": 30, "email": "john.doe@example.com"}

Transformed to match the target schema:

{"name": "John", "age": 30, "email": "\"john.doe@example.com\""}

Here's an explanation of the transformation:
- The source data is already in a valid JSON format.
- In the target schema, "name" and "email" are defined as strings, and "age" is defined as an integer.
- To match the target schema, we keep the "name" value as a string and leave it unchanged.
- For the "age" value, since it's already an integer in the source data, no transformation is needed.
- However, for the "email" value, we need to wrap it with double quotes to make it a string according to the target schema. Therefore, we add the outer double quotes around the email value.




EG 2

{
  "sourceData": "{\"name\": \"Bob\", \"email\": \"bob.com\"}",
  "targetSchema": "{\"name\": \"string\", \"age\": \"integer\", \"email\": \"string\"}"
}


Not able to convert:  Based on the provided source data and target schema, there are some issues that need to be addressed before the source data can be considered as matching or convertible to the target schema:

1. The source data is missing the 'age' key-value pair, which is present in the target schema.
2. The value of the 'email' key in the source data is not in the format expected by the target schema. In the target schema, email is defined as a string type, but the value provided in the source data ('bob.com') appears to be missing the '@' symbol and the domain extension, which are necessary parts of a valid email address.

Therefore, to validate or convert the source data to match the target schema, the following steps need to be taken:

1. Add the 'age' key-value pair to the source data with an appropriate integer value.
2. Correct the format of the 'email' value in the source data by adding the '@' symbol and the domain extension (e.g., '@example.com'). For example, if Bob's email address is 'bob', then the corrected value would be 'bob@example.com'.

Once these steps have been taken to modify the source data to match the target schema, the conversion or validation can be considered successful. If the source data cannot be modified to meet the requirements of the target schema, then it may not be possible to convert or validate it at all.



## Complex Examples:

eg 1

URL:http://localhost:8080/api/complex-data-transform/validate-transform

{
  "sourceData": "{\"orderId\":\"ORD12345\",\"customer\":{\"customerId\":\"CUST6789\"},\"items\":[{\"itemId\":\"ITEM111\",\"quantity\":\"2\"}]},\"targetSchema\":{\"orderId\":\"string\",\"customer\":{\"customerId\":\"string\",\"email\":\"string\"},\"items\":[{\"itemId\":\"string\",\"quantity\":\"integer\",\"price\":\"float\"}],\"orderDate\":\"date\",\"totalAmount\":\"float\"}",
  "targetSchema": "{\"orderId\":\"string\",\"customer\":{\"customerId\":\"string\",\"email\":\"string\"},\"items\":[{\"itemId\":\"string\",\"quantity\":\"integer\",\"price\":\"float\"}],\"orderDate\":\"date\",\"totalAmount\":\"float\"}"
}


Successfully transformed data:  Based on the provided source data and target schema, here's how you can transform the data:

First, let's cast the types for the fields that have specific types in the target schema:
- orderId: string (already a string in the source data)
- customer.customerId: string (already a string in the source data)
- items[].itemId: string (already strings in the source data)
- totalAmount: float (the source data doesn't have this field, so we'll add it as a default value of 0)

Next, let's add missing fields in the target schema and set their default values:
- customer.email: string (missing in the source data, so we'll set it to an empty string)
- items[].price: float (missing in the source data, so we'll add it as a default value of 0)

Now, let's ensure that nested structures align with the target schema:
- The customer object has the correct fields (customerId and email), but no changes are needed since they already match the target schema
- The items array needs to have 'quantity' as an integer and 'price' as a float. Since the source data only has 'quantity', we'll add 'price' as a new field with a default value of 0

With these transformations, the resulting transformed data will look like:

```json
{
  "orderId": "ORD12345",
  "customer": {
    "customerId": "CUST6789",
    "email": ""
  },
  "items": [
    {
      "itemId": "ITEM111",
      "quantity": 2,
      "price": 0
    }
  ],
  "orderDate": null, // not provided in the source data, so it will be null
  "totalAmount": 0 // added as a new field with default value of 0
}
```






Let's make the data transformation logic more **complex and realistic** for enterprise-level scenarios. We'll incorporate:  

1. **Nested Data Structures**  
2. **Data Enrichment**: Add default values if missing.  
3. **Conditional Transformations**: Apply transformations based on specific conditions.  
4. **Type Casting with Validation**: Ensure correct type conversion (e.g., string to date, integer to float).  
5. **Error Aggregation**: Return detailed validation and transformation error messages.  

### Problem: Complex Data Transformation  

**Scenario**:  
- **Source Data**: A nested JSON representing a Kafka event for processing customer orders.  
- **Target Schema**: A nested schema used for storing the processed order in a database.  

The API should:  
1. Validate if the source data conforms to the target schema.  
2. Transform the source data to match the schema, including nested objects and arrays.  
3. Enrich data (e.g., add missing fields with default values).  
4. Return success or failure with detailed errors if validation/transformation fails.  

---

### Complex Target Schema Example  

#### **Target Schema**:  
```json
{
  "orderId": "string",
  "customer": {
    "customerId": "string",
    "email": "string"
  },
  "items": [
    {
      "itemId": "string",
      "quantity": "integer",
      "price": "float"
    }
  ],
  "orderDate": "date",
  "totalAmount": "float"
}
```

---

### Step 1: Service Class  

```java
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class ComplexDataTransformationService {

    @Autowired
    private OllamaChatClient chatClient;

    public String validateAndTransformData(String sourceData, String targetSchema) {
        // Step 1: Validate Source Data Against Target Schema
        String validationPrompt = String.format(
            "Validate the source data against the provided nested target schema. "
            + "Identify missing fields, incorrect types, or invalid values. "
            + "Source Data: %s\nTarget Schema: %s\nProvide a detailed validation report.",
            sourceData.trim(),
            targetSchema.trim()
        );

        String validationResponse = chatClient.call(validationPrompt);

        if (validationResponse.contains("cannot")) {
            return "Validation Failed: " + validationResponse;
        }

        // Step 2: Transform and Enrich Data
        String transformationPrompt = String.format(
            "Transform the source data to match the target schema, including type casting, "
            + "adding default values for missing fields, and ensuring nested structures align. "
            + "Source Data: %s\nTarget Schema: %s\nReturn the transformed data.",
            sourceData.trim(),
            targetSchema.trim()
        );

        String transformedData = chatClient.call(transformationPrompt);

        return "Successfully transformed data: " + transformedData;
    }
}
```

---

### Step 2: Controller  

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/complex-data-transform")
public class ComplexDataTransformationController {

    @Autowired
    private ComplexDataTransformationService transformationService;

    @PostMapping("/validate-transform")
    public ResponseEntity<String> validateAndTransform(@RequestBody TransformationRequest request) {
        String response = transformationService.validateAndTransformData(
            request.getSourceData(), request.getTargetSchema());
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
```

---

### Step 3: Sample Data and Test Cases  

#### Input 1: Source Data (Valid but Missing Some Fields)
```json
{
  "orderId": "ORD12345",
  "customer": {
    "customerId": "CUST6789"
  },
  "items": [
    {
      "itemId": "ITEM111",
      "quantity": "2"
    }
  ]
}
```

#### Target Schema
```json
{
  "orderId": "string",
  "customer": {
    "customerId": "string",
    "email": "string"
  },
  "items": [
    {
      "itemId": "string",
      "quantity": "integer",
      "price": "float"
    }
  ],
  "orderDate": "date",
  "totalAmount": "float"
}
```

---

#### Expected Success Transformation Response  
```json
{
  "message": "Successfully transformed data",
  "transformedData": {
    "orderId": "ORD12345",
    "customer": {
      "customerId": "CUST6789",
      "email": "unknown@example.com"
    },
    "items": [
      {
        "itemId": "ITEM111",
        "quantity": 2,
        "price": 0.0
      }
    ],
    "orderDate": "2025-01-13",
    "totalAmount": 0.0
  }
}
```

---

#### Input 2: Source Data (Invalid)  
```json
{
  "orderId": 12345,
  "customer": {
    "customerId": "CUST6789",
    "email": "invalid-email"
  },
  "items": [
    {
      "itemId": "ITEM111",
      "quantity": "two"
    }
  ]
}
```

#### Expected Failure Response  
```json
{
  "message": "Validation Failed",
  "issues": [
    "Field 'orderId' must be a string but received an integer.",
    "Field 'email' must be a valid email address.",
    "Field 'quantity' must be an integer but received 'two'."
  ]
}
```

---

### Step 4: API Testing and Edge Cases  

#### 1. **Missing Nested Fields**  
- Missing fields like `email` or `price`. Ensure default values are added.

#### 2. **Invalid Data Types**  
- Attempt to convert strings to integers or floats. Fail gracefully if conversion is not possible.

#### 3. **Incorrect Field Formats**  
- Example: Validate `orderDate` to ensure it's in the correct date format (e.g., `YYYY-MM-DD`).

#### 4. **Multiple Items in Arrays**  
- Transform and validate an array of `items` with diverse issues.

---

