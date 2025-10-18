package com.simply.Cinema.core.show_and_booking.dto;

import com.simply.Cinema.core.show_and_booking.Enum.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {

    private Long userId;
    private Long showId;
    private List<Long> seatIds;
    private PaymentMethod paymentMethod;
    private String couponCode;

    private String Email;

    private String paymentLinkId;        // Razorpay payment link ID
    private String paymentToken;         // Razorpay payment/transaction ID

}
