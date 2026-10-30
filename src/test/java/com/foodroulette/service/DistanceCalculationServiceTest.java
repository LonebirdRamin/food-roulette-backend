package com.foodroulette.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DistanceCalculationServiceTest {
    private final DistanceCalculationService distance = new DistanceCalculationService();

    @Test
    void calculatesStraightLineDistanceInMeters() {
        assertEquals(0, distance.distanceMeters(13.75, 100.50, 13.75, 100.50), 0.001);
        assertEquals(1112, distance.distanceMeters(0, 0, 0.01, 0), 5);
    }
}
