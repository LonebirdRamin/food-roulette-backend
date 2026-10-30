# Food Roulette backend

The backend is a Java 21 application built with Spring Boot and Maven. It provides a health endpoint and a Google Places restaurant search.

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

Maven writes the packaged application to `target/`.

## Restaurant search (Phase 5)

The backend searches Google Places API (New), computes straight-line distances, and returns matches within the requested radius (up to 5,000 meters), nearest first. Search relevance does not confirm a restaurant serves the dish; results have `menuMatchStatus: "UNVERIFIED"`. An empty list means no candidates passed the filter, not that no restaurant exists.

Set the `GOOGLE_MAPS_API_KEY` environment variable in the terminal before starting the backend. Never commit or paste your key into the frontend. Your Google Cloud project must have billing and Places API (New) enabled. Google may charge for requests; check its current pricing and set usage alerts.

Example request (after starting the backend):

```sh
curl -X POST http://localhost:8080/api/restaurants/search \
  -H 'Content-Type: application/json' \
  -d '{"menuName":"ผัดไทย","latitude":13.7563,"longitude":100.5018,"radiusMeters":5000}'
```

The location above is a sample location in Bangkok. Use your intended coordinates when testing. Responses contain real Google Places data only when a valid key is configured. A missing key returns 503; invalid inputs return 400. The frontend displays these results with Google Maps and any returned third-party attribution. Follow the current Places API policies when changing how results are displayed.


## Deployment on Render

Render does not offer a native Java runtime; the included Dockerfile builds with Java 21 and runs the resulting JAR. Create a Docker web service from this repository and set `GOOGLE_MAPS_API_KEY` in Render's environment settings. Set the health check path to `/api/health`. Render supplies `PORT`, which Spring Boot reads; locally it defaults to 8080. Never include the key in the Dockerfile or repository.

The backend needs an HTTPS URL reachable from the Next.js server. Set the frontend's `BACKEND_API_URL` to that URL without a trailing path. The restaurant endpoint can incur Google API charges; configure appropriate Google API restrictions, usage quotas, and billing alerts before exposing it.
