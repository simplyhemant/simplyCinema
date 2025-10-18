package com.simply.Cinema.service.show_and_booking;

import com.razorpay.Payment;
import com.simply.Cinema.core.show_and_booking.dto.PaymentDto;
import com.simply.Cinema.core.show_and_booking.dto.PaymentResponseDto;
import com.simply.Cinema.exception.BookingException;
import com.simply.Cinema.exception.PaymentException;
import com.simply.Cinema.response.ApiResponse;

import java.util.List;

public interface PaymentService {

//    PaymentResponseDto processPayment(PaymentDto paymentDto) throws PaymentException;

    //Payment refundPayment(Long paymentId) throws PaymentException;

    List<Payment> getPaymentHistory(Long userId) throws PaymentException;

    PaymentResponseDto getPaymentDetails(Long paymentId) throws PaymentException;

    List<String> managePaymentMethods(Long userId) throws PaymentException;

//    PaymentResponseDto createBooking(User user, PaymentDto paymentDto) throws PaymentException;

    // Create Razorpay payment link for a booking
    PaymentResponseDto createPaymentLink(PaymentDto paymentDto)
            throws BookingException, PaymentException, Exception;

    // Verify payment and confirm booking


    ApiResponse verifyPayment(String paymentId, String paymentLinkId)
            throws BookingException, PaymentException, Exception;

    //ApiResponse verifyPayment(String paymentId, String paymentLinkId) throws PaymentException;

}
