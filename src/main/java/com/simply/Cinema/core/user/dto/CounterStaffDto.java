package com.simply.Cinema.core.user.dto;

import com.simply.Cinema.core.user.Enum.CounterStaffRoleEnum;
import com.simply.Cinema.core.user.Enum.UserRoleEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CounterStaffDto {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Phone number is required")
    private String phone;

    @NotBlank(message = "First name is required")
    private String firstName;

    private String lastName;

    // Theatre & Role Info
    @NotNull(message = "Theatre ID is required")
    private Long theatreId;

    @NotNull(message = "Staff type is required")
    private CounterStaffRoleEnum staffType;

    private UserRoleEnum UserRole = UserRoleEnum.ROLE_COUNTER_STAFF;

    // Role-Specific Details
    private String counterNumber;   // Booking Counter
    private Boolean canIssueRefunds = false;

    private String gateNumber;

    // Common Fields
    private String deviceId;
    private LocalDate joiningDate;

    private Boolean isOnDuty = false;
    private Boolean isActive = true;

}
