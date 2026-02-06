package com.simply.Cinema.controller;

import com.simply.Cinema.core.location_and_venue.dto.CityDto;
import com.simply.Cinema.exception.BusinessException;
import com.simply.Cinema.exception.ResourceNotFoundException;
import com.simply.Cinema.response.ApiResponse;
import com.simply.Cinema.service.location_and_venue.CityService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/cities")
@RequiredArgsConstructor
@Tag(name = "City API", description = "Operations related to city management")
public class CityController {

    private static final Logger logger = LoggerFactory.getLogger(CityController.class);

    private final CityService cityService;

    @Operation(
            summary = "Create City",
            description = "Creates a new city (Admin only)",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> createCity(@RequestBody CityDto cityDto) throws BusinessException {
        logger.info("Request to create city: {}", cityDto.getName());
        CityDto createdCity = cityService.createCity(cityDto);
        logger.info("City created successfully with ID: {}", createdCity.getId());
        return ResponseEntity.ok(new ApiResponse("City created successfully", true));
    }

    @Operation(
            summary = "Update City",
            description = "Updates an existing city (Admin only)",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PutMapping("/update/{cityId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> updateCity(@PathVariable Long cityId, @RequestBody CityDto cityDto) throws BusinessException {
        logger.info("Request to update city with ID: {}", cityId);
        CityDto updatedCity = cityService.updateCity(cityId, cityDto);
        logger.info("City updated successfully with ID: {}", updatedCity.getId());
        return ResponseEntity.ok(new ApiResponse("City updated successfully", true));
    }

    @Operation(
            summary = "Delete City",
            description = "Soft deletes a city by ID (Admin only)",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{cityId}")
    public ResponseEntity<ApiResponse> deleteCity(@PathVariable Long cityId) {
        logger.info("Request to delete city with ID: {}", cityId);
        try {
            cityService.deleteCity(cityId);
            logger.info("City with ID: {} deleted successfully (soft delete)", cityId);
            return ResponseEntity.ok(new ApiResponse("City deleted successfully", true));
        } catch (BusinessException e) {
            logger.error("City not found with ID: {}", cityId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse("City not found with ID: " + cityId, false));
        }
    }

    @Operation(summary = "Get City By ID", description = "Fetch city details using city ID")
    @GetMapping("/city/{cityId}")
    public ResponseEntity<CityDto> getCityById(@PathVariable Long cityId) {
        logger.info("Fetching city with ID: {}", cityId);
        CityDto city = cityService.getCityById(cityId);
        logger.info("City fetched successfully with ID: {}", cityId);
        return ResponseEntity.ok(city);
    }

    @Operation(summary = "Get All Cities", description = "Fetch all cities (including inactive)")
    @GetMapping("/all")
    public ResponseEntity<List<CityDto>> getAllCities() {
        logger.info("Fetching all cities");
        List<CityDto> cities = cityService.getAllCities();
        logger.info("Fetched {} cities", cities.size());
        return ResponseEntity.ok(cities);
    }

    @Operation(summary = "Get Active Cities", description = "Fetch all active cities")
    @GetMapping("/active")
    public ResponseEntity<List<CityDto>> getActiveCities() {
        logger.info("Fetching all active cities");
        List<CityDto> cities = cityService.getActiveCities();
        logger.info("Fetched {} active cities", cities.size());
        return ResponseEntity.ok(cities);
    }

    @Operation(summary = "Find Cities By State", description = "Fetch cities filtered by state name")
    @GetMapping("/state")
    public ResponseEntity<List<CityDto>> findCitiesByState(@RequestParam String state) {
        logger.info("Fetching cities for state: {}", state);
        List<CityDto> cities = cityService.findCitiesByState(state);
        logger.info("Fetched {} cities for state: {}", cities.size(), state);
        return ResponseEntity.ok(cities);
    }

    @Operation(summary = "Find Cities By Country", description = "Fetch cities filtered by country name")
    @GetMapping("/country")
    public ResponseEntity<List<CityDto>> findCitiesByCountry(@RequestParam String country) {
        logger.info("Fetching cities for country: {}", country);
        List<CityDto> cities = cityService.findCitiesByCountry(country);
        logger.info("Fetched {} cities for country: {}", cities.size(), country);
        return ResponseEntity.ok(cities);
    }

    @Operation(summary = "Get City Timezone", description = "Fetch timezone of a specific city by ID")
    @GetMapping("/{cityId}/timezone")
    public ResponseEntity<ApiResponse> getCityTimezone(@PathVariable Long cityId) {
        logger.info("Fetching timezone for city ID: {}", cityId);
        try {
            String timezone = cityService.getCityTimezone(cityId);
            logger.info("Timezone for city ID {}: {}", cityId, timezone);
            return ResponseEntity.ok(new ApiResponse(timezone, true));
        } catch (ResourceNotFoundException e) {
            logger.error("City not found with ID: {}", cityId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse("City not found with ID: " + cityId, false));
        }
    }

    @Operation(summary = "Search Cities", description = "Search cities using a keyword")
    @GetMapping("/search")
    public ResponseEntity<List<CityDto>> searchCities(@RequestParam String keyword) {
        logger.info("Searching cities with keyword: {}", keyword);
        List<CityDto> cityList = cityService.searchCities(keyword);
        logger.info("Found {} cities for keyword: {}", cityList.size(), keyword);
        return ResponseEntity.ok(cityList);
    }
}
