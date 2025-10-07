package com.simply.Cinema.service.show_and_booking;

import com.simply.Cinema.core.show_and_booking.entity.Booking;
import com.simply.Cinema.exception.AuthorizationException;
import com.simply.Cinema.exception.BookingException;
import com.simply.Cinema.exception.BusinessException;

import java.util.List;

public interface BookingService {

    // Create a new booking
    Booking createBooking(BookingDto bookingDto)
            throws AuthorizationException, BookingException, BusinessException, CouponException, PaymentException;

    // Modify an existing booking
    Booking modifyBooking(Long bookingId, BookingDto bookingDto)
            throws AuthorizationException, BookingException, BusinessException, CouponException, PaymentException;

    // Cancel a booking
    void cancelBooking(Long bookingId)
            throws AuthorizationException, BookingException, BusinessException, PaymentException;

    // Get booking history for a user
    List<Booking> getBookingHistory(Long userId)
            throws AuthorizationException, BookingException;

    // Get details of a specific booking
    Booking getBookingDetails(Long bookingId)
            throws AuthorizationException, BookingException;
}
