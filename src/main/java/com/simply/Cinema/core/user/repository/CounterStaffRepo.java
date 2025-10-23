package com.simply.Cinema.core.user.repository;

import com.simply.Cinema.core.user.entity.CounterStaff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CounterStaffRepo extends JpaRepository<CounterStaff, Long> {

    Optional<CounterStaff> findByUserId(Long userId);

    boolean existsByUserId(Long userId);

    boolean existsByEmployeeCode(String employeeCode);

    List<CounterStaff> findByTheatreId(Long theatreId);



}
