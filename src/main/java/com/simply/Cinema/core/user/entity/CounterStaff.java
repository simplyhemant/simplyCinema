package com.simply.Cinema.core.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.simply.Cinema.core.location_and_venue.entity.Theatre;
import com.simply.Cinema.core.user.Enum.CounterStaffRoleEnum;
import com.simply.Cinema.core.user.Enum.UserRoleEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "counter_staff")
@NoArgsConstructor
@AllArgsConstructor
public class CounterStaff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theatre_id", nullable = false)
    @JsonIgnore
    private Theatre theatre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CounterStaffRoleEnum staffType;

    @Column(unique = true, nullable = false)
    private String employeeCode;

    private LocalDate joiningDate;

    @OneToOne(cascade = CascadeType.ALL)
    private UserRole userRole;

    // ROLE_COUNTER_STAFF (Booking Counter)
    private String counterNumber;
    private Boolean canIssueRefunds = false;

    // ROLE_VERIFICATION_STAFF (Entry Gate)
    private String gateNumber;

    @JsonIgnore
    private String deviceId; // POS Terminal ID or Scanner Device ID

    // Common
    private Boolean isOnDuty = false;
    private Boolean isActive = true;

  //  private Long supervisorId;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;



}
