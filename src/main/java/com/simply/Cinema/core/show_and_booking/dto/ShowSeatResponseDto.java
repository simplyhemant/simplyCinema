package com.simply.Cinema.core.show_and_booking.dto;

import com.simply.Cinema.core.show_and_booking.Enum.ShowSeatStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShowSeatResponseDto {

    private Long id;
    private String rowNumber;
    private String seatNumber;
    private String seatType;
    private Boolean isActive;
    private Double price;
    private ShowSeatStatus status;
    private LocalDateTime createdAt;

}
