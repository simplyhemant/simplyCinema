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

    private Long userId;         // User making the booking
    private Long showId;         // Show being booked
    private List<Long> seatIds;  // Seats to book
    private PaymentMethod paymentMethod;  // Chosen payment method
    private String couponCode;   // Optional coupon

}
