package com.foodroulette.dto;

import jakarta.validation.constraints.*;

public record RestaurantSearchRequest(
        @NotBlank @Size(max = 120) String menuName,
        @NotNull @DecimalMin("-90.0") @DecimalMax("90.0") Double latitude,
        @NotNull @DecimalMin("-180.0") @DecimalMax("180.0") Double longitude,
        @NotNull @Min(1) @Max(5000) Integer radiusMeters) {}
