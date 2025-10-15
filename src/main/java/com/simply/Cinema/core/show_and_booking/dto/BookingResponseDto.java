package com.simply.Cinema.core.show_and_booking.dto;


import com.simply.Cinema.core.show_and_booking.Enum.BookingStatus;
import com.simply.Cinema.core.show_and_booking.Enum.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponseDto {

    private Long bookingId;
    private String bookingReference;
    private Long userId;
    private Long showId;
    private List<Long> seatIds;
    private Double totalAmount;
    private Double discountAmount;
    private Double finalAmount;
    private PaymentStatus paymentStatus;
    private BookingStatus bookingStatus;
    private String qrCode;
    private String email;


    public void setGuestUserId(Long id) {
    }
}
