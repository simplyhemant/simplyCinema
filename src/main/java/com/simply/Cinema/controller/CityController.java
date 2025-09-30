package com.simply.Cinema.controller;

import com.simply.Cinema.core.location_and_venue.dto.CityDto;
import com.simply.Cinema.exception.BusinessException;
import com.simply.Cinema.exception.ResourceNotFoundException;
import com.simply.Cinema.service.location_and_venue.CityService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cities")
@RequiredArgsConstructor
public class CityController {

    private static final Logger logger = LoggerFactory.getLogger(CityController.class);

    private final CityService cityService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CityDto> createCity(@RequestBody CityDto cityDto) throws BusinessException {
        logger.info("Request to create city: {}", cityDto.getName());
        CityDto createdCity = cityService.createCity(cityDto);
        logger.info("City created successfully with ID: {}", createdCity.getId());
        return ResponseEntity.ok(createdCity);
    }

    @PutMapping("/update/{cityId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CityDto> updateCity(@PathVariable Long cityId, @RequestBody CityDto cityDto) throws BusinessException {
        logger.info("Request to update city with ID: {}", cityId);
        CityDto updatedCity = cityService.updateCity(cityId, cityDto);
        logger.info("City updated successfully with ID: {}", updatedCity.getId());
        return ResponseEntity.ok(updatedCity);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{cityId}")
    public ResponseEntity<String> deleteCity(@PathVariable Long cityId) {
        logger.info("Request to delete city with ID: {}", cityId);
        try {
            cityService.deleteCity(cityId);
            logger.info("City with ID: {} deleted successfully (soft delete)", cityId);
            return ResponseEntity.noContent().build(); // 204
        } catch (BusinessException e) {
            logger.error("City not found with ID: {}", cityId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("City not found with ID: " + cityId);
        }
    }

    @GetMapping("/city/{cityId}")
    public ResponseEntity<CityDto> getCityById(@PathVariable Long cityId) {
        logger.info("Fetching city with ID: {}", cityId);
        CityDto city = cityService.getCityById(cityId);
        logger.info("City fetched successfully with ID: {}", cityId);
        return ResponseEntity.ok(city);
    }

    @GetMapping("/all")
    public ResponseEntity<List<CityDto>> getAllCities() {
        logger.info("Fetching all cities");
        List<CityDto> cities = cityService.getAllCities();
        logger.info("Fetched {} cities", cities.size());
        return ResponseEntity.ok(cities);
    }

    @GetMapping("/active")
    public ResponseEntity<List<CityDto>> getActiveCities() {
        logger.info("Fetching all active cities");
        List<CityDto> cities = cityService.getActiveCities();
        logger.info("Fetched {} active cities", cities.size());
        return ResponseEntity.ok(cities);
    }

    @GetMapping("/state")
    public ResponseEntity<List<CityDto>> findCitiesByState(@RequestParam String state) {
        logger.info("Fetching cities for state: {}", state);
        List<CityDto> cities = cityService.findCitiesByState(state);
        logger.info("Fetched {} cities for state: {}", cities.size(), state);
        return ResponseEntity.ok(cities);
    }

    @GetMapping("/country")
    public ResponseEntity<List<CityDto>> findCitiesByCountry(@RequestParam String country) {
        logger.info("Fetching cities for country: {}", country);
        List<CityDto> cities = cityService.findCitiesByCountry(country);
        logger.info("Fetched {} cities for country: {}", cities.size(), country);
        return ResponseEntity.ok(cities);
    }

    @GetMapping("/{cityId}/timezone")
    public ResponseEntity<String> getCityTimezone(@PathVariable Long cityId) {
        logger.info("Fetching timezone for city ID: {}", cityId);
        try {
            String timezone = cityService.getCityTimezone(cityId);
            logger.info("Timezone for city ID {}: {}", cityId, timezone);
            return ResponseEntity.ok(timezone);
        } catch (ResourceNotFoundException e) {
            logger.error("City not found with ID: {}", cityId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("City not found with ID: " + cityId);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<CityDto>> searchCities(@RequestParam String keyword) {
        logger.info("Searching cities with keyword: {}", keyword);
        List<CityDto> cityList = cityService.searchCities(keyword);
        logger.info("Found {} cities for keyword: {}", cityList.size(), keyword);
        return ResponseEntity.ok(cityList);
    }

}

