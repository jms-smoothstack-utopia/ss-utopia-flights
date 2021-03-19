package com.ss.utopia.flights.repository;

import com.ss.utopia.flights.entity.flight.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {

}
