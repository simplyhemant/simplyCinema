package com.simply.Cinema.controller;

import com.simply.Cinema.core.user.dto.CounterStaffDto;
import com.simply.Cinema.core.user.entity.CounterStaff;
import com.simply.Cinema.exception.AuthorizationException;
import com.simply.Cinema.exception.BusinessException;
import com.simply.Cinema.exception.ResourceNotFoundException;
import com.simply.Cinema.exception.ValidationException;
import com.simply.Cinema.service.auth.CounterStaffService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/counter-staff")
@RequiredArgsConstructor
@Slf4j
public class CounterStaffController {

    private final CounterStaffService counterStaffService;

    //@PreAuthorize("hasRole('THEATRE_OWNER')")


    @PostMapping("/register")
    @PreAuthorize("hasAnyRole('ADMIN', 'THEATRE_ADMIN')")
    public ResponseEntity<CounterStaff> registerCounterStaff(@Valid @RequestBody CounterStaffDto request) {

        try {
            CounterStaff counterStaff = counterStaffService.registerCounterStaff(request);
            return new ResponseEntity<CounterStaff>(counterStaff, HttpStatus.CREATED);

        } catch (ValidationException e) {
            log.error("Validation error: " + e.getMessage());
            return new ResponseEntity<CounterStaff>(HttpStatus.BAD_REQUEST);

        } catch (BusinessException e) {
            log.error("Business error: " + e.getMessage());
            return new ResponseEntity<CounterStaff>(HttpStatus.CONFLICT);

        } catch (ResourceNotFoundException e) {
            log.error("Resource not found: " + e.getMessage());
            return new ResponseEntity<CounterStaff>(HttpStatus.NOT_FOUND);

        } catch (AuthorizationException e) {
            log.error("Authorization error: " + e.getMessage());
            return new ResponseEntity<CounterStaff>(HttpStatus.FORBIDDEN);

        } catch (Exception e) {
            log.error("Error registering counter staff: " + e.getMessage());
            return new ResponseEntity<CounterStaff>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
//    @PreAuthorize("hasAnyRole('ADMIN', 'THEATRE_ADMIN')")
    public ResponseEntity<CounterStaff> updateCounterStaff(
            @PathVariable Long id,
            @Valid @RequestBody CounterStaffDto request) {

        try {
            CounterStaff counterStaff = counterStaffService.updateCounterStaffDetails(id, request);
            return new ResponseEntity<CounterStaff>(counterStaff, HttpStatus.OK);

        } catch (ValidationException e) {
            log.error("Validation error: " + e.getMessage());
            return new ResponseEntity<CounterStaff>(HttpStatus.BAD_REQUEST);

        } catch (BusinessException e) {
            log.error("Business error: " + e.getMessage());
            return new ResponseEntity<CounterStaff>(HttpStatus.CONFLICT);

        } catch (ResourceNotFoundException e) {
            log.error("Resource not found: " + e.getMessage());
            return new ResponseEntity<CounterStaff>(HttpStatus.NOT_FOUND);

        } catch (Exception e) {
            log.error("Error updating counter staff: " + e.getMessage());
            return new ResponseEntity<CounterStaff>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
//    @PreAuthorize("hasAnyRole('ADMIN', 'THEATRE_ADMIN', 'COUNTER_STAFF')")
    public ResponseEntity<CounterStaff> getCounterStaffById(@PathVariable Long id) {

        try {
            CounterStaff counterStaff = counterStaffService.getCounterStaffById(id);
            return new ResponseEntity<CounterStaff>(counterStaff, HttpStatus.OK);

        } catch (ResourceNotFoundException e) {
            log.error("Counter staff not found: " + e.getMessage());
            return new ResponseEntity<CounterStaff>(HttpStatus.NOT_FOUND);

        } catch (Exception e) {
            log.error("Error fetching counter staff: " + e.getMessage());
            return new ResponseEntity<CounterStaff>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/theatre/{theatreId}")
//    @PreAuthorize("hasAnyRole('ADMIN', 'THEATRE_ADMIN')")
    public ResponseEntity<List<CounterStaff>> getAllCounterStaff(@PathVariable Long theatreId) {

        try {
            List<CounterStaff> counterStaffList = counterStaffService.getAllCounterStaff(theatreId);
            return new ResponseEntity<List<CounterStaff>>(counterStaffList, HttpStatus.OK);

        } catch (ResourceNotFoundException e) {
            log.error("Theatre not found: " + e.getMessage());
            return new ResponseEntity<List<CounterStaff>>(HttpStatus.NOT_FOUND);

        } catch (AuthorizationException e) {
            log.error("Authorization error: " + e.getMessage());
            return new ResponseEntity<List<CounterStaff>>(HttpStatus.FORBIDDEN);

        } catch (Exception e) {
            log.error("Error fetching counter staff list: " + e.getMessage());
            return new ResponseEntity<List<CounterStaff>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}/deactivate")
    @PreAuthorize("hasAnyRole('ADMIN', 'THEATRE_ADMIN')")
    public ResponseEntity<String> deactivateCounterStaff(@PathVariable Long id) {

        try {
            counterStaffService.deactivateCounterStaff(id);
            return new ResponseEntity<String>("Counter staff deactivated successfully", HttpStatus.OK);

        } catch (ResourceNotFoundException e) {
            log.error("Counter staff not found: " + e.getMessage());
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);

        } catch (BusinessException e) {
            log.error("Business error: " + e.getMessage());
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.CONFLICT);

        } catch (Exception e) {
            log.error("Error deactivating counter staff: " + e.getMessage());
            return new ResponseEntity<String>("Error deactivating counter staff", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/{id}/duty-status")
    @PreAuthorize("hasAnyRole('ADMIN', 'THEATRE_ADMIN', 'COUNTER_STAFF')")
    public ResponseEntity<CounterStaff> updateDutyStatus(
            @PathVariable Long id,
            @RequestParam Boolean isOnDuty) {

        try {
            CounterStaffDto updateDto = new CounterStaffDto();
            updateDto.setIsOnDuty(isOnDuty);

            CounterStaff counterStaff = counterStaffService.updateCounterStaffDetails(id, updateDto);
            return new ResponseEntity<CounterStaff>(counterStaff, HttpStatus.OK);

        } catch (ResourceNotFoundException e) {
            log.error("Counter staff not found: " + e.getMessage());
            return new ResponseEntity<CounterStaff>(HttpStatus.NOT_FOUND);

        } catch (Exception e) {
            log.error("Error updating duty status: " + e.getMessage());
            return new ResponseEntity<CounterStaff>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}