package com.simply.Cinema.service.auth.impl;


import com.simply.Cinema.core.location_and_venue.entity.Theatre;
import com.simply.Cinema.core.location_and_venue.repository.TheatreRepo;
import com.simply.Cinema.core.show_and_booking.Enum.BookingStatus;
import com.simply.Cinema.core.show_and_booking.Enum.PaymentStatus;
import com.simply.Cinema.core.show_and_booking.Enum.ShowSeatStatus;
import com.simply.Cinema.core.show_and_booking.dto.CounterBookingDto;
import com.simply.Cinema.core.show_and_booking.entity.OfflineBooking;
import com.simply.Cinema.core.show_and_booking.entity.Show;
import com.simply.Cinema.core.show_and_booking.entity.ShowSeat;
import com.simply.Cinema.core.show_and_booking.repository.OfflineBookingRepo;
import com.simply.Cinema.core.show_and_booking.repository.ShowRepo;
import com.simply.Cinema.core.show_and_booking.repository.ShowSeatRepo;
import com.simply.Cinema.core.user.Enum.CounterStaffRoleEnum;
import com.simply.Cinema.core.user.Enum.UserRoleEnum;
import com.simply.Cinema.core.user.dto.CounterStaffDto;
import com.simply.Cinema.core.user.dto.CounterStaffResponseDto;
import com.simply.Cinema.core.user.entity.CounterStaff;
import com.simply.Cinema.core.user.entity.User;
import com.simply.Cinema.core.user.entity.UserRole;
import com.simply.Cinema.core.user.repository.CounterStaffRepo;
import com.simply.Cinema.core.user.repository.UserRepo;
import com.simply.Cinema.core.user.repository.UserRoleRepo;
import com.simply.Cinema.exception.*;
import com.simply.Cinema.service.auth.TheatreStaffService;
import com.simply.Cinema.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TheatreStaffServiceImpl implements TheatreStaffService {

    private final CounterStaffRepo counterStaffRepo;
    private final UserRepo userRepo;
    private final TheatreRepo theatreRepo;
    private final UserRoleRepo userRoleRepo;
    private final OfflineBookingRepo offlineBookingRepo;
    private final ShowRepo showRepo;
    private final ShowSeatRepo showSeatRepo;
    private final PasswordEncoder passwordEncoder;


    @Override
    @Transactional
    public CounterStaffResponseDto registerCounterStaff(CounterStaffDto request)
            throws AuthorizationException, ValidationException, BusinessException, ResourceNotFoundException {

        log.info("Registering counter staff with email: {}", request.getEmail());

        Long loggedInUserId = SecurityUtil.getCurrentUserId();

        // Validate theatre exists
        Theatre theatre = theatreRepo.findById(request.getTheatreId())
                .orElseThrow(() -> new ResourceNotFoundException("Theatre not found with ID: " + request.getTheatreId()));

        // Check if user already exists
        User user = userRepo.findByEmail(request.getEmail())
                .orElse(null);

        if (user == null && userRepo.existsByPhone(request.getPhone())) {
            throw new UserException("Phone number is already registered.");
        }

        if (user == null) {
            // Create new user with password
            user = new User();
            user.setEmail(request.getEmail());
            user.setPhone(request.getPhone());
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setPasswordHash(passwordEncoder.encode(request.getPassword()));             user.setIsActive(true);
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());
            user = userRepo.save(user);
        } else {
            if (counterStaffRepo.existsByUserId(user.getId())) {
                throw new BusinessException("User is already registered as counter staff");
            }

            // Update existing user details if needed
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setPhone(request.getPhone());
            user.setUpdatedAt(LocalDateTime.now());
            if (request.getPassword() != null && !request.getPassword().isEmpty()) {
                user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
            }
            user = userRepo.save(user);
        }

        // Validate staff type specific fields
        validateStaffTypeFields(request);

        // Generate unique employee code
        String employeeCode = generateEmployeeCode(request.getStaffType());
        while (counterStaffRepo.existsByEmployeeCode(employeeCode)) {
            employeeCode = generateEmployeeCode(request.getStaffType());
        }

        // Create UserRole for this staff
        UserRole userRole = new UserRole();
        userRole.setUser(user);
        userRole.setRole(UserRoleEnum.ROLE_THEATRE_STAFF);
        userRole.setTheatreId(request.getTheatreId());
        userRole.setIsActive(true);
        userRole.setAssignedAt(LocalDateTime.now());
        userRole.setAssignedBy(loggedInUserId);
        userRole.setAssignedBy(1L);
        userRole = userRoleRepo.save(userRole);

        // Create counter staff
        CounterStaff counterStaff = new CounterStaff();
        counterStaff.setUser(user);
        counterStaff.setTheatre(theatre);
        counterStaff.setStaffType(request.getStaffType());
        counterStaff.setEmployeeCode(employeeCode);
        counterStaff.setJoiningDate(request.getJoiningDate() != null ? request.getJoiningDate() : LocalDate.now());
        counterStaff.setUserRole(userRole);
        counterStaff.setDeviceId(request.getDeviceId());
        counterStaff.setIsOnDuty(request.getIsOnDuty() != null ? request.getIsOnDuty() : false);
        counterStaff.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        counterStaff.setCreatedAt(LocalDateTime.now());
        counterStaff.setUpdatedAt(LocalDateTime.now());

        // Set role-specific fields
        if (request.getStaffType() == CounterStaffRoleEnum.ROLE_COUNTER_STAFF) {
            counterStaff.setCounterNumber(request.getCounterNumber());
            counterStaff.setCanIssueRefunds(request.getCanIssueRefunds() != null ? request.getCanIssueRefunds() : false);
        } else if (request.getStaffType() == CounterStaffRoleEnum.ROLE_VERIFICATION_STAFF) {
            counterStaff.setGateNumber(request.getGateNumber());
        }

        CounterStaff savedStaff = counterStaffRepo.save(counterStaff);
        log.info("Counter staff registered successfully with ID: {}", savedStaff.getId());

        // Map to response DTO
        return mapToResponseDto(savedStaff);
    }

    @Override
    @Transactional
    public CounterStaff updateCounterStaffDetails(Long id, CounterStaffDto request)
            throws ResourceNotFoundException, ValidationException, BusinessException {

        log.info("Updating counter staff with ID: {}", id);

        CounterStaff counterStaff = counterStaffRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Counter staff not found with ID: " + id));

        // Validate theatre if changed
        if (request.getTheatreId() != null && !counterStaff.getTheatre().getId().equals(request.getTheatreId())) {
            Theatre theatre = theatreRepo.findById(request.getTheatreId())
                    .orElseThrow(() -> new ResourceNotFoundException("Theatre not found with ID: " + request.getTheatreId()));
            counterStaff.setTheatre(theatre);

            // Update UserRole theatreId
            UserRole userRole = counterStaff.getUserRole();
            if (userRole != null) {
                userRole.setTheatreId(request.getTheatreId());
                userRoleRepo.save(userRole);
            }
        }

        // Update user details
        User user = counterStaff.getUser();
        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        userRepo.save(user);

        // Update staff type if changed
        if (request.getStaffType() != null && !counterStaff.getStaffType().equals(request.getStaffType())) {
            validateStaffTypeFields(request);
            counterStaff.setStaffType(request.getStaffType());

            // Clear old role-specific fields and set new ones
            if (request.getStaffType() == CounterStaffRoleEnum.ROLE_COUNTER_STAFF) {
                counterStaff.setGateNumber(null);
                counterStaff.setCounterNumber(request.getCounterNumber());
                counterStaff.setCanIssueRefunds(request.getCanIssueRefunds() != null ? request.getCanIssueRefunds() : false);
            } else if (request.getStaffType() == CounterStaffRoleEnum.ROLE_VERIFICATION_STAFF) {
                counterStaff.setCounterNumber(null);
                counterStaff.setCanIssueRefunds(false);
                counterStaff.setGateNumber(request.getGateNumber());
            }
        } else {
            // Update role-specific fields for existing type
            if (counterStaff.getStaffType() == CounterStaffRoleEnum.ROLE_COUNTER_STAFF) {
                if (request.getCounterNumber() != null) {
                    counterStaff.setCounterNumber(request.getCounterNumber());
                }
                if (request.getCanIssueRefunds() != null) {
                    counterStaff.setCanIssueRefunds(request.getCanIssueRefunds());
                }
            } else if (counterStaff.getStaffType() == CounterStaffRoleEnum.ROLE_VERIFICATION_STAFF) {
                if (request.getGateNumber() != null) {
                    counterStaff.setGateNumber(request.getGateNumber());
                }
            }
        }

        // Update common fields
        if (request.getDeviceId() != null) {
            counterStaff.setDeviceId(request.getDeviceId());
        }
        if (request.getIsOnDuty() != null) {
            counterStaff.setIsOnDuty(request.getIsOnDuty());
        }
        if (request.getIsActive() != null) {
            counterStaff.setIsActive(request.getIsActive());

            // Also update UserRole active status
            UserRole userRole = counterStaff.getUserRole();
            if (userRole != null) {
                userRole.setIsActive(request.getIsActive());
                userRoleRepo.save(userRole);
            }
        }
        if (request.getJoiningDate() != null) {
            counterStaff.setJoiningDate(request.getJoiningDate());
        }

        CounterStaff updatedStaff = counterStaffRepo.save(counterStaff);
        log.info("Counter staff updated successfully with ID: {}", updatedStaff.getId());

        return updatedStaff;
    }

    @Override
    @Transactional(readOnly = true)
    public CounterStaff getCounterStaffById(Long id) throws ResourceNotFoundException {
        log.info("Fetching counter staff with ID: {}", id);

        return counterStaffRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Counter staff not found with ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CounterStaff> getAllCounterStaff(Long theatreId)
            throws ResourceNotFoundException, AuthorizationException {

        log.info("Fetching all counter staff for theatre ID: {}", theatreId);

        // Validate theatre exists
        if (!theatreRepo.existsById(theatreId)) {
            throw new ResourceNotFoundException("Theatre not found with ID: " + theatreId);
        }

        return counterStaffRepo.findByTheatreId(theatreId);
    }

    @Override
    @Transactional
    public void deactivateCounterStaff(Long id) throws ResourceNotFoundException, BusinessException {
        log.info("Deactivating counter staff with ID: {}", id);

        CounterStaff counterStaff = counterStaffRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Counter staff not found with ID: " + id));

        if (!counterStaff.getIsActive()) {
            throw new BusinessException("Counter staff is already inactive");
        }

        if (counterStaff.getIsOnDuty()) {
            throw new BusinessException("Cannot deactivate staff member who is currently on duty");
        }

        counterStaff.setIsActive(false);

        // Also deactivate the UserRole
        UserRole userRole = counterStaff.getUserRole();
        if (userRole != null) {
            userRole.setIsActive(false);
            userRoleRepo.save(userRole);
        }

        counterStaffRepo.save(counterStaff);

        log.info("Counter staff deactivated successfully with ID: {}", id);
    }

    @Override
    @Transactional
    public CounterBookingDto createCounterBooking(CounterBookingDto request) throws BusinessException, BookingException {

        Long loggedInUserId = SecurityUtil.getCurrentUserId();

        CounterStaff counterStaff = counterStaffRepo.findByUserId(loggedInUserId)
                .orElseThrow(() -> new AuthorizationException("Counter staff not found for this user"));

        if (request.getCustomerName() == null || request.getCustomerName().isEmpty()) {
            throw new BookingException("Customer name is required");
        }
        if (request.getCustomerPhone() == null || request.getCustomerPhone().isEmpty()) {
            throw new BookingException("Customer phone is required");
        }

        if (request.getCustomerEmail() == null || request.getCustomerEmail().isEmpty()) {
            throw new BookingException("Customer email is required");
        }

        Show show = showRepo.findById(request.getShowId())
                .orElseThrow(() -> new ResourceNotFoundException("Show not found with ID: " + request.getShowId()));

        if (show.getAvailableSeats() == null) {
            show.setAvailableSeats(show.getTotalSeats() != null ? show.getTotalSeats() : 0);
        }

        List<ShowSeat> seats = showSeatRepo.findAllById(request.getSeatIds());
        if (seats.isEmpty()) {
            throw new ResourceNotFoundException("No valid seats found for given IDs");
        }
        if (seats.size() != request.getSeatIds().size()) {
            throw new BookingException("Some seat IDs are invalid");
        }

        List<String> unavailableSeats = new ArrayList<>();
        for (ShowSeat seat : seats) {
            if (!seat.getShow().getId().equals(show.getId())) {
                throw new BookingException("Seat " + seat.getSeat().getSeatNumber() + " does not belong to this show");
            }
            if (seat.getStatus() != ShowSeatStatus.AVAILABLE) {
                unavailableSeats.add(seat.getSeat().getSeatNumber());
            }
        }

        if (!unavailableSeats.isEmpty()) {
            throw new BusinessException("Following seats are not available: " + String.join(", ", unavailableSeats));
        }

        for (ShowSeat seat : seats) {
            seat.setStatus(ShowSeatStatus.BOOKED);
            seat.setLockedUntil(null);
            seat.setLockedByUserId(null);
        }
        showSeatRepo.saveAll(seats);

        int updatedSeats = show.getAvailableSeats() - seats.size();
        show.setAvailableSeats(updatedSeats);
        showRepo.save(show);

        double totalAmount = seats.stream().mapToDouble(ShowSeat::getPrice).sum();

        OfflineBooking booking = new OfflineBooking();
        booking.setShow(show);
        booking.setCounterStaff(counterStaff);  //// ----- testing null
        booking.setCustomerName(request.getCustomerName());
        booking.setCustomerEmail(request.getCustomerEmail());
        booking.setCustomerPhone(request.getCustomerPhone());
        booking.setTotalAmount(totalAmount);
        booking.setFinalAmount(totalAmount);
        booking.setPaymentMode(request.getPaymentMode() != null ? request.getPaymentMode() : "CASH");
        booking.setCounterNumber(counterStaff.getCounterNumber());  //// ----- testing
        booking.setBookingReference(generateBookingReference());
        booking.setBookingStatus(BookingStatus.CONFIRMED);
        booking.setPaymentStatus(PaymentStatus.SUCCESS);
        booking.setCreatedAt(LocalDateTime.now());
        booking.setUpdatedAt(LocalDateTime.now());

        OfflineBooking savedBooking = offlineBookingRepo.save(booking);

        CounterBookingDto response = new CounterBookingDto();

        response.setCustomerName(savedBooking.getCustomerName());
        response.setCustomerPhone(savedBooking.getCustomerPhone());
        response.setCustomerEmail(savedBooking.getCustomerEmail());
        response.setShowId(show.getId());
        response.setSeatIds(request.getSeatIds());
        response.setCounterStaffId(counterStaff.getId()); ////  testing null
        response.setPaymentMode(savedBooking.getPaymentMode());
        response.setMovie(show.getMovie().getTitle());
        response.setScreenId(show.getScreen().getId().toString());
        response.setTicketPrice(String.valueOf(totalAmount));

        return response;

//        // --- Dummy CounterStaff for testing ---
//        CounterStaff dummyStaff = counterStaffRepo.findById(1L)
//                .orElseThrow(() -> new RuntimeException("Dummy counter staff not found"));
//
//        // --- Create OfflineBooking ---
//        OfflineBooking booking = new OfflineBooking();
//        booking.setShow(show);
//        booking.setCounterStaff(dummyStaff);
//        booking.setCounterNumber(dummyStaff.getCounterNumber() != null ? dummyStaff.getCounterNumber() : "TEST-COUNTER");
//        booking.setCustomerName(request.getCustomerName());
//        booking.setCustomerEmail(request.getCustomerEmail());
//        booking.setCustomerPhone(request.getCustomerPhone());
//        booking.setTotalAmount(totalAmount);
//        booking.setFinalAmount(totalAmount);
//        booking.setPaymentMode(request.getPaymentMode() != null ? request.getPaymentMode() : "CASH");
//        booking.setBookingReference(generateBookingReference());
//        booking.setBookingStatus(BookingStatus.CONFIRMED);
//        booking.setPaymentStatus(PaymentStatus.SUCCESS);
//        booking.setCreatedAt(LocalDateTime.now());
//        booking.setUpdatedAt(LocalDateTime.now());
//
//        OfflineBooking savedBooking = offlineBookingRepo.save(booking);
//
//        // --- Prepare Response ---
//        CounterBookingDto response = new CounterBookingDto();
//        response.setCustomerName(savedBooking.getCustomerName());
//        response.setCustomerPhone(savedBooking.getCustomerPhone());
//        response.setCustomerEmail(savedBooking.getCustomerEmail());
//        response.setShowId(show.getId());
//        response.setSeatIds(request.getSeatIds());
//        response.setCounterStaffId(dummyStaff.getId());
//        response.setPaymentMode(savedBooking.getPaymentMode());
//        response.setMovie(show.getMovie().getTitle());
//        response.setScreenId(show.getScreen().getId().toString());
//        response.setTicketPrice(String.valueOf(totalAmount));
//
//        return response;
//

    }


    // Helper methods
    private CounterStaffResponseDto mapToResponseDto(CounterStaff staff) {
        return CounterStaffResponseDto.builder()
                .id(staff.getId())
                .firstName(staff.getUser().getFirstName())
                .lastName(staff.getUser().getLastName())
                .email(staff.getUser().getEmail())
                .phone(staff.getUser().getPhone())
                .staffType(staff.getStaffType())
                .employeeCode(staff.getEmployeeCode())
                .joiningDate(staff.getJoiningDate())
                .counterNumber(staff.getCounterNumber())
                .gateNumber(staff.getGateNumber())
                .canIssueRefunds(staff.getCanIssueRefunds())
                .isOnDuty(staff.getIsOnDuty())
                .isActive(staff.getIsActive())
                .theatreId(staff.getTheatre().getId())
                .theatreName(staff.getTheatre().getName())
                .createdAt(staff.getCreatedAt())
                .build();
    }

    private void validateStaffTypeFields(CounterStaffDto request) throws ValidationException {
        if (request.getStaffType() == CounterStaffRoleEnum.ROLE_COUNTER_STAFF) {
            if (request.getCounterNumber() == null || request.getCounterNumber().isBlank()) {
                throw new ValidationException("Counter number is required for counter staff");
            }
        } else if (request.getStaffType() == CounterStaffRoleEnum.ROLE_VERIFICATION_STAFF) {
            if (request.getGateNumber() == null || request.getGateNumber().isBlank()) {
                throw new ValidationException("Gate number is required for verification staff");
            }
        }
    }

    private String generateEmployeeCode(CounterStaffRoleEnum staffType) {
        String prefix = staffType == CounterStaffRoleEnum.ROLE_COUNTER_STAFF ? "CS" : "VS";
        String uniqueId = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return prefix + "-" + uniqueId;
    }

    private String generateBookingReference() {
        return "OFF-" + System.currentTimeMillis() + "-" + (int) (Math.random() * 9000 + 1000);
    }

    private String generateTemporaryPassword() {
        return "Temp@" + UUID.randomUUID().toString().substring(0, 8);
    }
}