# =========================

# Stage 1: Build

# =========================

FROM eclipse-temurin:25-jdk AS build

WORKDIR /app

# Maven wrapper + pom

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

RUN chmod +x mvnw

# Cache dependencies

RUN ./mvnw dependency:go-offline -B

# Source code

COPY src ./src

# Build application

RUN ./mvnw clean package -DskipTests

# =========================

# Stage 2: Runtime

# =========================

FROM eclipse-temurin:25-jre

WORKDIR /app

# Copy Spring Boot fat jar

COPY --from=build /app/target/*.jar app.jar

COPY --from=build /app/src/main/resources/scanlink-firebase-service-account.json /app/scanlink-firebase-service-account.json

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
