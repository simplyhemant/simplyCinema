package com.simply.Cinema.service.auth;

import com.simply.Cinema.core.show_and_booking.dto.BookingDto;
import com.simply.Cinema.core.show_and_booking.dto.BookingResponseDto;
import com.simply.Cinema.core.show_and_booking.dto.CounterBookingDto;
import com.simply.Cinema.core.user.dto.CounterStaffDto;
import com.simply.Cinema.core.user.dto.CounterStaffResponseDto;
import com.simply.Cinema.core.user.entity.CounterStaff;
import com.simply.Cinema.exception.*;

import java.util.List;

public interface CounterStaffService {

    // Common
    CounterStaffResponseDto registerCounterStaff(CounterStaffDto request)
            throws AuthorizationException, ValidationException, BusinessException, ResourceNotFoundException;

    CounterStaff updateCounterStaffDetails(Long id, CounterStaffDto request)
            throws ResourceNotFoundException, ValidationException, BusinessException;

    CounterStaff getCounterStaffById(Long id)
            throws ResourceNotFoundException;

    List<CounterStaff> getAllCounterStaff(Long theatreId)
            throws ResourceNotFoundException, AuthorizationException;

    void deactivateCounterStaff(Long id)
            throws ResourceNotFoundException, BusinessException;


    // Booking Staff

    public CounterBookingDto createCounterBooking(CounterBookingDto bookingDto)
            throws BusinessException, BookingException ;

//    void printOrSendTicket(Long bookingId);
//    String generateTicketQRCode(Long bookingId);
//    List<BookingDto> getBookingHistoryForStaff(Long staffId);
//
//    // Verification Staff
//    boolean verifyTicketByBookingIdOrQRCode(String bookingId, String token);
//    void markTicketCheckedIn(Long bookingId);
//    void rejectInvalidOrUsedTicket(Long bookingId);
//    List<BookingDto> getVerificationHistory(Long staffId);
//    void updateTicketsHandledCount(Long staffId);

}
