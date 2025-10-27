package com.simply.Cinema.core.show_and_booking.repository;

import com.simply.Cinema.core.show_and_booking.entity.OfflineBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OfflineBookingRepo extends JpaRepository<OfflineBooking, Long> {
}
