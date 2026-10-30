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

## Restaurant search (Phase 5)

The backend searches Google Places API (New), computes straight-line distances, and returns matches within the requested radius (up to 5,000 meters), nearest first. Search relevance does not confirm a restaurant serves the dish; results have `menuMatchStatus: "UNVERIFIED"`. An empty list means no candidates passed the filter, not that no restaurant exists.

Set the `GOOGLE_MAPS_API_KEY` environment variable in the terminal before starting the backend. Never commit or paste your key into the frontend. Your Google Cloud project must have billing and Places API (New) enabled. Google may charge for requests; check its current pricing and set usage alerts.

Example request (after starting the backend):

```sh
curl -X POST http://localhost:8080/api/restaurants/search \
  -H 'Content-Type: application/json' \
  -d '{"menuName":"ผัดไทย","latitude":13.7563,"longitude":100.5018,"radiusMeters":5000}'
```

The location above is a sample location in Bangkok. Use your intended coordinates when testing. Responses contain real Google Places data only when a valid key is configured. A missing key returns 503; invalid inputs return 400. The app does not yet display these results. When results are displayed, include required Google Maps and third-party attribution from the response, and follow the current Places API policies.
