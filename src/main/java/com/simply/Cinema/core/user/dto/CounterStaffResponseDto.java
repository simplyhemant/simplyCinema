package com.simply.Cinema.core.user.dto;

import com.simply.Cinema.core.user.Enum.CounterStaffRoleEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CounterStaffResponseDto {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private CounterStaffRoleEnum staffType;
    private String employeeCode;
    private LocalDate joiningDate;
    private String counterNumber;
    private String gateNumber;
    private Boolean canIssueRefunds;
    private Boolean isOnDuty;
    private Boolean isActive;
    private Long theatreId;
    private String theatreName;
    private LocalDateTime createdAt;

}
