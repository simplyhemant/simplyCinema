package com.simply.Cinema.service.auth.impl;


import com.simply.Cinema.core.location_and_venue.entity.Theatre;
import com.simply.Cinema.core.location_and_venue.repository.TheatreRepo;
import com.simply.Cinema.core.user.Enum.CounterStaffRoleEnum;
import com.simply.Cinema.core.user.Enum.UserRoleEnum;
import com.simply.Cinema.core.user.dto.CounterStaffDto;
import com.simply.Cinema.core.user.entity.CounterStaff;
import com.simply.Cinema.core.user.entity.User;
import com.simply.Cinema.core.user.entity.UserRole;
import com.simply.Cinema.core.user.repository.CounterStaffRepo;
import com.simply.Cinema.core.user.repository.UserRepo;
import com.simply.Cinema.core.user.repository.UserRoleRepo;
import com.simply.Cinema.exception.*;
import com.simply.Cinema.service.auth.CounterStaffService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CounterStaffServiceImpl implements CounterStaffService {

    private final CounterStaffRepo counterStaffRepo;
    private final UserRepo userRepo;
    private final TheatreRepo theatreRepo;
    private final UserRoleRepo userRoleRepo;


        @Override
        @Transactional
        public CounterStaff registerCounterStaff(CounterStaffDto request)
                throws AuthorizationException, ValidationException, BusinessException, ResourceNotFoundException {

            log.info("Registering counter staff with email: {}", request.getEmail());

            // Validate theatre exists
            Theatre theatre = theatreRepo.findById(request.getTheatreId())
                    .orElseThrow(() -> new ResourceNotFoundException("Theatre not found with ID: " + request.getTheatreId()));

            // Check if user already exists
            User user = userRepo.findByEmail(request.getEmail())
                    .orElse(null);

            if (userRepo.existsByPhone(request.getPhone())) {
                throw new UserException("Phone number is already registered.");
            }

            if (user == null) {
                // Create new user
                user = new User();
                user.setEmail(request.getEmail());
                user.setPhone(request.getPhone());
                user.setFirstName(request.getFirstName());
                user.setLastName(request.getLastName());
//                user.setRole(UserRoleEnum.ROLE_STAFF);
                user.setIsActive(true);
                user = userRepo.save(user);
            } else {
                if (counterStaffRepo.existsByUserId(user.getId())) {
                    throw new BusinessException("User is already registered as counter staff");
                }

                // Update existing user details if needed
                user.setFirstName(request.getFirstName());
                user.setLastName(request.getLastName());
                user.setPhone(request.getPhone());
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
            userRole.setRole(UserRoleEnum.ROLE_COUNTER_STAFF);
            userRole.setTheatreId(request.getTheatreId());
            userRole.setIsActive(true);
            // Set assignedBy if you have current user context
            // userRole.setAssignedBy(getCurrentUserId());
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

            // Set role-specific fields
            if (request.getStaffType() == CounterStaffRoleEnum.ROLE_COUNTER_STAFF) {
                counterStaff.setCounterNumber(request.getCounterNumber());
                counterStaff.setCanIssueRefunds(request.getCanIssueRefunds() != null ? request.getCanIssueRefunds() : false);
            } else if (request.getStaffType() == CounterStaffRoleEnum.ROLE_VERIFICATION_STAFF) {
                counterStaff.setGateNumber(request.getGateNumber());
            }

            CounterStaff savedStaff = counterStaffRepo.save(counterStaff);
            log.info("Counter staff registered successfully with ID: {}", savedStaff.getId());

            return savedStaff;
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

        // Helper methods
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
}