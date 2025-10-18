package com.simply.Cinema.service.show_and_booking.Impl;

import com.simply.Cinema.core.show_and_booking.Enum.BookingStatus;
import com.simply.Cinema.core.show_and_booking.Enum.PaymentMethod;
import com.simply.Cinema.core.show_and_booking.Enum.PaymentStatus;
import com.simply.Cinema.core.show_and_booking.Enum.ShowSeatStatus;
import com.simply.Cinema.core.show_and_booking.dto.BookingDto;
import com.simply.Cinema.core.show_and_booking.dto.BookingPaymentDto;
import com.simply.Cinema.core.show_and_booking.dto.BookingResponseDto;
import com.simply.Cinema.core.show_and_booking.dto.PaymentResponseDto;
import com.simply.Cinema.core.show_and_booking.entity.*;
import com.simply.Cinema.core.show_and_booking.repository.*;
import com.simply.Cinema.core.user.dto.UserProfileDto;
import com.simply.Cinema.core.user.entity.User;
import com.simply.Cinema.core.user.repository.UserRepo;
import com.simply.Cinema.exception.*;
import com.simply.Cinema.response.ApiResponse;
import com.simply.Cinema.service.RedisService;
import com.simply.Cinema.service.UserService.UserService;
import com.simply.Cinema.service.show_and_booking.BookingService;
import com.simply.Cinema.service.show_and_booking.PaymentService;
import com.simply.Cinema.service.show_and_booking.SeatLockService;
import com.simply.Cinema.util.SecurityUtil;
import jakarta.transaction.Transactional;
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
    private final BookingRepo bookingRepo;
    private final RedisService redisService;
    private final PaymentService paymentService;
    private final BookingSeatRepo bookingSeatRepo;
    private final BookingPaymentRepo bookingPaymentRepo;


    //for guest users

//    @Override
//    public BookingResponseDto createBooking(BookingDto bookingDto, String jwt)
//            throws AuthorizationException, BookingException, BusinessException, CouponException, PaymentException {
//
//        User user = null;
//        Long userId;
//        String email;
//
//        if (jwt != null && !jwt.isEmpty()) {
//            UserProfileDto userDto = userService.findUserBYJwtToken(jwt);
//            userId = userDto.getId();
//            user = userRepo.findById(userId)
//                    .orElseThrow(() -> new UserException("User not found with ID: " + userId));
//            email = user.getEmail();
//        }
//        else {
//            userId = null;
//            if (bookingDto.getEmail() == null || bookingDto.getEmail().isEmpty()) {
//                throw new BookingException("Email is required for guest booking");
//            }
//
//            email = bookingDto.getEmail();
//
//            // Temporarily store guest in Redis (not DB)
//            GuestUser tempGuest = new GuestUser();
//            tempGuest.setEmail(email);
//
//            String guestKey = "guest_temp:" + email;
//            redisService.set(guestKey, tempGuest, 300); // 5 mins TTL
//        }
//
//        Show show = showRepo.findById(bookingDto.getShowId())
//                .orElseThrow(() -> new BookingException("Show not found"));
//
//        // temporarily Lock Seats
//        List<Long> seatIds = bookingDto.getSeatIds();
//        seatLockService.lockSeats(show.getId(), seatIds, userId);
//
//        List<ShowSeat> showSeats = showSeatRepo.findByShowAndSeatIds(show, seatIds);
//        if (showSeats.size() != seatIds.size()) {
//            throw new BookingException("Some seats are not available for booking");
//        }
//
//        double totalAmount = showSeats.stream().mapToDouble(ShowSeat::getPrice).sum();
//        double discountAmount = 0.0;
//        double finalAmount = totalAmount;
//
//        // temporary Booking object (NOT SAVED in DB)
//        BookingResponseDto response = new BookingResponseDto();
//        response.setBookingId(null); // not generated yet
//        response.setShowId(show.getId());
//        response.setSeatIds(seatIds);
//        response.setTotalAmount(totalAmount);
//        response.setFinalAmount(finalAmount);
//        response.setDiscountAmount(discountAmount);
//        response.setBookingStatus(BookingStatus.PENDING);
//        response.setEmail(email);
//        response.setUserId(userId);
//        response.setPaymentStatus(null);
//        response.setQrCode(null);
//
//        //  Store temp booking in Redis
//        String bookingKey = "temp_booking:" + email + ":" + show.getId();
//        redisService.set(bookingKey, response, 300); // 5 min TTL
//
//        return response;
//    }


    @Override
    public BookingResponseDto createBooking(BookingDto bookingDto, String jwt)
            throws AuthorizationException, BookingException, BusinessException, CouponException, PaymentException {

        if (jwt == null || jwt.isEmpty()) {
            throw new AuthorizationException("User must sign up or log in before booking seats");
        }

        // Get authenticated user
        UserProfileDto userDto = userService.findUserBYJwtToken(jwt);
        Long userId = userDto.getId();
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new UserException("User not found with ID: " + userId));
        String email = user.getEmail();

        // Fetch show
        Show show = showRepo.findById(bookingDto.getShowId())
                .orElseThrow(() -> new BookingException("Show not found"));

        // Lock seats temporarily
        List<Long> seatIds = bookingDto.getSeatIds();
        seatLockService.lockSeats(show.getId(), seatIds, userId);

        List<ShowSeat> showSeats = showSeatRepo.findByShowAndSeatIds(show, seatIds);
        if (showSeats.size() != seatIds.size()) {
            throw new BookingException("Some seats are not available for booking");
        }

        double totalAmount = showSeats.stream().mapToDouble(ShowSeat::getPrice).sum();
        double discountAmount = 0.0;
        double finalAmount = totalAmount;

        // Temporary booking response (not saved yet)
        BookingResponseDto response = new BookingResponseDto();
        response.setBookingId(null);
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

        // Store temp booking in Redis (optional)
        String bookingKey = "temp_booking:" + email + ":" + show.getId();
        redisService.set(bookingKey, response, 600); // 10 min TTL

        return response;
    }


    @Override
    @Transactional
    public BookingResponseDto confirmBooking(BookingDto bookingConfirmDto)
            throws AuthorizationException, BookingException, BusinessException, CouponException, PaymentException, Exception {

        // get current logged-in user from JWT
        Long currentUserId = SecurityUtil.getCurrentUserId();
        User currentUser = userRepo.findById(currentUserId)
                .orElseThrow(() -> new BookingException("User not found"));

        // fetch temporary booking from Redis
        String bookingKey = "temp_booking:" + currentUser.getEmail() + ":" + bookingConfirmDto.getShowId();
        BookingResponseDto tempBooking = redisService.get(bookingKey, BookingResponseDto.class);
        if (tempBooking == null) {
            throw new BookingException("Temporary booking not found or expired");
        }

        // ensure the current user matches the temp booking
        if (!tempBooking.getEmail().equalsIgnoreCase(currentUser.getEmail())) {
            throw new AuthorizationException("Payment was made by another user");
        }

        // verify payment
        ApiResponse paymentStatus = paymentService.verifyPayment(
                bookingConfirmDto.getPaymentToken(),
                bookingConfirmDto.getPaymentLinkId()
        );

        if (!paymentStatus.isStatus()) {
            throw new PaymentException("Payment not completed yet");
        }

        // fetch show & seats
        Show show = showRepo.findById(tempBooking.getShowId())
                .orElseThrow(() -> new BookingException("Show not found"));

        List<Long> seatIds = tempBooking.getSeatIds();
        List<ShowSeat> showSeats = showSeatRepo.findByShowAndSeatIds(show, seatIds);

        if (showSeats.size() != seatIds.size()) {
            throw new BookingException("Some seats are no longer available");
        }

        // lock seats permanently
        for (ShowSeat seat : showSeats) {
            seat.setStatus(ShowSeatStatus.BOOKED);
            showSeatRepo.save(seat);
        }

        // save booking
        Booking booking = new Booking();
        booking.setUser(currentUser);
        booking.setEmail(currentUser.getEmail());
        booking.setShow(show);
        booking.setBookingReference("BKG-" + System.currentTimeMillis());
        booking.setTotalAmount(tempBooking.getTotalAmount());
        booking.setFinalAmount(tempBooking.getFinalAmount());
        booking.setDiscountAmount(tempBooking.getDiscountAmount());
        booking.setBookingStatus(BookingStatus.CONFIRMED);
        booking.setPaymentStatus(PaymentStatus.SUCCESS);
        booking.setCreatedAt(LocalDateTime.now());
        booking.setUpdatedAt(LocalDateTime.now());
        bookingRepo.save(booking);

        // save booking seats
        for (ShowSeat seat : showSeats) {
            BookingSeat bookingSeat = new BookingSeat();
            bookingSeat.setBooking(booking);
            bookingSeat.setShowSeat(seat);
            bookingSeat.setPricePaid(seat.getPrice());
            bookingSeatRepo.save(bookingSeat);
        }

        // save payment info
        BookingPayment payment = new BookingPayment();
        payment.setBooking(booking);
        payment.setAmount(tempBooking.getFinalAmount());
        payment.setPaymentMethod(
                bookingConfirmDto.getPaymentMethod() != null ? bookingConfirmDto.getPaymentMethod() : PaymentMethod.UPI
        );

        payment.setTransactionId(bookingConfirmDto.getPaymentToken());
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setGatewayResponse(paymentStatus.getMessage());
        payment.setCreatedAt(LocalDateTime.now());
        bookingPaymentRepo.save(payment);

        //  Clear Redis temp booking
        redisService.delete(bookingKey);
        redisService.delete("payment_link:" + bookingConfirmDto.getPaymentLinkId());

        // return final booking response
        tempBooking.setBookingId(booking.getId());
        tempBooking.setBookingStatus(BookingStatus.CONFIRMED);
        tempBooking.setPaymentStatus(PaymentStatus.SUCCESS);

        System.out.println("------------------------------helooooooo--------------------------");

        return tempBooking;
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
