package com.simply.Cinema.service.show_and_booking;

import com.simply.Cinema.exception.*;

import java.util.List;

public interface SeatLockService{

    void lockSeats(Long showId, List<Long> seatIds, Long userId)
            throws ResourceNotFoundException, SeatLockException, ValidationException,
            AuthenticationException, AuthorizationException;

    void releaseLockedSeats(Long showId, List<Long> seatNumbers, Long userId)
            throws ResourceNotFoundException, AuthorizationException, ValidationException;

    boolean checkSeatLockStatus(Long showId, Long seatNumber)
            throws ResourceNotFoundException, ValidationException;

    List<String> getLockedSeats(Long showId) throws ResourceNotFoundException;

}

