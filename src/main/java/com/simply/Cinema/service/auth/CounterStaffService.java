package com.simply.Cinema.service.auth;

import com.simply.Cinema.core.show_and_booking.dto.BookingDto;
import com.simply.Cinema.core.user.dto.CounterStaffDto;
import com.simply.Cinema.core.user.entity.CounterStaff;
import com.simply.Cinema.exception.AuthorizationException;
import com.simply.Cinema.exception.BusinessException;
import com.simply.Cinema.exception.ResourceNotFoundException;
import com.simply.Cinema.exception.ValidationException;

import java.util.List;

public interface CounterStaffService {

    // Common
    CounterStaff registerCounterStaff(CounterStaffDto request)
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
//    BookingDto createBookingForWalkInCustomer(...);
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
