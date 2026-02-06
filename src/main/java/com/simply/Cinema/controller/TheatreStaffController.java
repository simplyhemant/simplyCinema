package com.simply.Cinema.controller;

import com.simply.Cinema.core.show_and_booking.dto.CounterBookingDto;
import com.simply.Cinema.core.user.dto.CounterStaffDto;
import com.simply.Cinema.core.user.dto.CounterStaffResponseDto;
import com.simply.Cinema.core.user.entity.CounterStaff;
import com.simply.Cinema.exception.*;
import com.simply.Cinema.response.ApiResponse;
import com.simply.Cinema.service.auth.TheatreStaffService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/v1/counter-staff")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Counter Staff API", description = "Operations related to theatre counter staff management and bookings")
public class TheatreStaffController {

    private final TheatreStaffService counterStaffService;

    @Operation(
            summary = "Register Counter Staff",
            description = "Register a new counter staff (ADMIN or THEATRE_OWNER only)",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/register")
    @PreAuthorize("hasAnyRole('ADMIN', 'THEATRE_OWNER')")
    public ResponseEntity<?> registerCounterStaff(@Valid @RequestBody CounterStaffDto request) {

        try {
            CounterStaffResponseDto response = counterStaffService.registerCounterStaff(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (ValidationException e) {
            log.error("Validation error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(e.getMessage(), false));

        } catch (BusinessException e) {
            log.error("Business error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiResponse(e.getMessage(), false));

        } catch (ResourceNotFoundException e) {
            log.error("Resource not found: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), false));

        } catch (AuthorizationException e) {
            log.error("Authorization error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse(e.getMessage(), false));

        } catch (Exception e) {
            log.error("Error registering counter staff: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Failed to register counter staff", false));
        }
    }

    @Operation(
            summary = "Update Counter Staff",
            description = "Update counter staff details by ID",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'THEATRE_OWNER', 'THEATRE_STAFF')")
    public ResponseEntity<CounterStaff> updateCounterStaff(
            @PathVariable Long id,
            @Valid @RequestBody CounterStaffDto request) {

        try {
            CounterStaff counterStaff = counterStaffService.updateCounterStaffDetails(id, request);
            return new ResponseEntity<>(counterStaff, HttpStatus.OK);

        } catch (ValidationException e) {
            log.error("Validation error: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        } catch (BusinessException e) {
            log.error("Business error: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.CONFLICT);

        } catch (ResourceNotFoundException e) {
            log.error("Resource not found: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        } catch (Exception e) {
            log.error("Error updating counter staff: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(
            summary = "Get Counter Staff By ID",
            description = "Fetch counter staff details by ID",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'THEATRE_OWNER', 'THEATRE_STAFF')")
    public ResponseEntity<CounterStaff> getCounterStaffById(@PathVariable Long id) {

        try {
            CounterStaff counterStaff = counterStaffService.getCounterStaffById(id);
            return new ResponseEntity<>(counterStaff, HttpStatus.OK);

        } catch (ResourceNotFoundException e) {
            log.error("Counter staff not found: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        } catch (Exception e) {
            log.error("Error fetching counter staff: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(
            summary = "Get All Counter Staff By Theatre",
            description = "Fetch all counter staff for a theatre (ADMIN or THEATRE_OWNER)",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/theatre/{theatreId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'THEATRE_OWNER')")
    public ResponseEntity<List<CounterStaff>> getAllCounterStaff(@PathVariable Long theatreId) {

        try {
            List<CounterStaff> counterStaffList = counterStaffService.getAllCounterStaff(theatreId);
            return new ResponseEntity<>(counterStaffList, HttpStatus.OK);

        } catch (ResourceNotFoundException e) {
            log.error("Theatre not found: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        } catch (AuthorizationException e) {
            log.error("Authorization error: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        } catch (Exception e) {
            log.error("Error fetching counter staff list: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(
            summary = "Deactivate Counter Staff",
            description = "Deactivate a counter staff by ID (ADMIN or THEATRE_OWNER)",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @DeleteMapping("/{id}/deactivate")
    @PreAuthorize("hasAnyRole('ADMIN', 'THEATRE_OWNER')")
    public ResponseEntity<String> deactivateCounterStaff(@PathVariable Long id) {

        try {
            counterStaffService.deactivateCounterStaff(id);
            return new ResponseEntity<>("Counter staff deactivated successfully", HttpStatus.OK);

        } catch (ResourceNotFoundException e) {
            log.error("Counter staff not found: " + e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);

        } catch (BusinessException e) {
            log.error("Business error: " + e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);

        } catch (Exception e) {
            log.error("Error deactivating counter staff: " + e.getMessage());
            return new ResponseEntity<>("Error deactivating counter staff", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(
            summary = "Update Counter Staff Duty Status",
            description = "Update duty status of counter staff",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PatchMapping("/{id}/duty-status")
    @PreAuthorize("hasAnyRole('ADMIN', 'THEATRE_OWNER', 'THEATRE_STAFF')")
    public ResponseEntity<CounterStaff> updateDutyStatus(
            @PathVariable Long id,
            @RequestParam Boolean isOnDuty) {

        try {
            CounterStaffDto updateDto = new CounterStaffDto();
            updateDto.setIsOnDuty(isOnDuty);

            CounterStaff counterStaff = counterStaffService.updateCounterStaffDetails(id, updateDto);
            return new ResponseEntity<>(counterStaff, HttpStatus.OK);

        } catch (ResourceNotFoundException e) {
            log.error("Counter staff not found: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        } catch (Exception e) {
            log.error("Error updating duty status: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(
            summary = "Create Counter Booking",
            description = "Create a booking at counter (THEATRE_STAFF, THEATRE_OWNER, COUNTER_STAFF)",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/create/booking")
    @PreAuthorize("hasAnyRole('THEATRE_STAFF', 'THEATRE_OWNER', 'COUNTER_STAFF')")
    public ResponseEntity<?> createCounterBooking(@Valid @RequestBody CounterBookingDto request) {
        try {
            CounterBookingDto response = counterStaffService.createCounterBooking(request);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("booking", response);
            responseData.put("message", "Booking created successfully");

            return ResponseEntity.status(HttpStatus.CREATED).body(responseData);

        } catch (AuthorizationException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse(e.getMessage(), false));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), false));
        } catch (BookingException | BusinessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(e.getMessage(), false));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("An unexpected error occurred: " + e.getMessage(), false));
        }
    }
}
