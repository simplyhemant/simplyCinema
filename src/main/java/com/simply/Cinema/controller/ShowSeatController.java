package com.simply.Cinema.controller;

import com.simply.Cinema.core.show_and_booking.dto.ShowSeatResponseDto;
import com.simply.Cinema.exception.BusinessException;
import com.simply.Cinema.service.show_and_booking.ShowSeatService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/show-seats")
@Tag(name = "Show Seat API", description = "Operations related to show seat availability and statistics")
public class ShowSeatController {

    private static final Logger logger = LoggerFactory.getLogger(ShowSeatController.class);

    private final ShowSeatService showSeatService;

    @Operation(
            summary = "Get All Seats By Show",
            description = "Fetch all seats associated with a specific show"
    )
    @GetMapping("/{showId}")
    public ResponseEntity<?> getSeatsByShow(@PathVariable Long showId) {
        logger.info("🎟️ [GET SEATS BY SHOW] Request received for Show ID: {}", showId);
        try {
            List<ShowSeatResponseDto> seats = showSeatService.getSeatsByShow(showId);
            logger.info("✅ [GET SEATS BY SHOW] Retrieved {} seats for Show ID: {}", seats.size(), showId);
            return ResponseEntity.ok(seats);
        } catch (BusinessException e) {
            logger.error("❌ [GET SEATS BY SHOW] Error retrieving seats for Show ID {}: {}", showId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(
            summary = "Get Available Seats By Show",
            description = "Fetch only available seats for a specific show"
    )
    @GetMapping("/{showId}/available")
    public ResponseEntity<?> getAvailableSeats(@PathVariable Long showId) {
        logger.info("💺 [GET AVAILABLE SEATS] Request received for Show ID: {}", showId);
        try {
            List<ShowSeatResponseDto> seats = showSeatService.getAvailableSeats(showId);
            logger.info("✅ [GET AVAILABLE SEATS] Found {} available seats for Show ID: {}", seats.size(), showId);
            return ResponseEntity.ok(seats);
        } catch (BusinessException e) {
            logger.error("❌ [GET AVAILABLE SEATS] Error for Show ID {}: {}", showId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(
            summary = "Get Booked Seats By Show",
            description = "Fetch only booked seats for a specific show"
    )
    @GetMapping("/{showId}/booked")
    public ResponseEntity<?> getBookedSeats(@PathVariable Long showId) {
        logger.info("🎫 [GET BOOKED SEATS] Request received for Show ID: {}", showId);
        try {
            List<ShowSeatResponseDto> seats = showSeatService.getBookedSeats(showId);
            logger.info("✅ [GET BOOKED SEATS] Found {} booked seats for Show ID: {}", seats.size(), showId);
            return ResponseEntity.ok(seats);
        } catch (BusinessException e) {
            logger.error("❌ [GET BOOKED SEATS] Error for Show ID {}: {}", showId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(
            summary = "Count Total Seats",
            description = "Returns total number of seats for a specific show"
    )
    @GetMapping("/{showId}/count/total")
    public ResponseEntity<?> countTotalSeats(@PathVariable Long showId) {
        logger.info("🔢 [COUNT TOTAL SEATS] Request received for Show ID: {}", showId);
        try {
            int count = showSeatService.countTotalSeats(showId);
            logger.info("✅ [COUNT TOTAL SEATS] Total seats for Show ID {}: {}", showId, count);
            return ResponseEntity.ok(Map.of("showId", showId, "totalSeats", count));
        } catch (BusinessException e) {
            logger.error("❌ [COUNT TOTAL SEATS] Error for Show ID {}: {}", showId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(
            summary = "Count Available Seats",
            description = "Returns number of available seats for a specific show"
    )
    @GetMapping("/{showId}/count/available")
    public ResponseEntity<?> countAvailableSeats(@PathVariable Long showId) {
        logger.info("🔢 [COUNT AVAILABLE SEATS] Request received for Show ID: {}", showId);
        try {
            int count = showSeatService.countAvailableSeats(showId);
            logger.info("✅ [COUNT AVAILABLE SEATS] Available seats for Show ID {}: {}", showId, count);
            return ResponseEntity.ok(Map.of("showId", showId, "availableSeats", count));
        } catch (BusinessException e) {
            logger.error("❌ [COUNT AVAILABLE SEATS] Error for Show ID {}: {}", showId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(
            summary = "Count Booked Seats",
            description = "Returns number of booked seats for a specific show"
    )
    @GetMapping("/{showId}/count/booked")
    public ResponseEntity<?> countBookedSeats(@PathVariable Long showId) {
        logger.info("🔢 [COUNT BOOKED SEATS] Request received for Show ID: {}", showId);
        try {
            int count = showSeatService.countBookedSeats(showId);
            logger.info("✅ [COUNT BOOKED SEATS] Booked seats for Show ID {}: {}", showId, count);
            return ResponseEntity.ok(Map.of("showId", showId, "bookedSeats", count));
        } catch (BusinessException e) {
            logger.error("❌ [COUNT BOOKED SEATS] Error for Show ID {}: {}", showId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
