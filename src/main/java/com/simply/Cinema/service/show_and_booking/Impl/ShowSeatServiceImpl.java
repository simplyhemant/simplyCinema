package com.simply.Cinema.service.show_and_booking.Impl;

import com.simply.Cinema.core.location_and_venue.entity.Seat;
import com.simply.Cinema.core.show_and_booking.Enum.ShowSeatStatus;
import com.simply.Cinema.core.show_and_booking.dto.ShowSeatResponseDto;
import com.simply.Cinema.core.show_and_booking.entity.Show;
import com.simply.Cinema.core.show_and_booking.entity.ShowSeat;
import com.simply.Cinema.core.show_and_booking.repository.ShowRepo;
import com.simply.Cinema.core.show_and_booking.repository.ShowSeatRepo;
import com.simply.Cinema.exception.BookingException;
import com.simply.Cinema.exception.BusinessException;
import com.simply.Cinema.service.show_and_booking.ShowSeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShowSeatServiceImpl implements ShowSeatService {

    private final ShowSeatRepo showSeatRepo;

    @Override
    public List<ShowSeatResponseDto> getSeatsByShow(Long showId) throws BusinessException {
        List<ShowSeat> showSeats = showSeatRepo.findByShow_Id(showId);

        if (showSeats.isEmpty()) {
            throw new BusinessException("No seats found for show id: " + showId);
        }

        return showSeats.stream()
                .map(this::convertToDTO)
                .toList();
    }



    @Override
    public List<ShowSeatResponseDto> getAvailableSeats(Long showId) throws BusinessException {
        List<ShowSeat> availableSeats = showSeatRepo.findByShow_IdAndStatus(showId, ShowSeatStatus.AVAILABLE);

        if (availableSeats.isEmpty()) {
            throw new BusinessException("No available seats found for show id: " + showId);
        }

        return availableSeats.stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    public List<ShowSeatResponseDto> getBookedSeats(Long showId) throws BusinessException {
        List<ShowSeat> bookedSeats = showSeatRepo.findByShow_IdAndStatus(showId, ShowSeatStatus.BOOKED);

        if (bookedSeats.isEmpty()) {
            throw new BusinessException("No booked seats found for show id: " + showId);
        }

        return bookedSeats.stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    public int countTotalSeats(Long showId) throws BusinessException {
        int totalSeats = showSeatRepo.countByShow_Id(showId);

        if (totalSeats == 0) {
            throw new BusinessException("No seats found for show id: " + showId);
        }

        return totalSeats;
    }

    @Override
    public int countAvailableSeats(Long showId) throws BusinessException {
        return showSeatRepo.countByShow_IdAndStatus(showId, ShowSeatStatus.AVAILABLE);
    }

    @Override
    public int countBookedSeats(Long showId) throws BusinessException {
        return showSeatRepo.countByShow_IdAndStatus(showId, ShowSeatStatus.BOOKED);
    }
    private ShowSeatResponseDto convertToDTO(ShowSeat showSeat) {
        Seat seat = showSeat.getSeat();
        ShowSeatResponseDto dto = new ShowSeatResponseDto();
        dto.setId(seat.getId());
        dto.setRowNumber(seat.getRowNumber());
        dto.setSeatNumber(seat.getSeatNumber());
        dto.setSeatType(String.valueOf(seat.getSeatType()));
        dto.setIsActive(seat.getIsActive());
        dto.setCreatedAt(seat.getCreatedAt());
        dto.setStatus(showSeat.getStatus());
        dto.setPrice(showSeat.getPrice());
        return dto;
    }
}
