package com.simply.Cinema.service.show_and_booking.Impl;

import com.simply.Cinema.core.show_and_booking.Enum.BookingStatus;
import com.simply.Cinema.core.show_and_booking.dto.BookingDto;
import com.simply.Cinema.core.show_and_booking.dto.BookingPaymentDto;
import com.simply.Cinema.core.show_and_booking.dto.BookingResponseDto;
import com.simply.Cinema.core.show_and_booking.entity.Booking;
import com.simply.Cinema.core.show_and_booking.entity.Show;
import com.simply.Cinema.core.show_and_booking.entity.ShowSeat;
import com.simply.Cinema.core.show_and_booking.repository.BookingRepo;
import com.simply.Cinema.core.show_and_booking.repository.ShowRepo;
import com.simply.Cinema.core.show_and_booking.repository.ShowSeatRepo;
import com.simply.Cinema.core.user.dto.UserProfileDto;
import com.simply.Cinema.core.user.entity.GuestUser;
import com.simply.Cinema.core.user.entity.User;
import com.simply.Cinema.core.user.repository.GuestUserRepo;
import com.simply.Cinema.core.user.repository.UserRepo;
import com.simply.Cinema.exception.*;
import com.simply.Cinema.service.RedisService;
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

    private final UserRepo userRepo;
    private final ShowRepo showRepo;
    private final ShowSeatRepo showSeatRepo;
    private final SeatLockService seatLockService;
    private final UserService userService;
    private final GuestUserRepo guestUserRepo;
    private final BookingRepo bookingRepo;
    private final RedisService redisService;

//    @Override
//    public BookingResponseDto createBooking(BookingDto bookingDto, String jwt)
//            throws AuthorizationException, BookingException, BusinessException, CouponException, PaymentException {
//
//        User user = null;
//        GuestUser guestUser = null;
//        Long userId = null;
//        String email;
//
//        if (jwt != null && !jwt.isEmpty()) {
//
//            UserProfileDto userDto = userService.findUserBYJwtToken(jwt);
//            userId = userDto.getId();
//            Long finalUserId = userId;
//            user = userRepo.findById(userId)
//                    .orElseThrow(() -> new UserException("User not found with ID: " + finalUserId));
//            email = user.getEmail();
//        }
//        else {
//            if (bookingDto.getEmail() == null || bookingDto.getEmail().isEmpty()) {
//                throw new BookingException("Email is required for guest booking");
//            }
//
//            email = bookingDto.getEmail();
//
//            // Check if guest already exists
//            guestUser = guestUserRepo.findByEmail(email)
//                    .orElseGet(() -> {
//                        GuestUser newGuest = new GuestUser();
//                        newGuest.setEmail(email);
//                        return guestUserRepo.save(newGuest);
//                    });
//
//            userId = guestUser.getId();
//        }
//
//        Long showId = bookingDto.getShowId();
//        Show show = showRepo.findById(showId)
//                .orElseThrow(() -> new BookingException("Show not found with ID: " + showId));
//
//        List<Long> seatIds = bookingDto.getSeatIds();
//        seatLockService.lockSeats(showId, seatIds, userId);
//
//        List<ShowSeat> showSeats = showSeatRepo.findByShowAndSeatIds(show, seatIds);
//        if (showSeats.size() != seatIds.size()) {
//            throw new BookingException("Some seats are not available for booking");
//        }
//
//        double totalAmount = showSeats.stream().mapToDouble(ShowSeat::getPrice).sum();
//        double discountAmount = 0.0;
//        double finalAmount = totalAmount - discountAmount;
//
//        Booking tempBooking = new Booking();
//        tempBooking.setShow(show);
//        tempBooking.setUser(user);
//        tempBooking.setGuestUser(guestUser);
//        tempBooking.setTotalAmount(totalAmount);
//        tempBooking.setBookingStatus(BookingStatus.PENDING);
//        tempBooking.setEmail(email);
//        tempBooking.setCreatedAt(LocalDateTime.now());
//        tempBooking.setUpdatedAt(LocalDateTime.now());
//
//        tempBooking = bookingRepo.save(tempBooking);
//
//        BookingResponseDto response = new BookingResponseDto();
//        response.setBookingId(tempBooking.getId());
//        response.setShowId(showId);
//        response.setSeatIds(seatIds);
//        response.setTotalAmount(totalAmount);
//        response.setFinalAmount(totalAmount);
//        response.setDiscountAmount(discountAmount);
//        response.setBookingStatus(BookingStatus.PENDING);
//        response.setEmail(email);
//        response.setUserId(userId);
//        if (guestUser != null) {
//            response.setGuestUserId(guestUser.getId());
//        }
//
//        return response;
//    }

    @Override
    public BookingResponseDto createBooking(BookingDto bookingDto, String jwt)
            throws AuthorizationException, BookingException, BusinessException, CouponException, PaymentException {

        User user = null;
        Long userId;
        String email;

        if (jwt != null && !jwt.isEmpty()) {
            UserProfileDto userDto = userService.findUserBYJwtToken(jwt);
            userId = userDto.getId();
            user = userRepo.findById(userId)
                    .orElseThrow(() -> new UserException("User not found with ID: " + userId));
            email = user.getEmail();
        }
        else {
            userId = null;
            if (bookingDto.getEmail() == null || bookingDto.getEmail().isEmpty()) {
                throw new BookingException("Email is required for guest booking");
            }

            email = bookingDto.getEmail();

            // Temporarily store guest in Redis (not DB)
            GuestUser tempGuest = new GuestUser();
            tempGuest.setEmail(email);

            String guestKey = "guest_temp:" + email;
            redisService.set(guestKey, tempGuest, 300); // 5 mins TTL
        }

        Show show = showRepo.findById(bookingDto.getShowId())
                .orElseThrow(() -> new BookingException("Show not found"));

        // temporarily Lock Seats
        List<Long> seatIds = bookingDto.getSeatIds();
        seatLockService.lockSeats(show.getId(), seatIds, userId);

        List<ShowSeat> showSeats = showSeatRepo.findByShowAndSeatIds(show, seatIds);
        if (showSeats.size() != seatIds.size()) {
            throw new BookingException("Some seats are not available for booking");
        }

        double totalAmount = showSeats.stream().mapToDouble(ShowSeat::getPrice).sum();
        double discountAmount = 0.0;
        double finalAmount = totalAmount;

        // temporary Booking object (NOT SAVED in DB)
        BookingResponseDto response = new BookingResponseDto();
        response.setBookingId(null); // not generated yet
        response.setShowId(show.getId());
        response.setSeatIds(seatIds);
        response.setTotalAmount(totalAmount);
        response.setFinalAmount(finalAmount);
        response.setDiscountAmount(discountAmount);
        response.setBookingStatus(BookingStatus.PENDING);
        response.setEmail(email);
        response.setUserId(userId);
        response.setPaymentStatus(null);
        response.setQrCode(null);

        // ✅ Store temp booking in Redis
        String bookingKey = "temp_booking:" + email + ":" + show.getId();
        redisService.set(bookingKey, response, 300); // 5 min TTL

        return response;
    }


    @Override
    public BookingResponseDto confirmBooking(BookingDto bookingConfirmDto, String jwt) throws AuthorizationException, BookingException, BusinessException, CouponException, PaymentException {
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
