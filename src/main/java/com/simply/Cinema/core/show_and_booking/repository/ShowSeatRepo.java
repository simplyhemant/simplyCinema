package com.simply.Cinema.core.show_and_booking.repository;

import com.simply.Cinema.core.location_and_venue.entity.Seat;
import com.simply.Cinema.core.show_and_booking.Enum.ShowSeatStatus;
import com.simply.Cinema.core.show_and_booking.entity.Show;
import com.simply.Cinema.core.show_and_booking.entity.ShowSeat;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ShowSeatRepo extends JpaRepository<ShowSeat, Long> {

    List<ShowSeat> findByShow_Id(Long showId);

    List<ShowSeat> findByShow_IdAndStatus(Long showId, ShowSeatStatus status);

    @Modifying
    @Transactional
    @Query("DELETE FROM ShowSeat ss WHERE ss.show.id = :showId")
    void deleteByShowId(@Param("showId") Long showId);

    @Query("SELECT ss FROM ShowSeat ss WHERE ss.show = :show AND ss.seat.id IN :seatIds")
    List<ShowSeat> findByShowAndSeatIds(@Param("show") Show show, @Param("seatIds") List<Long> seatIds);

    int countByShow_Id(Long showId);

    int countByShow_IdAndStatus(Long showId, ShowSeatStatus status);

    List<ShowSeat> findBySeat_Id(Long seatId);
}
