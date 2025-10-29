package com.simply.Cinema.controller;

import com.simply.Cinema.core.show_and_booking.dto.BookingDto;
import com.simply.Cinema.core.show_and_booking.dto.BookingResponseDto;
import com.simply.Cinema.exception.*;
import com.simply.Cinema.response.ApiResponse;
import com.simply.Cinema.service.show_and_booking.BookingService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private static final Logger logger = LoggerFactory.getLogger(BookingController.class);

    private final BookingService bookingService;

    @PostMapping("/create")
    public ResponseEntity<BookingResponseDto> createBooking(
            @RequestHeader(value = "Authorization", required = false) String jwt,
            @RequestBody BookingDto bookingDto
    ) throws AuthorizationException, BookingException, BusinessException, CouponException, PaymentException {

        logger.info("🎬 [CREATE BOOKING] Request received for booking creation. JWT: {}", jwt != null ? "Present" : "Absent");
        logger.debug("📦 Booking Request Data: {}", bookingDto);

        BookingResponseDto bookingResponse = bookingService.createBooking(bookingDto, jwt);

        logger.info("✅ [CREATE BOOKING] Booking created successfully. Booking ID: {}", bookingResponse.getBookingId());
        return ResponseEntity.ok(bookingResponse);
    }

    @PostMapping("/confirm")
    public ResponseEntity<?> confirmBooking(@RequestBody BookingDto bookingConfirmDto) {
        logger.info("🎟️ [CONFIRM BOOKING] Request received to confirm booking.");
        logger.debug("📦 Booking Confirmation Data: {}", bookingConfirmDto);

        try {
            BookingResponseDto response = bookingService.confirmBooking(bookingConfirmDto);
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

}
