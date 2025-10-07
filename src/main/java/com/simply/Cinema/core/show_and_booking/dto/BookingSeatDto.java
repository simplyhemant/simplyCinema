package com.simply.Cinema.core.show_and_booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingSeatDto {

    private Long bookingId;
    private Long seatId;
    private Double pricePaid;
}
