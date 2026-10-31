In a Spring Boot app (or any Java app), **JVM options** (also called JVM arguments or JVM flags) are passed to control how the JVM behaves — memory settings, GC behavior, debugging, performance tuning, etc.

---

### 🔹 **Categories of JVM Options**

There are 3 types of JVM options:

| Type                 | Syntax Prefix | Purpose                                                                  |
| -------------------- | ------------- | ------------------------------------------------------------------------ |
| **Standard**         | `-`           | Recognized and stable across JVM versions (e.g., `-Xmx`, `-cp`)          |
| **Non-standard (X)** | `-X`          | Not guaranteed to be stable; useful for tuning (e.g., `-Xms`, `-Xloggc`) |
| **Advanced (XX)**    | `-XX:`        | Internal tuning, GC tuning, flags for performance and monitoring         |

---

###  **Common JVM Options with Examples**

####  **1. Memory settings**

```bash
-Xms512m             # Initial heap size
-Xmx2g               # Maximum heap size
-Xss512k             # Stack size per thread
```

####  **2. Garbage Collection**

```bash
-XX:+UseG1GC         # Use G1 garbage collector
-XX:MaxGCPauseMillis=200
```

####  **3. Logging and GC Logs**

```bash
-Xlog:gc*            # Enable GC logging (Java 9+)
-Xloggc:logs/gc.log  # Save GC logs to file (pre-Java 9)
```

####  **4. Debugging**

```bash
-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005
```

####  **5. Performance tuning**

```bash
-XX:+AlwaysPreTouch
-XX:+UseStringDeduplication
```

####  **6. Enable experimental/diagnostic features**

```bash
-XX:+UnlockExperimentalVMOptions
-XX:+UnlockDiagnosticVMOptions
```

####  **7. JMX Monitoring**

```bash
-Dcom.sun.management.jmxremote
-Dcom.sun.management.jmxremote.port=9010
-Dcom.sun.management.jmxremote.ssl=false
-Dcom.sun.management.jmxremote.authenticate=false
```

####  **8. Application-specific system properties**

```bash
-Dspring.profiles.active=prod
-Dlogging.level.root=INFO
```

---

###  **How to Pass JVM Options**

#### **1. From command line (direct execution)**

```bash
java -Xms512m -Xmx1g -Dspring.profiles.active=prod -jar app.jar
```

#### **2. With `SPRING_BOOT_JAVA_OPTS` or `JAVA_OPTS` env variable**

```bash
export JAVA_OPTS="-Xms256m -Xmx1g -Dspring.profiles.active=prod"
java $JAVA_OPTS -jar app.jar
```

#### **3. Inside Docker CMD/ENTRYPOINT**

```dockerfile
CMD ["java", "-Xms512m", "-Xmx1g", "-jar", "app.jar"]
```

---

###  **Pro Tip: Separating Concerns**

| Concern     | Use                                                             |
| ----------- | --------------------------------------------------------------- |
| JVM tuning  | `-X`, `-XX:` flags                                              |
| App config  | `-Dproperty=value`                                              |
| Environment | Use `.env`, `application.properties`, or external config server |

---

###  **Example: Spring Boot in Production**

```bash
java \
  -Xms512m -Xmx2g \
  -XX:+UseG1GC \
  -XX:MaxGCPauseMillis=200 \
  -Dspring.profiles.active=prod \
  -Dserver.port=8081 \
  -jar app.jar
```

This sets heap size, uses G1GC, configures GC pause goal, and sets Spring profile and port.
