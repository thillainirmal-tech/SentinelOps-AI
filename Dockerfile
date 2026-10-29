FROM maven:3.9.11-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY pom.xml .
RUN mvn --batch-mode --no-transfer-progress dependency:go-offline
COPY src src
RUN mvn --batch-mode --no-transfer-progress package -DskipTests

FROM eclipse-temurin:17-jre-alpine
RUN addgroup -S sentinelops && adduser -S sentinelops -G sentinelops
WORKDIR /app
COPY --from=build /workspace/target/sentinelops-ai-1.0.0.jar app.jar
USER sentinelops
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=3s --start-period=20s --retries=3 CMD wget -qO- http://localhost:8080/actuator/health || exit 1
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "/app/app.jar"]
