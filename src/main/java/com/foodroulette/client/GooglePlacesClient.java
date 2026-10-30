package com.foodroulette.client;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class GooglePlacesClient implements PlacesSearchClient {
    private static final String FIELD_MASK = "places.id,places.displayName,places.formattedAddress,places.location,places.rating,places.googleMapsUri,places.attributions";
    private final RestClient client;
    private final String apiKey;

    @Autowired
    public GooglePlacesClient(@Value("${GOOGLE_MAPS_API_KEY:}") String apiKey) {
        this(apiKey, "https://places.googleapis.com");
    }

    GooglePlacesClient(String apiKey, String baseUrl) {
        this.apiKey = apiKey;
        var factory = new JdkClientHttpRequestFactory(HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build());
        factory.setReadTimeout(Duration.ofSeconds(10));
        this.client = RestClient.builder().baseUrl(baseUrl).requestFactory(factory).build();
    }

    @Override
    public List<Place> search(String menuName, double latitude, double longitude, int radiusMeters) {
        if (apiKey.isBlank()) throw new PlacesSearchException("Google Places API key is not configured");
        var body = Map.of("textQuery", menuName + " restaurant", "includedType", "restaurant",
                "locationBias", Map.of("circle", Map.of("center", Map.of("latitude", latitude, "longitude", longitude), "radius", radiusMeters)));
        try {
            GoogleResponse response = client.post().uri("/v1/places:searchText")
                    .header("X-Goog-Api-Key", apiKey).header("X-Goog-FieldMask", FIELD_MASK)
                    .body(body).retrieve().body(GoogleResponse.class);
            if (response == null || response.places() == null) return List.of();
            return response.places().stream()
                    .filter(p -> p.id() != null && p.displayName() != null && p.displayName().text() != null && p.location() != null
                            && p.location().latitude() != null && p.location().longitude() != null)
                    .map(p -> new Place(p.id(), p.displayName().text(), p.formattedAddress(), p.location().latitude(),
                            p.location().longitude(), p.rating(), p.googleMapsUri(),
                            p.attributions() == null ? List.of() : p.attributions().stream()
                                    .map(a -> new Attribution(a.provider(), a.providerUri())).toList()))
                    .toList();
        } catch (RestClientException e) {
            throw new PlacesSearchException("Google Places search is unavailable");
        }
    }

    record GoogleResponse(List<GooglePlace> places) {}
    record GooglePlace(String id, DisplayName displayName, String formattedAddress, Location location,
                       Double rating, String googleMapsUri, List<GoogleAttribution> attributions) {}
    record DisplayName(String text) {}
    record Location(Double latitude, Double longitude) {}
    record GoogleAttribution(String provider, String providerUri) {}
}
