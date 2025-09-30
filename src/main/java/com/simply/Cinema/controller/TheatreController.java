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

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/theatre")
public class TheatreController {

    private final TheatreService theatreService;

    @PreAuthorize("hasRole('THEATRE_OWNER')")
    @PostMapping("/owner/create")
    public ResponseEntity<TheatreResponseDto> createTheatre(
            @RequestBody TheatreRequestDto requestDto) throws BusinessException {

        log.info("Creating theatre: {}", requestDto);
        TheatreResponseDto created = theatreService.createTheatre(requestDto);
        log.debug("Theatre created successfully: {}", created);
        return ResponseEntity.ok(created);
    }

    @PreAuthorize("hasRole('THEATRE_OWNER')")
    @PutMapping("/owner/update/{theatreId}")
    public ResponseEntity<TheatreResponseDto> updateTheatre(
            @PathVariable Long theatreId,
            @RequestBody TheatreRequestDto requestDto) throws ResourceNotFoundException, BusinessException {

        log.info("Updating theatre with ID: {}", theatreId);
        TheatreResponseDto updateTheatre = theatreService.updateTheatre(theatreId, requestDto);
        log.debug("Theatre updated: {}", updateTheatre);
        return ResponseEntity.ok(updateTheatre);
    }

    @PreAuthorize("hasRole('THEATRE_OWNER')")
    @DeleteMapping("/owner/delete/{theatreId}")
    public ResponseEntity<Void> deleteTheatre(
            @PathVariable Long theatreId) throws ResourceNotFoundException {

        log.info("Deleting theatre with ID: {}", theatreId);
        theatreService.deleteTheatre(theatreId);
        log.info("Theatre deleted successfully: {}", theatreId);
        return ResponseEntity.noContent().build(); //204
    }

    @GetMapping("/{theatreId}")
    public ResponseEntity<TheatreResponseDto> getTheatreById(
            @PathVariable Long theatreId) throws ResourceNotFoundException {

        log.info("Fetching theatre with ID: {}", theatreId);
        TheatreResponseDto dto = theatreService.getTheatreById(theatreId);
        log.debug("Theatre fetched: {}", dto);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/city/{cityId}")
    public ResponseEntity<List<TheatreResponseDto>> getTheatreByCityId(
            @PathVariable Long cityId) throws ResourceNotFoundException {

        log.info("Fetching theatres in city with ID: {}", cityId);
        List<TheatreResponseDto> dto = theatreService.getTheatreByCity(cityId);
        log.debug("Theatres fetched: {}", dto);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/list")
    public ResponseEntity<Page<TheatreResponseDto>> getAllTheatre(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize) {

        log.info("Fetching all theatres, pageNo: {}, pageSize: {}", pageNo, pageSize);
        Page<TheatreResponseDto> theatrePage = theatreService.getAllTheatre(pageNo, pageSize);
        log.debug("Theatres fetched: {}", theatrePage.getContent());
        return ResponseEntity.ok(theatrePage);
    }

    @PreAuthorize("hasRole('THEATRE_OWNER')")
    @GetMapping("/owner/list/{ownerId}")
    public ResponseEntity<List<TheatreResponseDto>> getTheatresByOwner(
            @PathVariable Long ownerId) throws AccessDeniedException {

        Long currentUserId = SecurityUtil.getCurrentUserId();
        boolean isAdmin = SecurityUtil.hasRole("ADMIN");

        log.info("Fetching theatres for ownerId: {}, currentUserId: {}, isAdmin: {}", ownerId, currentUserId, isAdmin);
        List<TheatreResponseDto> theatres = theatreService.getTheatreByOwner(ownerId, currentUserId, isAdmin);
        log.debug("Theatres fetched: {}", theatres);
        return ResponseEntity.ok(theatres);
    }

    @GetMapping("/amenities")
    public ResponseEntity<List<TheatreResponseDto>> getTheatreByAmenities(
            @RequestParam List<String> amenities
    ) {
        log.info("Fetching theatres by amenities: {}", amenities);
        List<TheatreResponseDto> result = theatreService.getTheatreByAmenities(amenities);
        log.debug("Theatres fetched: {}", result);
        return ResponseEntity.ok(result);
    }

    @PreAuthorize("hasRole('THEATRE_OWNER')")
    @PutMapping("/owner/changeTime/{theatreId}")
    public ResponseEntity<TheatreResponseDto> updateTheatreOperatingHours(
            @PathVariable Long theatreId,
            @RequestBody OperatingHoursDto hoursDto
    ) {
        log.info("Updating operating hours for theatreId: {}, hours: {}", theatreId, hoursDto);
        TheatreResponseDto updatedTime = theatreService.updateTheatreOperatingHours(theatreId, hoursDto);
        log.debug("Updated operating hours: {}", updatedTime);
        return ResponseEntity.ok(updatedTime);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<TheatreResponseDto>> searchTheatre(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize) {

        log.info("Searching theatres with keyword: {}, pageNo: {}, pageSize: {}", keyword, pageNo, pageSize);
        Page<TheatreResponseDto> theatrePage = theatreService.searchTheatre(keyword, pageNo, pageSize);
        log.debug("Theatres found: {}", theatrePage.getContent());
        return ResponseEntity.ok(theatrePage);
    }

}
