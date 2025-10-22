package com.simply.Cinema.service.show_and_booking;

import com.simply.Cinema.core.location_and_venue.entity.Seat;
import com.simply.Cinema.core.show_and_booking.dto.ShowSeatResponseDto;
import com.simply.Cinema.core.show_and_booking.entity.ShowSeat;
import com.simply.Cinema.exception.BookingException;
import com.simply.Cinema.exception.BusinessException;

import java.util.List;

public interface ShowSeatService {

    List<ShowSeatResponseDto> getSeatsByShow(Long showId) throws BusinessException;

    List<ShowSeatResponseDto> getAvailableSeats(Long showId) throws BusinessException;

    List<ShowSeatResponseDto> getBookedSeats(Long showId) throws BusinessException;

    int countTotalSeats(Long showId) throws BusinessException;

    int countAvailableSeats(Long showId) throws BusinessException;

    int countBookedSeats(Long showId) throws BusinessException;
}
