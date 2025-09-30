package com.simply.Cinema.controller;

import com.simply.Cinema.service.show_and_booking.SeatLockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/seats")
@RequiredArgsConstructor
public class SeatLockController {

    private final SeatLockService seatLockService;

    @PostMapping("/lock")
    public ResponseEntity<String> lockSeats(@RequestParam Long showId,
                                            @RequestParam Long userId,
                                            @RequestBody List<Long> seatIds) throws Exception {
        log.info("Locking seats {} for showId={} by userId={}", seatIds, showId, userId);
        seatLockService.lockSeats(showId, seatIds, userId);
        log.info("Seats {} locked successfully for showId={} by userId={}", seatIds, showId, userId);
        return ResponseEntity.ok("Seats locked successfully");
    }

    @PostMapping("/release")
    public ResponseEntity<String> releaseSeats(@RequestParam Long showId,
                                               @RequestParam Long userId,
                                               @RequestBody List<Long> seatNumbers) throws Exception {
        log.info("Releasing seats {} for showId={} by userId={}", seatNumbers, showId, userId);
        seatLockService.releaseLockedSeats(showId, seatNumbers, userId);
        log.info("Seats {} released successfully for showId={} by userId={}", seatNumbers, showId, userId);
        return ResponseEntity.ok("Seats released successfully");
    }

    @GetMapping("/isLocked")
    public ResponseEntity<Boolean> isSeatLocked(@RequestParam Long showId,
                                                @RequestParam Long seatNumber) throws Exception {
        log.debug("Checking lock status for seat {} in showId={}", seatNumber, showId);
        boolean isLocked = seatLockService.checkSeatLockStatus(showId, seatNumber);
        log.debug("Seat {} in showId={} locked={}", seatNumber, showId, isLocked);
        return ResponseEntity.ok(isLocked);
    }

    @GetMapping("/locked")
    public ResponseEntity<List<String>> getLockedSeats(@RequestParam Long showId) {
        log.debug("Fetching all locked seats for showId={}", showId);
        List<String> lockedSeats = seatLockService.getLockedSeats(showId);
        log.debug("Locked seats for showId={}: {}", showId, lockedSeats);
        return ResponseEntity.ok(lockedSeats);
    }

}
