package com.foodroulette;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = "GOOGLE_MAPS_API_KEY=")
class RestaurantEndpointTest {
    @LocalServerPort int port;

    private int search(String body) throws Exception {
        var request = HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/api/restaurants/search"))
                .header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(body)).build();
        return HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString()).statusCode();
    }

    @Test void rejectsInvalidCoordinatesAndRadius() throws Exception {
        assertEquals(400, search("""
                {"menuName":"food","latitude":91,"longitude":0,"radiusMeters":5000}
                """));
        assertEquals(400, search("""
                {"menuName":"food","latitude":0,"longitude":0,"radiusMeters":5001}
                """));
        assertEquals(400, search("""
                {"menuName":" ","latitude":0,"longitude":0,"radiusMeters":5000}
                """));
    }

    @Test void reportsMissingGoogleKey() throws Exception {
        assertEquals(503, search("""
                {"menuName":"food","latitude":0,"longitude":0,"radiusMeters":5000}
                """));
    }
}
