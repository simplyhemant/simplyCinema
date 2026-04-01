package com.simply.Cinema.controller;

import com.simply.Cinema.core.support.dto.SupportDto;
import com.simply.Cinema.exception.BusinessException;
import com.simply.Cinema.service.support.SupportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/support")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Support Management", description = "Endpoints for support ticket management")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class SupportController {

    private final SupportService supportService;

    @Operation(
            summary = "Create Support Ticket",
            description = "Creates a new support ticket (Accessible by all roles)",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/create")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<SupportDto> createTicket(@RequestBody SupportDto supportDto) throws BusinessException {
        log.info("Request to create support ticket from user: {}", supportDto.getUserId());
        SupportDto created = supportService.createSupportTicket(supportDto);
        return ResponseEntity.ok(created);
    }

    @Operation(
            summary = "Update Ticket Status",
            description = "Updates the status and resolution status of a ticket (Admin only)",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PutMapping("/update/{ticketId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SupportDto> updateStatus(
            @PathVariable Long ticketId,
            @RequestParam String status,
            @RequestParam boolean isResolved) throws BusinessException {
        log.info("Request to update status for ticket: {}", ticketId);
        SupportDto updated = supportService.updateSupportStatus(ticketId, status, isResolved);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SupportDto>> getAllTickets() {
        List<SupportDto> tickets = supportService.getAllTickets();
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<SupportDto>> getTicketsByUser(@PathVariable Long userId) {
        List<SupportDto> tickets = supportService.getTicketsByUserId(userId);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<SupportDto> getTicketById(@PathVariable Long id) throws BusinessException {
        SupportDto ticket = supportService.getTicketById(id);
        return ResponseEntity.ok(ticket);
    }
}
