package com.foodroulette.client;

import java.util.List;

public interface PlacesSearchClient {
    List<Place> search(String menuName, double latitude, double longitude, int radiusMeters);

    record Attribution(String provider, String providerUri) {}
    record Place(String id, String name, String address, double latitude, double longitude,
                 Double rating, String googleMapsUrl, List<Attribution> attributions) {}
}
