package com.simply.Cinema.controller;

import com.simply.Cinema.service.show_and_booking.SeatLockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Slf4j
@RestController
@RequestMapping("/api/seats")
@RequiredArgsConstructor
@Tag(name = "Seat Lock API", description = "Operations for locking and releasing seats during booking")
public class SeatLockController {

    private final SeatLockService seatLockService;

    @Operation(
            summary = "Lock Seats",
            description = "Locks selected seats for a specific show and user"
    )
    @PostMapping("/lock")
    public ResponseEntity<String> lockSeats(@RequestParam Long showId,
                                            @RequestParam Long userId,
                                            @RequestBody List<Long> seatIds) throws Exception {
        log.info("Locking seats {} for showId={} by userId={}", seatIds, showId, userId);
        seatLockService.lockSeats(showId, seatIds, userId);
        log.info("Seats {} locked successfully for showId={} by userId={}", seatIds, showId, userId);
        return ResponseEntity.ok("Seats locked successfully");
    }

    @Operation(
            summary = "Release Locked Seats",
            description = "Releases previously locked seats for a specific show and user"
    )
    @PostMapping("/release")
    public ResponseEntity<String> releaseSeats(@RequestParam Long showId,
                                               @RequestParam Long userId,
                                               @RequestBody List<Long> seatNumbers) throws Exception {
        log.info("Releasing seats {} for showId={} by userId={}", seatNumbers, showId, userId);
        seatLockService.releaseLockedSeats(showId, seatNumbers, userId);
        log.info("Seats {} released successfully for showId={} by userId={}", seatNumbers, showId, userId);
        return ResponseEntity.ok("Seats released successfully");
    }

    @Operation(
            summary = "Check Seat Lock Status",
            description = "Checks whether a specific seat is currently locked for a show"
    )
    @GetMapping("/isLocked")
    public ResponseEntity<Boolean> isSeatLocked(@RequestParam Long showId,
                                                @RequestParam Long seatNumber) throws Exception {
        log.debug("Checking lock status for seat {} in showId={}", seatNumber, showId);
        boolean isLocked = seatLockService.checkSeatLockStatus(showId, seatNumber);
        log.debug("Seat {} in showId={} locked={}", seatNumber, showId, isLocked);
        return ResponseEntity.ok(isLocked);
    }

    @Operation(
            summary = "Get All Locked Seats",
            description = "Fetches all currently locked seats for a specific show"
    )
    @GetMapping("/locked")
    public ResponseEntity<List<String>> getLockedSeats(@RequestParam Long showId) {
        log.debug("Fetching all locked seats for showId={}", showId);
        List<String> lockedSeats = seatLockService.getLockedSeats(showId);
        log.debug("Locked seats for showId={}: {}", showId, lockedSeats);
        return ResponseEntity.ok(lockedSeats);
    }
}
