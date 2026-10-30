package com.foodroulette;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HealthEndpointTest {
    @LocalServerPort
    private int port;

    @Test
    void healthEndpointReportsOk() throws Exception {
        var request = HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/api/health"))
                .GET().build();
        var response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("{\"status\":\"ok\"}", response.body());
    }
}
