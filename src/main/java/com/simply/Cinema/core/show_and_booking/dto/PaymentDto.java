package com.simply.Cinema.core.show_and_booking.dto;

import com.simply.Cinema.core.show_and_booking.Enum.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDto {

    private Long bookingId;
    private Double amount;
    private PaymentMethod method;
    private String paymentToken;
    private String email;

    private Long showId;
}
