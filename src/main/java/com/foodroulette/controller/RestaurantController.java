package com.foodroulette.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import com.foodroulette.client.PlacesSearchException;
import com.foodroulette.dto.RestaurantSearchRequest;
import com.foodroulette.dto.RestaurantSearchResponse;
import com.foodroulette.service.RestaurantSearchService;
import java.util.Map;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {
    private final RestaurantSearchService service;
    public RestaurantController(RestaurantSearchService service) { this.service = service; }

    @PostMapping("/search")
    public RestaurantSearchResponse search(@Valid @RequestBody RestaurantSearchRequest request) {
        return service.search(request);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<Map<String, String>> handleInvalidRequest(Exception error) {
        return ResponseEntity.badRequest().body(Map.of("error", "Invalid search request. Check menu name, coordinates, and radius (1–5000 meters)."));
    }

    @ExceptionHandler(PlacesSearchException.class)
    public ResponseEntity<Map<String, String>> handlePlacesError(PlacesSearchException error) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Map.of("error", error.getMessage()));
    }
}
