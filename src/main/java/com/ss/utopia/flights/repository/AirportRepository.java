package com.ss.utopia.flights.repository;

import com.ss.utopia.flights.entity.airport.Airport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AirportRepository extends JpaRepository<Airport, String> {

}
