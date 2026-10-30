package com.foodroulette.client;

import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GooglePlacesClientTest {
    @Test
    void sendsTheSearchAndMapsTheGoogleResponse() throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        AtomicReference<String> body = new AtomicReference<>();
        AtomicReference<String> fieldMask = new AtomicReference<>();
        server.createContext("/v1/places:searchText", exchange -> {
            body.set(new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
            fieldMask.set(exchange.getRequestHeaders().getFirst("X-Goog-FieldMask"));
            assertEquals("test-key", exchange.getRequestHeaders().getFirst("X-Goog-Api-Key"));
            byte[] response = """
                {"places":[{"id":"place1","displayName":{"text":"Test place","languageCode":"th"},"formattedAddress":"Test address",
                "location":{"latitude":13.75,"longitude":100.5},"rating":4.2,"googleMapsUri":"https://maps.google.com/",
                "attributions":[{"provider":"Example provider","providerUri":"https://example.com"}]}]}
                """.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.length);
            exchange.getResponseBody().write(response);
            exchange.close();
        });
        server.start();
        try {
            var client = new GooglePlacesClient("test-key", "http://127.0.0.1:" + server.getAddress().getPort());
            var results = client.search("ผัดไทย", 13.75, 100.5, 5000);
            assertEquals(1, results.size());
            assertEquals("place1", results.getFirst().id());
            assertEquals("Example provider", results.getFirst().attributions().getFirst().provider());
            assertTrue(body.get().contains("locationBias"));
            assertTrue(body.get().contains("ผัดไทย"));
            assertTrue(fieldMask.get().contains("places.location"));
        } finally {
            server.stop(0);
        }
    }

    @Test
    void handlesEmptyAndFailedResponses() throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/v1/places:searchText", exchange -> {
            byte[] response = "{}".getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.length);
            exchange.getResponseBody().write(response);
            exchange.close();
        });
        server.start();
        var client = new GooglePlacesClient("test-key", "http://127.0.0.1:" + server.getAddress().getPort());
        try {
            assertTrue(client.search("food", 0, 0, 5000).isEmpty());
        } finally {
            server.stop(0);
        }
        assertThrows(PlacesSearchException.class, () -> client.search("food", 0, 0, 5000));
    }

    @Test
    void rejectsMissingKeyBeforeSendingARequest() {
        assertThrows(PlacesSearchException.class, () -> new GooglePlacesClient("").search("food", 0, 0, 5000));
    }
}
