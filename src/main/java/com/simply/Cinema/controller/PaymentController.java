package com.simply.Cinema.controller;

import com.simply.Cinema.core.show_and_booking.dto.PaymentDto;
import com.simply.Cinema.core.show_and_booking.dto.PaymentResponseDto;
import com.simply.Cinema.exception.BookingException;
import com.simply.Cinema.exception.PaymentException;
import com.simply.Cinema.response.ApiResponse;
import com.simply.Cinema.service.show_and_booking.PaymentService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payment")
@Tag(name = "Payment API", description = "Operations related to payment processing and verification")
public class PaymentController {

    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    @Autowired
    private PaymentService paymentService;

    @Operation(
            summary = "Create Payment Link",
            description = "Creates a Razorpay payment link for a booking"
    )
    @PostMapping("/create-link")
    public ResponseEntity<?> createPaymentLink(@RequestBody PaymentDto paymentDto) {
        logger.info("💳 [CREATE PAYMENT LINK] Request received to create payment link.");
        logger.debug("📦 Payment DTO: {}", paymentDto);

        try {
            PaymentResponseDto response = paymentService.createPaymentLink(paymentDto);
            logger.info("✅ [CREATE PAYMENT LINK] Payment link created successfully. Link ID: {}", response.getPaymentLinkId());
            return ResponseEntity.ok(response);
        } catch (BookingException | PaymentException e) {
            logger.error("⚠️ [CREATE PAYMENT LINK] Business or Payment exception: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(e.getMessage(), false));
        } catch (Exception e) {
            logger.error("💥 [CREATE PAYMENT LINK] Unexpected error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Payment link creation failed: " + e.getMessage(), false));
        }
    }

    @Operation(
            summary = "Verify Payment",
            description = "Verifies Razorpay payment using payment ID and payment link ID"
    )
    @PostMapping("/verify")
    public ResponseEntity<?> verifyPayment(@RequestBody Map<String, String> payload) {
        logger.info("🧾 [VERIFY PAYMENT] Verification request received.");
        logger.debug("📦 Payload: {}", payload);

        try {
            String paymentId = payload.get("razorpay_payment_id");
            String paymentLinkId = payload.get("razorpay_payment_link_id");
            logger.info("🔍 [VERIFY PAYMENT] Payment ID: {}, Link ID: {}", paymentId, paymentLinkId);

            ApiResponse response = paymentService.verifyPayment(paymentId, paymentLinkId);

            if (response.isStatus()) {
                logger.info("✅ [VERIFY PAYMENT] Payment verified successfully for Payment ID: {}", paymentId);
                return ResponseEntity.ok(response);
            } else {
                logger.warn("⚠️ [VERIFY PAYMENT] Payment verification failed for Payment ID: {}", paymentId);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (PaymentException e) {
            logger.error("❌ [VERIFY PAYMENT] Payment exception occurred: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(e.getMessage(), false));
        } catch (Exception e) {
            logger.error("💥 [VERIFY PAYMENT] Unexpected error during verification: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Verification failed: " + e.getMessage(), false));
        }
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
            summary = "Handle Razorpay Webhook",
            description = "Processes webhook events received from Razorpay for secure payment status updates"
    )
    @PostMapping("/webhook")
    public ResponseEntity<?> handleWebhook(
            @RequestBody String payload,
            @RequestHeader(name = "X-Razorpay-Signature") String signature) {
        logger.info("📩 [WEBHOOK] Webhook event received from Razorpay.");
        logger.debug("📦 Payload: {}", payload);
        logger.debug("🔑 Signature: {}", signature);

        try {
            logger.info("✅ [WEBHOOK] Webhook processed successfully.");
            return ResponseEntity.ok(new ApiResponse("Webhook processed", true));
        } catch (Exception e) {
            logger.error("💥 [WEBHOOK] Webhook processing failed: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse("Webhook processing failed", false));
        }
    }
}
