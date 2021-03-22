package com.ss.utopia.flights.repository;

import com.ss.utopia.flights.entity.airport.ServicingArea;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServicingAreaRepository extends JpaRepository<ServicingArea, Long> {

  Optional<ServicingArea> findByAreaName(String servicingArea);
}
