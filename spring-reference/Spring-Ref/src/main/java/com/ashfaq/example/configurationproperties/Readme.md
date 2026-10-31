The `@ConfigurationProperties` annotation in Spring Boot is used to map properties from external configuration files (like `application.properties` or `application.yml`) to Java objects. It makes managing configurations easier by binding them to strongly-typed POJOs (Plain Old Java Objects).

### Key Attributes of `@ConfigurationProperties`
1. **prefix**: Defines the prefix of the properties in the configuration file that should be mapped to this class.
2. **ignoreUnknownFields** (default `true`): Determines whether to ignore properties in the configuration file that are not mapped in the Java class.
3. **ignoreInvalidFields**: Determines whether to ignore fields with invalid values during binding.

### How It Works
1. **Define a POJO**: Create a Java class with fields corresponding to the configuration properties you want to map.
2. **Annotate with `@ConfigurationProperties`**: Use this annotation to specify the prefix of the properties.
3. **Register the POJO as a Spring Bean**: Use `@Component` or register it using a `@Bean` method.

---

### Example: Mapping Configuration Properties

#### Step 1: Define Properties in `application.properties`
```properties
app.name=My Application
app.version=1.0.0
app.description=A Spring Boot demo application
```

#### Step 2: Create a POJO
```java
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app")
public class AppConfig {
    private String name;
    private String version;
    private String description;

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
```

#### Step 3: Use the Configuration Class
```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AppController {

    @Autowired
    private AppConfig appConfig;

    @GetMapping("/app-info")
    public String getAppInfo() {
        return "Name: " + appConfig.getName() + 
               ", Version: " + appConfig.getVersion() + 
               ", Description: " + appConfig.getDescription();
    }
}
```

#### Step 4: Start the Application and Test
- When you hit `http://localhost:8080/app-info`, you'll see:
```
Name: My Application, Version: 1.0.0, Description: A Spring Boot demo application
```

---

### Additional Attributes Usage Example

1. **`ignoreUnknownFields` Example**
   If `application.properties` contains:
   ```properties
   app.name=My Application
   app.version=1.0.0
   app.unknown=Some value
   ```
   - With `ignoreUnknownFields = true` (default), the `unknown` field is ignored.
   - With `ignoreUnknownFields = false`, an exception is thrown.

   Modify the annotation:
   ```java
   @ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
   ```

2. **`ignoreInvalidFields` Example**
   If a property is incorrectly formatted:
   ```properties
   app.version=one-point-zero
   ```
   - With `ignoreInvalidFields = true`, invalid values are ignored.
   - With `ignoreInvalidFields = false`, an exception is thrown.

---

This makes `@ConfigurationProperties` powerful for centralizing and managing configurations in a clean and maintainable way.

---
