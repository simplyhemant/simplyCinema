package com.simply.Cinema.service.show_and_booking;

import com.simply.Cinema.core.show_and_booking.dto.BookingDto;
import com.simply.Cinema.core.show_and_booking.dto.BookingPaymentDto;
import com.simply.Cinema.core.show_and_booking.dto.BookingResponseDto;
import com.simply.Cinema.exception.*;

import java.util.List;

public interface BookingService {

    BookingResponseDto createBooking(BookingDto bookingDto,String jwt)
            throws AuthorizationException, BookingException, BusinessException, CouponException, PaymentException;

    BookingResponseDto confirmBooking(BookingDto bookingConfirmDto, String jwt)
            throws AuthorizationException, BookingException, BusinessException, CouponException, PaymentException, Exception;

//    BookingResponseDto modifyBooking(Long bookingId, BookingDto bookingDto)
//            throws AuthorizationException, BookingException, BusinessException, CouponException, PaymentException;

    void cancelBooking(Long bookingId)
            throws AuthorizationException, BookingException, BusinessException, PaymentException;

    List<BookingResponseDto> getBookingHistory(Long userId)
            throws AuthorizationException, BookingException;

    BookingResponseDto getBookingDetails(Long bookingId)
            throws AuthorizationException, BookingException;

    BookingPaymentDto processPayment(Long bookingId, BookingPaymentDto paymentDto)
            throws AuthorizationException, PaymentException, BookingException;
}