# ------------ Build Stage ------------
FROM eclipse-temurin:21-jdk AS build

# Set working directory
WORKDIR /app

# Copy Maven wrapper files (optional, if using ./mvnw)
COPY mvnw ./
COPY .mvn .mvn

# Copy all files
COPY pom.xml ./
COPY src ./src

# Grant permission to Maven wrapper and build project
RUN chmod +x mvnw && ./mvnw clean package -DskipTests

# ------------ Runtime Stage ------------
FROM eclipse-temurin:21-jdk

# Set working directory
WORKDIR /app

# Copy the jar from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose port (adjust if needed)
EXPOSE 8080

# Run the jar
ENTRYPOINT ["java", "-jar", "app.jar"]
