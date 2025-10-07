package com.simply.Cinema.core.show_and_booking.dto;

import com.simply.Cinema.core.show_and_booking.Enum.PaymentMethod;
import com.simply.Cinema.core.show_and_booking.Enum.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingPaymentDto {

    private Long bookingId;
    private PaymentMethod paymentMethod;
    private Double amount;
    private String transactionId;
    private PaymentStatus status;
    private String gatewayResponse;

}
