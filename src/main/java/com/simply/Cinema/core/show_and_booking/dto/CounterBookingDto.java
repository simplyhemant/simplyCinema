package com.simply.Cinema.core.show_and_booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CounterBookingDto {

    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private Long showId;
    private List<Long> seatIds;
    private Long counterStaffId;
    private String paymentMode;
    private String movie;
    private String screenId;
    private String ticketPrice;


}
