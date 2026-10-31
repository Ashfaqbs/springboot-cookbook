The `SpringApplicationBuilder` class in Spring Boot provides a flexible way to configure and launch a Spring Boot application. It is especially useful when you need to create complex application contexts, such as parent-child hierarchies, or when you want to set properties and profiles fluently.

### **Key Use Cases and Examples**

#### **1. Creating a Simple Application**
This example demonstrates a basic usage of `SpringApplicationBuilder` to launch an application.

```java
import org.springframework.boot.builder.SpringApplicationBuilder;

public class MyApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(MyApplication.class)
            .run(args);
    }
}
```

Here:
- `MyApplication.class` is your main application class annotated with `@SpringBootApplication`.
- The `run` method launches the application.

---

#### **2. Setting Active Profiles**
Profiles help in managing different configurations (e.g., dev, test, production). You can use `SpringApplicationBuilder` to set the active profile.

```java
import org.springframework.boot.builder.SpringApplicationBuilder;

public class MyApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(MyApplication.class)
            .profiles("dev")
            .run(args);
    }
}
```

Here:
- The profile `dev` is activated. Configuration files like `application-dev.properties` will be used.

---

#### **3. Defining Default Properties**
You can set default properties programmatically.

```java
import org.springframework.boot.builder.SpringApplicationBuilder;

import java.util.HashMap;
import java.util.Map;

public class MyApplication {
    public static void main(String[] args) {
        Map<String, Object> defaultProps = new HashMap<>();
        defaultProps.put("server.port", 8085);
        defaultProps.put("spring.application.name", "MyCustomApp");

        new SpringApplicationBuilder(MyApplication.class)
            .properties(defaultProps)
            .run(args);
    }
}
```

Here:
- The application runs on port `8085`.
- The application's name is set to `MyCustomApp`.

---

#### **4. Creating a Parent-Child Context Hierarchy**
In some cases, you might need to create modular applications where one context depends on another. For example:

```java
import org.springframework.boot.builder.SpringApplicationBuilder;

public class MyApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(ParentConfig.class)
            .child(ChildConfig.class)
            .run(args);
    }
}
```

Here:
- `ParentConfig` and `ChildConfig` are configuration classes annotated with `@Configuration` or `@SpringBootApplication`.
- The child context will inherit beans from the parent context, enabling modular configurations.

---

#### **5. Customizing the Application Banner**
You can configure the banner mode (e.g., OFF, CONSOLE, LOG).

```java
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.Banner;

public class MyApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(MyApplication.class)
            .bannerMode(Banner.Mode.OFF) // Disables the banner
            .run(args);
    }
}
```

---

### **Why Use SpringApplicationBuilder?**
- It allows **fluent chaining** of multiple configurations.
- It's particularly helpful when dealing with **parent-child contexts**.
- You can easily set profiles, properties, and other configurations programmatically.

---







Here are some **critical and advanced examples** that showcase the power of `SpringApplicationBuilder` in more complex scenarios:

---

### **1. Creating Multiple Child Contexts**
If you have a modular application, you might want to set up multiple child contexts with different configurations.

```java
import org.springframework.boot.builder.SpringApplicationBuilder;

public class ModularApplication {
    public static void main(String[] args) {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(ParentConfig.class);
        builder.child(ModuleAConfig.class).run(args);
        builder.child(ModuleBConfig.class).run(args);
    }
}
```

- **ParentConfig**: Defines shared beans (e.g., database connections, services).
- **ModuleAConfig** and **ModuleBConfig**: Contain module-specific beans.
- Each module (child context) is isolated but can still use beans from the parent context.

---

### **2. Configuring Application for Multiple Environments Dynamically**
You can dynamically decide which configuration to load based on the environment, enabling more control than just using profiles.

```java
import org.springframework.boot.builder.SpringApplicationBuilder;

public class EnvironmentBasedApp {
    public static void main(String[] args) {
        String environment = System.getProperty("env", "default"); // Read 'env' system property

        new SpringApplicationBuilder()
            .sources(BaseConfig.class)
            .profiles(environment) // Activate profile based on the 'env' variable
            .run(args);
    }
}
```

- Use the command line to pass `-Denv=dev` or `-Denv=prod`.
- This dynamically activates the profile (`application-dev.properties` or `application-prod.properties`).

---

### **3. Adding Listeners Programmatically**
You can add custom listeners to your application, such as those that respond to application lifecycle events.

```java
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationListener;
import org.springframework.boot.context.event.ApplicationStartingEvent;

public class ApplicationWithListener {
    public static void main(String[] args) {
        new SpringApplicationBuilder(ApplicationWithListener.class)
            .listeners((ApplicationListener<ApplicationStartingEvent>) event -> {
                System.out.println("Application is starting...");
            })
            .run(args);
    }
}
```

- This example prints a message when the application starts.
- Listeners are useful for monitoring or triggering actions during specific phases of the application lifecycle.

---

### **4. Using Custom Environment Configurations**
You can configure a custom environment programmatically, allowing you to modify properties at runtime.

```java
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.core.env.ConfigurableEnvironment;

public class CustomEnvironmentApp {
    public static void main(String[] args) {
        new SpringApplicationBuilder(CustomEnvironmentApp.class)
            .environment(env -> {
                ConfigurableEnvironment environment = (ConfigurableEnvironment) env;
                environment.getSystemProperties().put("custom.property", "customValue");
            })
            .run(args);
    }
}
```

- Sets a custom property `custom.property` dynamically.
- This can be accessed in the application using `@Value("${custom.property}")`.

---

### **5. Handling Failures Gracefully with Exception Handlers**
You can configure the builder to handle startup exceptions explicitly.

```java
import org.springframework.boot.builder.SpringApplicationBuilder;

public class AppWithErrorHandler {
    public static void main(String[] args) {
        new SpringApplicationBuilder(AppWithErrorHandler.class)
            .run(args)
            .addShutdownHook(() -> System.out.println("Application shutting down gracefully."));
    }
}
```

This setup:
- Adds custom behavior during application shutdown.
- Ensures that any necessary cleanup happens.

---

### **6. Using Parent Context for Shared Security Configurations**
In security-heavy applications, a parent context can share authentication configurations.

```java
import org.springframework.boot.builder.SpringApplicationBuilder;

public class SecureApp {
    public static void main(String[] args) {
        new SpringApplicationBuilder(SecurityConfig.class)
            .child(ModuleOneConfig.class)
            .child(ModuleTwoConfig.class)
            .run(args);
    }
}
```

- **SecurityConfig**: Defines shared security settings (e.g., `Spring Security`).
- **ModuleOneConfig** and **ModuleTwoConfig**: Inherit security configurations from the parent context.

---

### **7. Chaining with `web()` and `headless()`**
You can configure the application for web or headless environments:

```java
import org.springframework.boot.builder.SpringApplicationBuilder;

public class WebOrHeadlessApp {
    public static void main(String[] args) {
        new SpringApplicationBuilder(WebOrHeadlessApp.class)
            .web(false) // Runs the app in a non-web (headless) environment
            .run(args);
    }
}
```

- `web(true)` ensures the application starts with a web server (default for web applications).
- `web(false)` is useful for batch processing or command-line applications.

---

### **8. Overriding the Default Banner with a Custom One**
Customize the startup banner programmatically.

```java
import org.springframework.boot.Banner;
import org.springframework.boot.builder.SpringApplicationBuilder;

import java.io.PrintStream;

public class CustomBannerApp {
    public static void main(String[] args) {
        new SpringApplicationBuilder(CustomBannerApp.class)
            .banner((environment, sourceClass, out) -> {
                out.println("***************************");
                out.println("* Welcome to MyApp v1.0! *");
                out.println("***************************");
            })
            .run(args);
    }
}
```

- Replaces the default Spring Boot banner with a custom ASCII-art banner.
- Allows branding of your application during startup.

---

### **Key Takeaways**
- **Dynamic Configuration**: Use profiles, properties, and environments dynamically.
- **Modularization**: Parent-child context support makes modular development seamless.
- **Advanced Lifecycle Management**: Adding listeners and exception handlers enhances control over the application lifecycle.
- **Customizability**: Everything from banners to configurations can be customized programmatically.

---
