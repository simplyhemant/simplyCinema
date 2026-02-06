package com.simply.Cinema.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.simply.Cinema.core.location_and_venue.dto.ScreenDto;
import com.simply.Cinema.core.location_and_venue.dto.ScreenSummaryDto;
import com.simply.Cinema.exception.AuthorizationException;
import com.simply.Cinema.exception.BusinessException;
import com.simply.Cinema.exception.ResourceNotFoundException;
import com.simply.Cinema.exception.ValidationException;
import com.simply.Cinema.service.location_and_venue.ScreenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/screens")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Screen API", description = "Operations related to theatre screens management")
public class ScreenController {

    private final ScreenService screenService;

    @Operation(
            summary = "Create Screen",
            description = "Creates a new screen for a theatre (THEATRE_OWNER only)",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/create")
    @PreAuthorize("hasRole('THEATRE_OWNER')")
    public ResponseEntity<ScreenDto> createScreen(@RequestBody ScreenDto screenDto)
            throws BusinessException, ValidationException, ResourceNotFoundException {
        log.info("Creating new screen for theatreId={}", screenDto.getTheatreId());
        ScreenDto created = screenService.createScreen(screenDto);
        log.info("Screen created successfully with ID={}", created.getId());
        return ResponseEntity.ok(created);
    }

    @Operation(
            summary = "Update Screen",
            description = "Updates an existing screen (THEATRE_OWNER only)",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PutMapping("/update/{screenId}")
    @PreAuthorize("hasRole('THEATRE_OWNER')")
    public ResponseEntity<ScreenDto> updateScreen(@PathVariable Long screenId,
                                                  @RequestBody ScreenDto screenDto)
            throws ResourceNotFoundException, ValidationException, AuthorizationException, JsonProcessingException {
        log.info("Updating screen with ID={}", screenId);
        ScreenDto updated = screenService.updateScreen(screenId, screenDto);
        log.info("Screen updated successfully with ID={}", updated.getId());
        return ResponseEntity.ok(updated);
    }

    @Operation(
            summary = "Delete Screen",
            description = "Deletes a screen by ID (THEATRE_OWNER only)",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @DeleteMapping("/{screenId}")
    @PreAuthorize("hasRole('THEATRE_OWNER')")
    public ResponseEntity<Void> deleteScreen(@PathVariable Long screenId)
            throws ResourceNotFoundException, AuthorizationException {
        log.warn("Deleting screen with ID={}", screenId);
        screenService.deleteScreen(screenId);
        log.info("Screen with ID={} deleted successfully", screenId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Get All Screens",
            description = "Fetch all screens (ADMIN only)",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ScreenDto>> getAllScreens() {
        log.debug("Fetching all screens");
        List<ScreenDto> screens = screenService.getAllScreen();
        return ResponseEntity.ok(screens);
    }

    @Operation(
            summary = "Get Screens By Theatre",
            description = "Fetch all screens for a specific theatre (THEATRE_OWNER only)",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/theatre/{theatreId}")
    @PreAuthorize("hasRole('THEATRE_OWNER')")
    public ResponseEntity<List<ScreenDto>> getScreensByTheatre(@PathVariable Long theatreId)
            throws ResourceNotFoundException, AuthorizationException {
        log.debug("Fetching screens for theatreId={}", theatreId);
        List<ScreenDto> screens = screenService.getScreenByTheatre(theatreId);
        return ResponseEntity.ok(screens);
    }

    @Operation(
            summary = "Get Screen Summary",
            description = "Fetch summary details of a screen (THEATRE_OWNER only)",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/{screenId}/summary")
    @PreAuthorize("hasRole('THEATRE_OWNER')")
    public ResponseEntity<ScreenSummaryDto> getScreenSummary(@PathVariable Long screenId)
            throws ResourceNotFoundException {
        log.debug("Fetching summary for screenId={}", screenId);
        ScreenSummaryDto summary = screenService.getScreenSummary(screenId);
        return ResponseEntity.ok(summary);
    }

    @Operation(
            summary = "Get Screen By ID",
            description = "Fetch screen details by screen ID"
    )
    @GetMapping("/{screenId}")
    public ResponseEntity<ScreenDto> getScreenById(@PathVariable Long screenId)
            throws ResourceNotFoundException {
        log.debug("Fetching screen details for screenId={}", screenId);
        ScreenDto screen = screenService.getScreenById(screenId);
        return ResponseEntity.ok(screen);
    }

    @Operation(
            summary = "Deactivate Screen",
            description = "Deactivate a screen (THEATRE_OWNER only)",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PatchMapping("/owner/{screenId}/deactivate")
    @PreAuthorize("hasRole('THEATRE_OWNER')")
    public ResponseEntity<Void> deactivateScreen(@PathVariable Long screenId)
            throws ResourceNotFoundException, AuthorizationException {
        log.warn("Deactivating screen with ID={}", screenId);
        screenService.deactivateScreen(screenId);
        log.info("Screen with ID={} deactivated successfully", screenId);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Activate Screen",
            description = "Activate a screen (THEATRE_OWNER only)",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PatchMapping("/owner/{screenId}/activate")
    @PreAuthorize("hasRole('THEATRE_OWNER')")
    public ResponseEntity<Void> activateScreen(@PathVariable Long screenId)
            throws ResourceNotFoundException, AuthorizationException {
        log.warn("Activating screen with ID={}", screenId);
        screenService.activateScreen(screenId);
        log.info("Screen with ID={} activated successfully", screenId);
        return ResponseEntity.ok().build();
    }
}
