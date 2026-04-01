package com.simply.Cinema.controller;

import com.simply.Cinema.core.location_and_venue.dto.OperatingHoursDto;
import com.simply.Cinema.core.location_and_venue.dto.TheatreRequestDto;
import com.simply.Cinema.core.location_and_venue.dto.TheatreResponseDto;
import com.simply.Cinema.exception.BusinessException;
import com.simply.Cinema.exception.ResourceNotFoundException;
import com.simply.Cinema.service.location_and_venue.TheatreService;
import com.simply.Cinema.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/theatre")
@Tag(name = "Theatre API", description = "Operations related to theatre management and search")
public class TheatreController {

        private final TheatreService theatreService;

        @Operation(summary = "Create Theatre", description = "Create a new theatre (THEATRE_OWNER only)", security = @SecurityRequirement(name = "bearerAuth"))
        @PreAuthorize("hasAnyRole('THEATRE_OWNER', 'ADMIN')")
        @PostMapping("/owner/create")
        public ResponseEntity<TheatreResponseDto> createTheatre(
                        @RequestBody TheatreRequestDto requestDto) throws BusinessException {

                log.info("Creating theatre: {}", requestDto);
                TheatreResponseDto created = theatreService.createTheatre(requestDto);
                log.debug("Theatre created successfully: {}", created);
                return ResponseEntity.ok(created);
        }

        @Operation(summary = "Update Theatre", description = "Update theatre details by ID (THEATRE_OWNER only)", security = @SecurityRequirement(name = "bearerAuth"))
        @PreAuthorize("hasAnyRole('THEATRE_OWNER', 'ADMIN')")
        @PutMapping("/owner/update/{theatreId}")
        public ResponseEntity<TheatreResponseDto> updateTheatre(
                        @PathVariable(name = "theatreId") Long theatreId,
                        @RequestBody TheatreRequestDto requestDto)
                        throws ResourceNotFoundException, BusinessException {

                log.info("Updating theatre with ID: {}", theatreId);
                TheatreResponseDto updateTheatre = theatreService.updateTheatre(theatreId, requestDto);
                log.debug("Theatre updated: {}", updateTheatre);
                return ResponseEntity.ok(updateTheatre);
        }

        @Operation(summary = "Delete Theatre", description = "Delete a theatre by ID (THEATRE_OWNER only)", security = @SecurityRequirement(name = "bearerAuth"))
        @PreAuthorize("hasAnyRole('THEATRE_OWNER', 'ADMIN')")
        @DeleteMapping("/owner/delete/{theatreId}")
        public ResponseEntity<Void> deleteTheatre(
                        @PathVariable(name = "theatreId") Long theatreId) throws ResourceNotFoundException {

                log.info("Deleting theatre with ID: {}", theatreId);
                theatreService.deleteTheatre(theatreId);
                log.info("Theatre deleted successfully: {}", theatreId);
                return ResponseEntity.noContent().build();
        }

        @Operation(summary = "Get Theatre By ID", description = "Fetch theatre details using theatre ID")
        @GetMapping("/{theatreId}")
        public ResponseEntity<TheatreResponseDto> getTheatreById(
                        @PathVariable(name = "theatreId") Long theatreId) throws ResourceNotFoundException {

                log.info("Fetching theatre with ID: {}", theatreId);
                TheatreResponseDto dto = theatreService.getTheatreById(theatreId);
                log.debug("Theatre fetched: {}", dto);
                return ResponseEntity.ok(dto);
        }

        @Operation(summary = "Get Theatres By City", description = "Fetch all theatres within a specific city")
        @GetMapping("/city/{cityId}")
        public ResponseEntity<List<TheatreResponseDto>> getTheatreByCityId(
                        @PathVariable(name = "cityId") Long cityId) throws ResourceNotFoundException {

                log.info("Fetching theatres in city with ID: {}", cityId);
                List<TheatreResponseDto> dto = theatreService.getTheatreByCity(cityId);
                log.debug("Theatres fetched: {}", dto);
                return ResponseEntity.ok(dto);
        }

        @Operation(summary = "Get All Theatres (Paginated)", description = "Fetch paginated list of all theatres")
        @GetMapping("/list")
        public ResponseEntity<Page<TheatreResponseDto>> getAllTheatre(
                        @RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
                        @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {

                log.info("Fetching all theatres, pageNo: {}, pageSize: {}", pageNo, pageSize);
                Page<TheatreResponseDto> theatrePage = theatreService.getAllTheatre(pageNo, pageSize);
                log.debug("Theatres fetched: {}", theatrePage.getContent());
                return ResponseEntity.ok(theatrePage);
        }

        @Operation(summary = "Get Theatres By Owner", description = "Fetch theatres belonging to a specific owner (THEATRE_OWNER only)", security = @SecurityRequirement(name = "bearerAuth"))
        @PreAuthorize("hasAnyRole('THEATRE_OWNER', 'ADMIN')")
        @GetMapping("/owner/list/{ownerId}")
        public ResponseEntity<List<TheatreResponseDto>> getTheatresByOwner(
                        @PathVariable(name = "ownerId") Long ownerId) throws AccessDeniedException {

                Long currentUserId = SecurityUtil.getCurrentUserId();
                boolean isAdmin = SecurityUtil.hasRole("ADMIN");

                log.info("Fetching theatres for ownerId: {}, currentUserId: {}, isAdmin: {}", ownerId, currentUserId,
                                isAdmin);
                List<TheatreResponseDto> theatres = theatreService.getTheatreByOwner(ownerId, currentUserId, isAdmin);
                log.debug("Theatres fetched: {}", theatres);
                return ResponseEntity.ok(theatres);
        }

        @Operation(summary = "Get Theatres By Amenities", description = "Fetch theatres filtered by amenities")
        @GetMapping("/amenities")
        public ResponseEntity<List<TheatreResponseDto>> getTheatreByAmenities(
                        @RequestParam(name = "amenities") List<String> amenities) {

                log.info("Fetching theatres by amenities: {}", amenities);
                List<TheatreResponseDto> result = theatreService.getTheatreByAmenities(amenities);
                log.debug("Theatres fetched: {}", result);
                return ResponseEntity.ok(result);
        }

        @Operation(summary = "Update Theatre Operating Hours", description = "Update operating hours of a theatre (THEATRE_OWNER only)", security = @SecurityRequirement(name = "bearerAuth"))
        @PreAuthorize("hasAnyRole('THEATRE_OWNER', 'ADMIN')")
        @PutMapping("/owner/changeTime/{theatreId}")
        public ResponseEntity<TheatreResponseDto> updateTheatreOperatingHours(
                        @PathVariable(name = "theatreId") Long theatreId,
                        @RequestBody OperatingHoursDto hoursDto) {

                log.info("Updating operating hours for theatreId: {}, hours: {}", theatreId, hoursDto);
                TheatreResponseDto updatedTime = theatreService.updateTheatreOperatingHours(theatreId, hoursDto);
                log.debug("Updated operating hours: {}", updatedTime);
                return ResponseEntity.ok(updatedTime);
        }

        @Operation(summary = "Search Theatres", description = "Search theatres using keyword with pagination")
        @GetMapping("/search")
        public ResponseEntity<Page<TheatreResponseDto>> searchTheatre(
                        @RequestParam(name = "keyword") String keyword,
                        @RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
                        @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {

                log.info("Searching theatres with keyword: {}, pageNo: {}, pageSize: {}", keyword, pageNo, pageSize);
                Page<TheatreResponseDto> theatrePage = theatreService.searchTheatre(keyword, pageNo, pageSize);
                log.debug("Theatres found: {}", theatrePage.getContent());
                return ResponseEntity.ok(theatrePage);
        }
}
