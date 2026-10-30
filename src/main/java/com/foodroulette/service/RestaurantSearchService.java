package com.foodroulette.service;

import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import com.foodroulette.client.PlacesSearchClient;
import com.foodroulette.client.PlacesSearchClient.Place;
import com.foodroulette.dto.RestaurantSearchRequest;
import com.foodroulette.dto.RestaurantSearchResponse;
import com.foodroulette.dto.RestaurantSearchResponse.Restaurant;

@Service
public class RestaurantSearchService {
    private final PlacesSearchClient client;
    private final DistanceCalculationService distances;

    public RestaurantSearchService(PlacesSearchClient client, DistanceCalculationService distances) {
        this.client = client;
        this.distances = distances;
    }

    public RestaurantSearchResponse search(RestaurantSearchRequest request) {
        List<Restaurant> restaurants = client.search(request.menuName().trim(), request.latitude(), request.longitude(), request.radiusMeters())
                .stream().map(place -> new Nearby(place, distances.distanceMeters(request.latitude(), request.longitude(), place.latitude(), place.longitude())))
                .filter(item -> item.distance <= request.radiusMeters())
                .sorted(Comparator.comparingDouble(Nearby::distance))
                .map(item -> new Restaurant(item.place.id(), item.place.name(), item.place.address(),
                        item.place.latitude(), item.place.longitude(), Math.round(item.distance), item.place.rating(),
                        item.place.googleMapsUrl(), "UNVERIFIED", item.place.attributions()))
                .toList();
        return new RestaurantSearchResponse(request.menuName().trim(), request.radiusMeters(), restaurants);
    }

    private record Nearby(Place place, double distance) {}
}
