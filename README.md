# Food Roulette backend

The backend is a Java 21 application built with Spring Boot and Maven. In Phase 2 it provides one endpoint to check whether the server is running.

## Requirements

- Java 21 JDK
- Maven, or the Maven Wrapper included in this repository

On macOS, select Java 21 in the current terminal if needed:

```sh
export JAVA_HOME="$(/usr/libexec/java_home -v 21)"
export PATH="$JAVA_HOME/bin:$PATH"
java -version
```

## Run locally

From the backend directory:

```sh
./mvnw spring-boot:run
```

The first run downloads Maven and the project dependencies and requires internet access. Once the application starts, in another terminal run:

```sh
curl http://localhost:8080/api/health
```

Expected response:

```json
{"status":"ok"}
```

Stop the server with Ctrl+C in the terminal where it is running. This endpoint confirms that the application responds; it does not check external services.

## Test and build

```sh
./mvnw test
./mvnw package
```

Maven writes the packaged application to `target/`. Restaurant search and external integrations are planned for later phases; they are not implemented yet.
