package com.simply.Cinema.controller;

import com.simply.Cinema.core.location_and_venue.dto.SeatLayoutDto;
import com.simply.Cinema.core.location_and_venue.dto.SeatTypeDto;
import com.simply.Cinema.exception.BusinessException;
import com.simply.Cinema.exception.ResourceNotFoundException;
import com.simply.Cinema.exception.ValidationException;
import com.simply.Cinema.service.location_and_venue.SeatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/seats/")
public class SeatController{

    private final SeatService seatService;

    @PostMapping("/{screenId}/create")
    @PreAuthorize("hasRole('THEATRE_OWNER')")
    public ResponseEntity<SeatLayoutDto> createSeatLayout(
            @RequestBody SeatLayoutDto seatLayoutDto,
            @PathVariable Long screenId) throws ResourceNotFoundException, ValidationException, BusinessException {

        log.info("Request to create seat layout for screenId: {}", screenId);
        SeatLayoutDto createdLayout = seatService.createSeatLayout(screenId, seatLayoutDto);
        log.debug("Seat layout created: {}", createdLayout);
        return ResponseEntity.ok(createdLayout);
    }

    @PostMapping("/{screenId}/update")
    @PreAuthorize("hasRole('THEATRE_OWNER')")
    public ResponseEntity<SeatLayoutDto> updateSeatLayout(
            @RequestBody SeatLayoutDto seatLayoutDto,
            @PathVariable Long screenId) throws ResourceNotFoundException, ValidationException, BusinessException {

        log.info("Request to update seat layout for screenId: {}", screenId);
        SeatLayoutDto updatedLayout = seatService.updateSeatLayout(screenId, seatLayoutDto);
        log.debug("Seat layout updated: {}", updatedLayout);
        return ResponseEntity.ok(updatedLayout);
    }

    @PutMapping("/{layoutId}/update")
    @PreAuthorize("hasRole('THEATRE_OWNER')")
    public ResponseEntity<SeatLayoutDto> updateSeat(
            @PathVariable Long layoutId,
            @RequestBody SeatLayoutDto seatLayoutDto) throws ResourceNotFoundException, ValidationException {

        log.info("Request to update seat in layoutId: {}", layoutId);
        SeatLayoutDto updateLayout = seatService.updateSeat(layoutId, seatLayoutDto);
        log.debug("Seat updated in layout: {}", updateLayout);
        return ResponseEntity.ok(updateLayout);
    }

    @GetMapping("/screen/{screenId}")
    public ResponseEntity<SeatLayoutDto> getSeatLayoutByScreen(@PathVariable Long screenId){
        log.info("Fetching seat layout for screenId: {}", screenId);
        SeatLayoutDto seatLayoutDto = seatService.getSeatLayoutByScreen(screenId);
        return ResponseEntity.ok(seatLayoutDto);
    }

    @DeleteMapping("/layout/{layoutId}")
    @PreAuthorize("hasRole('THEATRE_OWNER')")
    public ResponseEntity<Void> deleteSeatLayout(@PathVariable Long layoutId) throws ResourceNotFoundException {
        log.info("Deleting seat layout with layoutId: {}", layoutId);
        seatService.deleteSeatLayout(layoutId);
        log.debug("Seat layout deleted: {}", layoutId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{seatId}/availability")
    public ResponseEntity<Boolean> isSeatAvailable(@PathVariable Long seatId) throws ResourceNotFoundException {
        log.info("Checking availability for seatId: {}", seatId);
        boolean available = seatService.isSeatAvailable(seatId);
        log.debug("Seat availability for {} -> {}", seatId, available);
        return ResponseEntity.ok(available);
    }

    @GetMapping("/types")
    public ResponseEntity<List<SeatTypeDto>> getSeatTypes() {
        log.info("Fetching all seat types");
        return ResponseEntity.ok(seatService.getSeatTypes());
    }

    @PostMapping("/types")
    @PreAuthorize("hasRole('THEATRE_OWNER')")
    public ResponseEntity<SeatTypeDto> addSeatType(@RequestBody @Validated SeatTypeDto seatTypeDto) throws ValidationException {
        log.info("Adding new seat type: {}", seatTypeDto.getSeatType());
        SeatTypeDto added = seatService.addSeatType(seatTypeDto);
        log.debug("Seat type added: {}", added);
        return ResponseEntity.status(HttpStatus.CREATED).body(added);
    }

    @PutMapping("/types/{id}")
    @PreAuthorize("hasRole('THEATRE_OWNER')")
    public ResponseEntity<SeatTypeDto> updateSeatType(
            @PathVariable Long id,
            @RequestBody @Validated SeatTypeDto seatTypeDto) throws ResourceNotFoundException, ValidationException {
        log.info("Updating seat type with id: {}", id);
        SeatTypeDto updated = seatService.updateSeatType(id, seatTypeDto);
        log.debug("Seat type updated: {}", updated);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/capacity/{screenId}")
    public ResponseEntity<Integer> getSeatCapacityByScreen(@PathVariable Long screenId) throws ResourceNotFoundException {
        log.info("Fetching seat capacity for screenId: {}", screenId);
        int capacity = seatService.getSeatCapacityByScreen(screenId);
        log.debug("Seat capacity for screen {} -> {}", screenId, capacity);
        return ResponseEntity.ok(capacity);
    }
}
