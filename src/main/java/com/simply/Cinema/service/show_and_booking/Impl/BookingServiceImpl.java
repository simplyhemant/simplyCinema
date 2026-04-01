package com.simply.Cinema.service.show_and_booking.Impl;

import com.simply.Cinema.core.location_and_venue.entity.City;
import com.simply.Cinema.core.location_and_venue.repository.*;
import com.simply.Cinema.core.show_and_booking.Enum.BookingStatus;
import com.simply.Cinema.core.show_and_booking.Enum.PaymentMethod;
import com.simply.Cinema.core.show_and_booking.Enum.PaymentStatus;
import com.simply.Cinema.core.show_and_booking.Enum.ShowSeatStatus;
import com.simply.Cinema.core.show_and_booking.dto.BookingDto;
import com.simply.Cinema.core.show_and_booking.dto.BookingPaymentDto;
import com.simply.Cinema.core.show_and_booking.dto.BookingResponseDto;
import com.simply.Cinema.core.show_and_booking.entity.*;
import com.simply.Cinema.core.show_and_booking.repository.*;
import com.simply.Cinema.core.user.dto.UserProfileDto;
import com.simply.Cinema.core.user.entity.User;
import com.simply.Cinema.core.user.repository.UserRepo;
import com.simply.Cinema.exception.*;
import com.simply.Cinema.response.ApiResponse;
import com.simply.Cinema.security.jwt.JwtProvider;
import com.simply.Cinema.service.RedisService;
import com.simply.Cinema.service.UserService.UserService;
import com.simply.Cinema.service.show_and_booking.BookingService;
import com.simply.Cinema.service.show_and_booking.PaymentService;
import com.simply.Cinema.service.show_and_booking.SeatLockService;
import com.simply.Cinema.util.SecurityUtil;
import com.simply.Cinema.validation.EmailService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private final CityRepo cityRepo;

    private final EmailService emailService;

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

        List<Long> seatIds = bookingDto.getSeatIds();

        // Fetch all show seats
        List<ShowSeat> showSeats = showSeatRepo.findByShowAndSeatIds(show, seatIds);

        // Validate all seats exist
        if (showSeats.size() != seatIds.size()) {
            throw new BookingException("Some seats do not exist for this show");
        }

        // Check if ALL seats are available BEFORE locking
        List<Long> unavailableSeats = new ArrayList<>();
        for (ShowSeat showSeat : showSeats) {
            if (showSeat.getStatus() != ShowSeatStatus.AVAILABLE) {
                unavailableSeats.add(showSeat.getSeat().getId());
            }
        }

        if (!unavailableSeats.isEmpty()) {
            throw new BookingException("The following seats are not available: " + unavailableSeats);
        }

        // Now lock seats (all are confirmed available)
        seatLockService.lockSeats(show.getId(), seatIds, userId);

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
        response.setQrCode(null);

        // Fetch descriptive data for confirmation
        City city = cityRepo.findById(show.getScreen().getTheatre().getCityId()).orElse(null);

        response.setMovieTitle(show.getMovie().getTitle());
        response.setTheatreName(show.getScreen().getTheatre().getName());
        response.setCityName(city != null ? city.getName() : "Unknown");
        response.setScreenName(show.getScreen().getName());
        response.setShowDate(show.getShowDate());
        response.setShowTime(show.getShowTime());
        response.setSeatNumbers(showSeats.stream().map(ss -> ss.getSeat().getSeatNumber()).toList());

        // Store temp booking in Redis
        String bookingKey = "temp_booking:" + email + ":" + show.getId();
        redisService.set(bookingKey, response, 600); // 10 min TTL

        System.out.println("------------------- Saved to Redis: " + bookingKey);
        System.out.println("------------------- TTL: 600 seconds");

        return response;
    }

    @Override
    @Transactional
    public BookingResponseDto confirmBooking(BookingDto bookingConfirmDto, String jwt)
            throws AuthorizationException, BookingException, BusinessException, CouponException, PaymentException, Exception {

        Long currentUserId = SecurityUtil.getCurrentUserId();

        if (jwt == null || jwt.isEmpty()) {
            throw new AuthorizationException("User must sign up or log in before booking seats");
        }

        UserProfileDto userDto = userService.findUserBYJwtToken(jwt);
        Long userId = userDto.getId();
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new UserException("User not found with ID: " + userId));
        String email = user.getEmail();

        User currentUser = userRepo.findById(currentUserId)
                .orElseThrow(() -> new BookingException("User not found"));

        if (bookingConfirmDto.getShowId() == null) {
            throw new BookingException("Show ID is required for booking confirmation");
        }

        String bookingKey = "temp_booking:" + currentUser.getEmail() + ":" + bookingConfirmDto.getShowId();

        System.out.println("🔍 Looking for key--------------: " + bookingKey);

        BookingResponseDto tempBooking = redisService.get(bookingKey, BookingResponseDto.class);
        if (tempBooking == null) {
            throw new BookingException("Temporary booking not found or expired");
        }

        if (!tempBooking.getEmail().equalsIgnoreCase(currentUser.getEmail())) {
            throw new AuthorizationException("Payment was made by another user");
        }

        ApiResponse paymentStatus = paymentService.verifyPayment(
                bookingConfirmDto.getPaymentToken(),
                bookingConfirmDto.getPaymentLinkId()
        );

        if (!paymentStatus.isStatus()) {
            throw new PaymentException("Payment not completed yet");
        }

        Show show = showRepo.findById(tempBooking.getShowId())
                .orElseThrow(() -> new BookingException("Show not found"));

        List<Long> seatIds = tempBooking.getSeatIds();
        List<ShowSeat> showSeats = showSeatRepo.findByShowAndSeatIds(show, seatIds);

        if (showSeats.size() != seatIds.size()) {
            throw new BookingException("Some seats are no longer available");
        }

        for (ShowSeat seat : showSeats) {
            seat.setLockedByUserId(currentUserId);
            seat.setStatus(ShowSeatStatus.BOOKED);
        }
        showSeatRepo.saveAll(showSeats);

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

        for (ShowSeat seat : showSeats) {
            BookingSeat bookingSeat = new BookingSeat();
            bookingSeat.setBooking(booking);
            bookingSeat.setShowSeat(seat);
            bookingSeat.setPricePaid(seat.getPrice());
            bookingSeatRepo.save(bookingSeat);
        }

        BookingPayment payment = new BookingPayment();
        payment.setBooking(booking);
        payment.setAmount(tempBooking.getFinalAmount());
        payment.setPaymentMethod(
                bookingConfirmDto.getPaymentMethod() != null ? bookingConfirmDto.getPaymentMethod() : PaymentMethod.RAZORPAY
        );
        payment.setTransactionId(bookingConfirmDto.getPaymentToken());
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setGatewayResponse(paymentStatus.getMessage());
        payment.setCreatedAt(LocalDateTime.now());
        bookingPaymentRepo.save(payment);

        redisService.delete(bookingKey);
        redisService.delete("payment_link:" + bookingConfirmDto.getPaymentLinkId());

        tempBooking.setBookingId(booking.getId());
        tempBooking.setBookingStatus(BookingStatus.CONFIRMED);
        tempBooking.setPaymentStatus(PaymentStatus.SUCCESS);

        System.out.println("------------------------------helooooooo--------------------------");

        emailService.sendTicketEmail(email);

        return tempBooking;
    }



    @Override
    public void cancelBooking(Long bookingId) throws AuthorizationException, BookingException, BusinessException, PaymentException {

    }

    @Override
    public List<BookingResponseDto> getBookingHistory(Long userId) throws AuthorizationException, BookingException {
        List<Booking> bookings = bookingRepo.findByUser_Id(userId);
        List<BookingResponseDto> responseList = new ArrayList<>();
        for (Booking booking : bookings) {
            responseList.add(convertToResponseDto(booking));
        }
        return responseList;
    }

    @Override
    public BookingResponseDto getBookingDetails(Long bookingId) throws AuthorizationException, BookingException {
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new BookingException("Booking not found with ID: " + bookingId));
        return convertToResponseDto(booking);
    }

    private BookingResponseDto convertToResponseDto(Booking booking) {
        BookingResponseDto dto = new BookingResponseDto();
        dto.setBookingId(booking.getId());
        dto.setBookingReference(booking.getBookingReference());
        dto.setUserId(booking.getUser().getId());
        dto.setShowId(booking.getShow().getId());
        dto.setTotalAmount(booking.getTotalAmount());
        dto.setDiscountAmount(booking.getDiscountAmount());
        dto.setFinalAmount(booking.getFinalAmount());
        dto.setPaymentStatus(booking.getPaymentStatus());
        dto.setBookingStatus(booking.getBookingStatus());
        dto.setQrCode(booking.getQrCode());
        dto.setEmail(booking.getEmail());

        Show show = booking.getShow();
        City city = cityRepo.findById(show.getScreen().getTheatre().getCityId()).orElse(null);

        dto.setMovieTitle(show.getMovie().getTitle());
        dto.setTheatreName(show.getScreen().getTheatre().getName());
        dto.setCityName(city != null ? city.getName() : "Unknown");
        dto.setScreenName(show.getScreen().getName());
        dto.setShowDate(show.getShowDate());
        dto.setShowTime(show.getShowTime());

        List<BookingSeat> bookingSeats = bookingSeatRepo.findByBooking_Id(booking.getId());
        dto.setSeatIds(bookingSeats.stream().map(bs -> bs.getShowSeat().getSeat().getId()).toList());
        dto.setSeatNumbers(bookingSeats.stream().map(bs -> bs.getShowSeat().getSeat().getSeatNumber()).toList());

        return dto;
    }

    @Override
    public BookingPaymentDto processPayment(Long bookingId, BookingPaymentDto paymentDto) throws AuthorizationException, PaymentException, BookingException {
        return null;
    }


}
