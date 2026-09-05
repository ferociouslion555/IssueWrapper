FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
# Make maven wrapper executable
RUN chmod +x mvnw
# Download dependencies
RUN ./mvnw dependency:go-offline
# Copy source code and build
COPY src ./src
RUN ./mvnw package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
# Copy the .env file if it exists, otherwise it will run without it
COPY .env* ./

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
