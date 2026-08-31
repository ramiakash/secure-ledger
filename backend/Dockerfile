# ---- Build stage ----
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /build

# Copy the wrapper and POM first, then resolve dependencies. This layer is
# cached and only re-runs when pom.xml changes, so code edits don't trigger a
# full re-download.
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

COPY src/ src/
RUN ./mvnw clean package -DskipTests -B

# ---- Runtime stage ----
FROM eclipse-temurin:21-jre-alpine AS runtime
WORKDIR /app

# Run as a non-root user. A container process running as root that gets
# compromised has far more room to move.
RUN addgroup -S ledger && adduser -S ledger -G ledger
USER ledger

COPY --from=build /build/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]