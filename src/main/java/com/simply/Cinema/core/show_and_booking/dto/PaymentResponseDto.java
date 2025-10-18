package com.simply.Cinema.core.show_and_booking.dto;

import com.simply.Cinema.core.show_and_booking.Enum.PaymentMethod;
import com.simply.Cinema.core.show_and_booking.Enum.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseDto {

    private Long paymentId;
    private Long bookingId;
    private String transactionId;
    private PaymentMethod method;
    private PaymentStatus status;
    private Double amount;
    private String gatewayResponse;
    private LocalDateTime createdAt;

    private String paymentLinkId;
    private String paymentUrl;
}
