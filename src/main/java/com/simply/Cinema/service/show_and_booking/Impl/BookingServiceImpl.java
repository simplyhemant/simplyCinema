package com.simply.Cinema.service.show_and_booking.Impl;

import com.simply.Cinema.config.CustomUserDetails;
import com.simply.Cinema.core.location_and_venue.entity.Seat;
import com.simply.Cinema.core.location_and_venue.repository.SeatRepo;
import com.simply.Cinema.core.show_and_booking.Enum.BookingStatus;
import com.simply.Cinema.core.show_and_booking.Enum.ShowSeatStatus;
import com.simply.Cinema.core.show_and_booking.dto.BookingDto;
import com.simply.Cinema.core.show_and_booking.dto.BookingPaymentDto;
import com.simply.Cinema.core.show_and_booking.dto.BookingResponseDto;
import com.simply.Cinema.core.show_and_booking.entity.Booking;
import com.simply.Cinema.core.show_and_booking.entity.Show;
import com.simply.Cinema.core.show_and_booking.entity.ShowSeat;
import com.simply.Cinema.core.show_and_booking.repository.ShowRepo;
import com.simply.Cinema.core.show_and_booking.repository.ShowSeatRepo;
import com.simply.Cinema.core.user.dto.UserProfileDto;
import com.simply.Cinema.core.user.entity.User;
import com.simply.Cinema.core.user.repository.UserRepo;
import com.simply.Cinema.exception.*;
import com.simply.Cinema.security.jwt.JwtProvider;
import com.simply.Cinema.service.UserService.UserService;
import com.simply.Cinema.service.show_and_booking.BookingService;
import com.simply.Cinema.service.show_and_booking.SeatLockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final JwtProvider jwtProvider;
    private final UserRepo userRepo;
    private final ShowRepo showRepo;
    private final ShowSeatRepo showSeatRepo;
    private final SeatLockService seatLockService;
    private final UserService userService;

    @Override
    public BookingResponseDto createBooking(BookingDto bookingDto, String jwt)
            throws AuthorizationException, BookingException, BusinessException, CouponException, PaymentException {

        User user = null;
        Long userId;
        String email;

        // 1️⃣ Check if JWT is provided
        if (jwt != null && !jwt.isEmpty()) {
            UserProfileDto userDto = userService.findUserBYJwtToken(jwt);
            userId = userDto.getId();
            user = userRepo.findById(userId)
                    .orElseThrow(() -> new UserException("User not found with ID: " + userId));
            email = user.getEmail();
        } else {
            userId = null;
            // Guest booking: email must be provided in BookingDto
            if (bookingDto.getEmail() == null || bookingDto.getEmail().isEmpty()) {
                throw new BookingException("Email is required for guest booking");
            }
            email = bookingDto.getEmail();
        }

        // 2️⃣ Get Show entity
        Long showId = bookingDto.getShowId();
        Show show = showRepo.findById(showId)
                .orElseThrow(() -> new BookingException("Show not found with ID: " + showId));

        // 3️⃣ Lock seats
        List<Long> seatIds = bookingDto.getSeatIds();
        seatLockService.lockSeats(showId, seatIds, userId); // userId can be null for guest

        // 4️⃣ Fetch ShowSeat entities
        List<ShowSeat> showSeats = showSeatRepo.findByShowAndSeatIds(show, seatIds);
        if (showSeats.size() != seatIds.size()) {
            throw new BookingException("Some seats are not available for booking");
        }

        // 5️⃣ Calculate total and final amount
        double totalAmount = showSeats.stream().mapToDouble(ShowSeat::getPrice).sum();
        double discountAmount = 0.0; // add logic if coupon applied
        double finalAmount = totalAmount - discountAmount;

        // 6️⃣ Create temporary booking object
        Booking tempBooking = new Booking();
        tempBooking.setShow(show);
        tempBooking.setUser(user); // null if guest
        tempBooking.setTotalAmount(totalAmount);
        tempBooking.setBookingStatus(BookingStatus.PENDING);
        tempBooking.setEmail(email);
        tempBooking.setCreatedAt(LocalDateTime.now());

        // 7️⃣ Build response DTO
        BookingResponseDto response = new BookingResponseDto();
        response.setBookingId(null);
        response.setShowId(showId);
        response.setSeatIds(seatIds);
        response.setTotalAmount(totalAmount);
        response.setFinalAmount(finalAmount);
        response.setDiscountAmount(discountAmount);
        response.setBookingStatus(BookingStatus.PENDING);
        response.setEmail(email);
        response.setUserId(userId); // null if guest

        return response;
    }



    @Override
    public BookingResponseDto modifyBooking(Long bookingId, BookingDto bookingDto) throws AuthorizationException, BookingException, BusinessException, CouponException, PaymentException {
        return null;
    }

    @Override
    public void cancelBooking(Long bookingId) throws AuthorizationException, BookingException, BusinessException, PaymentException {

    }

    @Override
    public List<BookingResponseDto> getBookingHistory(Long userId) throws AuthorizationException, BookingException {
        return List.of();
    }

    @Override
    public BookingResponseDto getBookingDetails(Long bookingId) throws AuthorizationException, BookingException {
        return null;
    }

    @Override
    public BookingPaymentDto processPayment(Long bookingId, BookingPaymentDto paymentDto) throws AuthorizationException, PaymentException, BookingException {
        return null;
    }


}
