package com.simply.Cinema.controller;

import com.simply.Cinema.core.location_and_venue.entity.Seat;
import com.simply.Cinema.core.show_and_booking.dto.ShowSeatResponseDto;
import com.simply.Cinema.core.show_and_booking.entity.ShowSeat;
import com.simply.Cinema.exception.BusinessException;
import com.simply.Cinema.service.show_and_booking.ShowSeatService;
import com.simply.Cinema.service.show_and_booking.ShowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/show-seats")
public class ShowSeatController {

    private final ShowSeatService showSeatService;

    @GetMapping("/{showId}")
    public ResponseEntity<?> getSeatsByShow(@PathVariable Long showId) {
        try {
            List<ShowSeatResponseDto> seats = showSeatService.getSeatsByShow(showId);
            return ResponseEntity.ok(seats);
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{showId}/available")
    public ResponseEntity<?> getAvailableSeats(@PathVariable Long showId) {
        try {
            List<ShowSeatResponseDto> seats = showSeatService.getAvailableSeats(showId);
            return ResponseEntity.ok(seats);
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{showId}/booked")
    public ResponseEntity<?> getBookedSeats(@PathVariable Long showId) {
        try {
            List<ShowSeatResponseDto> seats = showSeatService.getBookedSeats(showId);
            return ResponseEntity.ok(seats);
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{showId}/count/total")
    public ResponseEntity<?> countTotalSeats(@PathVariable Long showId) {
        try {
            int count = showSeatService.countTotalSeats(showId);
            return ResponseEntity.ok(Map.of("showId", showId, "totalSeats", count));
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{showId}/count/available")
    public ResponseEntity<?> countAvailableSeats(@PathVariable Long showId) {
        try {
            int count = showSeatService.countAvailableSeats(showId);
            return ResponseEntity.ok(Map.of("showId", showId, "availableSeats", count));
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{showId}/count/booked")
    public ResponseEntity<?> countBookedSeats(@PathVariable Long showId) {
        try {
            int count = showSeatService.countBookedSeats(showId);
            return ResponseEntity.ok(Map.of("showId", showId, "bookedSeats", count));
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

}
