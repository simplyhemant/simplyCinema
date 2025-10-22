package com.simply.Cinema.service.show_and_booking.Impl;

import com.razorpay.Payment;
import com.razorpay.PaymentLink;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.simply.Cinema.core.show_and_booking.dto.BookingResponseDto;
import com.simply.Cinema.core.show_and_booking.dto.PaymentDto;
import com.simply.Cinema.core.show_and_booking.dto.PaymentResponseDto;
import com.simply.Cinema.exception.BookingException;
import com.simply.Cinema.exception.PaymentException;
import com.simply.Cinema.response.ApiResponse;
import com.simply.Cinema.service.RedisService;
import com.simply.Cinema.service.show_and_booking.PaymentService;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final RedisService redisService;

    private final RazorpayClient razorpayClient;

    @Override
    public List<Payment> getPaymentHistory(Long userId) throws PaymentException {
        return List.of();
    }

    @Override
    public PaymentResponseDto getPaymentDetails(Long paymentId) throws PaymentException {
        return null;
    }

    @Override
    public List<String> managePaymentMethods(Long userId) throws PaymentException {
        return List.of();
    }

    @Override
    public PaymentResponseDto createPaymentLink(PaymentDto paymentDto) throws BookingException, PaymentException,Exception {

        String bookingKey = "temp_booking:" + paymentDto.getEmail() + ":" + paymentDto.getShowId();
        BookingResponseDto tempBooking = redisService.get(bookingKey, BookingResponseDto.class);

        if(tempBooking == null){
            throw new BookingException("Temporary booking not found or expired");
        }

        try{

            JSONObject request = new JSONObject();
            request.put("amount", (int)(tempBooking.getFinalAmount() * 100));
            request.put("currency", "INR");
            request.put("description", "SimplyCinema Booking Payment");

            JSONObject customer = new JSONObject();
            customer.put("email", tempBooking.getEmail());
            request.put("customer", customer);

            JSONObject notify = new JSONObject();
            notify.put("sms", false);
            notify.put("email", true);
            request.put("notify", notify);

            request.put("callback_url", "http://localhost:3000/api/payment/verify");
//            request.put("callback_method", "post");

            request.put("reminder_enable", true);

            PaymentLink link = razorpayClient.paymentLink.create(request);

            PaymentResponseDto response = new PaymentResponseDto();
            response.setPaymentLinkId(link.get("id"));
            response.setPaymentUrl(link.get("short_url"));
            response.setAmount(tempBooking.getFinalAmount());
            response.setBookingId(tempBooking.getBookingId());
            response.setPaymentId(null);
            response.setStatus(null);
            response.setMethod(paymentDto.getMethod());
            response.setCreatedAt(LocalDateTime.now());

            // Optionally save link info in Redis for verification
            String linkKey = "payment_link:" + link.get("id");
            redisService.set(linkKey, tempBooking, 600); // 10 min TTL

            return response;

        } catch (RazorpayException e) {
            throw new PaymentException("Razorpay error: " + e.getMessage());
        }
    }


    @Override
    public ApiResponse verifyPayment(String paymentId, String paymentLinkId) throws PaymentException {
        if (paymentId == null || paymentLinkId == null) {
            throw new PaymentException("Payment ID or Payment Link ID cannot be null");
        }

        PaymentLink paymentLink;
        try {
            paymentLink = razorpayClient.paymentLink.fetch(paymentLinkId);
        } catch (RazorpayException e) {
            throw new PaymentException("Failed to fetch payment details from Razorpay: " + e.getMessage());
        }

        String status = paymentLink.get("status");
        if ("paid".equalsIgnoreCase(status)) {
            return new ApiResponse("Payment completed successfully", true);

        } else if ("pending".equalsIgnoreCase(status)) {
            return new ApiResponse("Payment not completed yet", false);

        } else {
            return new ApiResponse("Payment failed or cancelled", false);
        }
    }





    private Long extractShowIdFromLinkId(String linkId) {
        // Your logic to map paymentLinkId → showId
        return null; // implement according to your Redis or DB mapping
    }
}
