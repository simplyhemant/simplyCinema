package com.simply.Cinema.controller;

import com.simply.Cinema.core.show_and_booking.dto.BookingDto;
import com.simply.Cinema.core.show_and_booking.dto.BookingResponseDto;
import com.simply.Cinema.exception.*;
import com.simply.Cinema.response.ApiResponse;
import com.simply.Cinema.service.show_and_booking.BookingService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping("/create")
    public ResponseEntity<BookingResponseDto> createBooking(
            @RequestHeader(value = "Authorization", required = false) String jwt,
            @RequestBody BookingDto bookingDto
    ) throws AuthorizationException, BookingException, BusinessException, CouponException, PaymentException {

        // jwt can be null for guest booking
        BookingResponseDto bookingResponse = bookingService.createBooking(bookingDto, jwt);

        return ResponseEntity.ok(bookingResponse);
    }

    @PostMapping("/confirm")
    public ResponseEntity<?> confirmBooking(@RequestBody BookingDto bookingConfirmDto) {
        try {
            BookingResponseDto response = bookingService.confirmBooking(bookingConfirmDto);
            return ResponseEntity.ok(response);
        } catch (AuthorizationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(e.getMessage(), false));
        } catch (BookingException | BusinessException | CouponException | PaymentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(e.getMessage(), false));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("An error occurred: " + e.getMessage(), false));
        }
    }

    
}
