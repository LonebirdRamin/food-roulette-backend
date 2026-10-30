package com.foodroulette.dto;

import java.util.List;
import com.foodroulette.client.PlacesSearchClient.Attribution;

public record RestaurantSearchResponse(String query, int radiusMeters, List<Restaurant> restaurants) {
    public record Restaurant(String placeId, String name, String address, double latitude,
                             double longitude, long distanceMeters, Double rating,
                             String googleMapsUrl, String menuMatchStatus, List<Attribution> attributions) {}
}
