package com.simply.Cinema.controller;

import com.simply.Cinema.core.show_and_booking.dto.PaymentDto;
import com.simply.Cinema.core.show_and_booking.dto.PaymentResponseDto;
import com.simply.Cinema.exception.BookingException;
import com.simply.Cinema.exception.PaymentException;
import com.simply.Cinema.response.ApiResponse;
import com.simply.Cinema.service.show_and_booking.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/create-link")
    public ResponseEntity<?> createPaymentLink(@RequestBody PaymentDto paymentDto) {
        try {
            PaymentResponseDto response = paymentService.createPaymentLink(paymentDto);
            return ResponseEntity.ok(response);
        } catch (BookingException | PaymentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(e.getMessage(), false));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Payment link creation failed: " + e.getMessage(), false));
        }
    }


    @PostMapping("/verify")
    public ResponseEntity<?> verifyPayment(@RequestBody Map<String, String> payload) {
        try {
            String paymentId = payload.get("razorpay_payment_id");
            String paymentLinkId = payload.get("razorpay_payment_link_id");

            ApiResponse response = paymentService.verifyPayment(paymentId, paymentLinkId);

            if (response.isStatus()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (PaymentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(e.getMessage(), false));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Verification failed: " + e.getMessage(), false));
        }
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<?> getPaymentDetails(@PathVariable Long bookingId) {
        try {
            // Implement this in your service to fetch payment details
            return ResponseEntity.ok(new ApiResponse("Payment details retrieved", true));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), false));
        }
    }

    @PostMapping("/webhook")
    public ResponseEntity<?> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("X-Razorpay-Signature") String signature) {
        try {
            // Implement webhook signature verification and processing
            // This is more secure than relying only on callback
            return ResponseEntity.ok(new ApiResponse("Webhook processed", true));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse("Webhook processing failed", false));
        }
    }

}
