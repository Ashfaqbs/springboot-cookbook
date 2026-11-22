# --- Build stage ---
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /workspace

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
RUN ./mvnw -q -B dependency:go-offline

COPY src src
RUN ./mvnw -q -B package -DskipTests

# --- Runtime stage ---
FROM eclipse-temurin:17-jre-alpine
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
WORKDIR /app
COPY --from=build /workspace/target/worker-*.jar app.jar
RUN mkdir -p /data/incoming /data/storage && chown -R appuser:appgroup /data /app
USER appuser

HEALTHCHECK --interval=15s --timeout=5s --start-period=30s --retries=5 \
    CMD wget -q -O- http://localhost:8081/actuator/health | grep -q '"status":"UP"' || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
