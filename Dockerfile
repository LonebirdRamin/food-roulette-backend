# Build the application with the project's Maven Wrapper and Java 21.
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw
COPY src/ src/
RUN ./mvnw -B -DskipTests package

# Keep the runtime image separate from the build tools.
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/food-roulette-backend-0.0.1-SNAPSHOT.jar app.jar
USER 10001
CMD ["java", "-jar", "app.jar"]
