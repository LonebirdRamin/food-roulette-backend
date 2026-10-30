package com.foodroulette.service;

import java.util.List;
import org.junit.jupiter.api.Test;
import com.foodroulette.client.PlacesSearchClient;
import com.foodroulette.client.PlacesSearchClient.Place;
import com.foodroulette.dto.RestaurantSearchRequest;
import static org.junit.jupiter.api.Assertions.*;

class RestaurantSearchServiceTest {
    @Test
    void filtersByDistanceAndOrdersNearestFirst() {
        PlacesSearchClient client = (menu, lat, lon, radius) -> List.of(
                new Place("far", "Far", "", 0.10, 0, null, null, List.of()),
                new Place("middle", "Middle", "", 0.03, 0, null, null, List.of()),
                new Place("near", "Near", "", 0.01, 0, null, null, List.of()));
        var service = new RestaurantSearchService(client, new DistanceCalculationService());
        var result = service.search(new RestaurantSearchRequest("ผัดไทย", 0.0, 0.0, 5000));
        assertEquals(List.of("near", "middle"), result.restaurants().stream().map(r -> r.placeId()).toList());
        assertTrue(result.restaurants().stream().allMatch(r -> r.distanceMeters() <= 5000 && r.menuMatchStatus().equals("UNVERIFIED")));
    }
}
