package com.simply.Cinema.controller;

import com.simply.Cinema.core.show_and_booking.dto.ShowDto;
import com.simply.Cinema.exception.ResourceNotFoundException;
import com.simply.Cinema.response.ApiResponse;
import com.simply.Cinema.service.show_and_booking.ShowService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/show")
public class ShowController {

    private final ShowService showService;

    @GetMapping("/{id}")
    public ResponseEntity<ShowDto> getShowById(@PathVariable Long id) throws ResourceNotFoundException {
        log.info("Fetching show with ID: {}", id);
        ShowDto show = showService.getShowById(id);
        log.debug("Fetched show: {}", show);
        return ResponseEntity.ok(show);
    }

    @PreAuthorize("hasRole('THEATRE_OWNER')")
    @PostMapping("/create")
    public ResponseEntity<ShowDto> createShow(@RequestBody @Valid ShowDto dto) {
        log.info("Creating new show: {}", dto);
        ShowDto createdShow = showService.createShow(dto);
        log.debug("Show created successfully: {}", createdShow);
        return new ResponseEntity<>(createdShow, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('THEATRE_OWNER')")
    @PutMapping("/update/{id}")
    public ResponseEntity<ShowDto> updateShow(@PathVariable Long id, @RequestBody @Valid ShowDto dto)
            throws ResourceNotFoundException {
        log.info("Updating show with ID: {}", id);
        ShowDto updatedShow = showService.updateShow(id, dto);
        log.debug("Show updated: {}", updatedShow);
        return ResponseEntity.ok(updatedShow);
    }

    @PreAuthorize("hasRole('THEATRE_OWNER')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteShow(@PathVariable Long id) throws ResourceNotFoundException {
        log.info("Deleting show with ID: {}", id);
        showService.deleteShow(id);
        log.info("Show deleted successfully with ID: {}", id);
        return ResponseEntity.ok("Show with ID " + id + " deleted successfully.");
    }

    @GetMapping("/movies/{movieId}")
    public ResponseEntity<?> getShowsByMovie(@PathVariable Long movieId) {
        log.info("Fetching shows for movieId: {}", movieId);
        List<ShowDto> shows = showService.getShowsByMovie(movieId);

        if (shows.isEmpty()) {
            log.warn("No shows found for movieId: {}", movieId);
            ApiResponse response = new ApiResponse("No shows found for movie ID: " + movieId, false);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        log.debug("Shows found: {}", shows);
        return ResponseEntity.ok(shows);
    }

    @GetMapping("/theatres/{theatreId}")
    public ResponseEntity<List<ShowDto>> getShowsByTheatre(@PathVariable Long theatreId) {
        log.info("Fetching shows for theatreId: {}", theatreId);
        List<ShowDto> shows = showService.getShowsByTheatre(theatreId);
        log.debug("Shows fetched: {}", shows);
        return ResponseEntity.ok(shows);
    }
}
