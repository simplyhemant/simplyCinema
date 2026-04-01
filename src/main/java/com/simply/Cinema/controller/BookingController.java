package com.simply.Cinema.controller;

import com.simply.Cinema.core.show_and_booking.dto.BookingDto;
import com.simply.Cinema.core.show_and_booking.dto.BookingResponseDto;
import com.simply.Cinema.exception.*;
import com.simply.Cinema.response.ApiResponse;
import com.simply.Cinema.service.show_and_booking.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@Tag(name = "Booking API", description = "Operations related to movie ticket bookings")
public class BookingController {

    private static final Logger logger = LoggerFactory.getLogger(BookingController.class);

    private final BookingService bookingService;

    @Operation(
            summary = "Create Booking",
            description = "Creates a new booking for a movie show. JWT token is optional."
    )
    @PostMapping("/create")
    public ResponseEntity<BookingResponseDto> createBooking(
            @RequestHeader(name = "Authorization", required = false) String jwt,
            @RequestBody BookingDto bookingDto
    ) throws AuthorizationException, BookingException, BusinessException, CouponException, PaymentException {

        logger.info("🎬 [CREATE BOOKING] Request received for booking creation. JWT: {}", jwt != null ? "Present" : "Absent");
        logger.debug("📦 Booking Request Data: {}", bookingDto);

        BookingResponseDto bookingResponse = bookingService.createBooking(bookingDto, jwt);

        logger.info("✅ [CREATE BOOKING] Booking created successfully. Booking ID: {}", bookingResponse.getBookingId());
        return ResponseEntity.ok(bookingResponse);
    }

    @Operation(
            summary = "Get Payment Details By Booking ID",
            description = "Fetch payment details associated with a specific booking ID"
    )
    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<?> getPaymentDetails(@PathVariable(name = "bookingId") Long bookingId) {
        logger.info("📘 [GET PAYMENT DETAILS] Request received for Booking ID: {}", bookingId);
        try {
            logger.info("✅ [GET PAYMENT DETAILS] Successfully retrieved payment details for Booking ID: {}", bookingId);
            return ResponseEntity.ok(new ApiResponse("Payment details retrieved", true));
        } catch (Exception e) {
            logger.error("❌ [GET PAYMENT DETAILS] Failed to retrieve payment details for Booking ID: {}. Error: {}", bookingId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                     .body(new ApiResponse(e.getMessage(), false));
        }
    }

    @Operation(
            summary = "Confirm Booking",
            description = "Confirms an existing booking and processes payment. Requires JWT token."
    )
    @PostMapping("/confirm")
    public ResponseEntity<?> confirmBooking(
            @RequestBody BookingDto bookingConfirmDto,
            @RequestHeader(name = "Authorization") String jwt
    ) {
        logger.info("🎟️ [CONFIRM BOOKING] Request received to confirm booking.");
        logger.debug("📦 Booking Confirmation Data: {}", bookingConfirmDto);

        try {
            BookingResponseDto response = bookingService.confirmBooking(bookingConfirmDto, jwt);

            logger.info("✅ [CONFIRM BOOKING] Booking confirmed successfully. Booking ID: {}", response.getBookingId());

            return ResponseEntity.ok(response);

        } catch (AuthorizationException e) {
            logger.error("❌ [CONFIRM BOOKING] Authorization failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(e.getMessage(), false));

        } catch (BookingException | BusinessException | CouponException | PaymentException e) {
            logger.error("⚠️ [CONFIRM BOOKING] Booking-related exception: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(e.getMessage(), false));

        } catch (Exception e) {
            logger.error("💥 [CONFIRM BOOKING] Unexpected error occurred: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("An error occurred: " + e.getMessage(), false));
        }
    }

    @Operation(
            summary = "Get Booking History",
            description = "Retrieves a list of all non-pending bookings for the specified user ID"
    )
    @GetMapping("/user/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<BookingResponseDto>> getBookingHistory(@PathVariable Long userId) throws AuthorizationException, BookingException {
        logger.info("📋 [GET BOOKING HISTORY] Request received for User ID: {}", userId);
        List<BookingResponseDto> history = bookingService.getBookingHistory(userId);
        return ResponseEntity.ok(history);
    }

    @Operation(
            summary = "Get Booking Details",
            description = "Retrieves detailed information about a specific booking by ID"
    )
    @GetMapping("/{bookingId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BookingResponseDto> getBookingDetails(@PathVariable Long bookingId) throws AuthorizationException, BookingException {
        logger.info("📄 [GET BOOKING DETAILS] Request received for Booking ID: {}", bookingId);
        BookingResponseDto details = bookingService.getBookingDetails(bookingId);
        return ResponseEntity.ok(details);
    }
}
