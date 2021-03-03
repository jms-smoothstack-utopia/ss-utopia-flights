package com.ss.utopia.flights.service;

import com.ss.utopia.flights.dto.flight.CreateFlightDto;
import com.ss.utopia.flights.dto.flight.FlightSearchDto;
import com.ss.utopia.flights.dto.flight.UpdateFlightDto;
import com.ss.utopia.flights.dto.flight.UpdateSeatDto;
import com.ss.utopia.flights.entity.flight.Flight;
import com.ss.utopia.flights.entity.flight.Seat;
import java.util.List;
import java.util.Map;

public interface FlightService {

  List<Flight> getAllFlights();

  Flight getFlightById(Long id);

  Flight createNewFlight(CreateFlightDto createFlightDto);

  void updateFlight(Long id, UpdateFlightDto updateFlightDto);

  void deleteFlight(Long id);

  List<Seat> getFlightSeats(Long flightId);

  void updateFlightSeats(Long flightId, Map<String, UpdateSeatDto> seatDtoList);

  Map<String, ?> getFlightByCriteria(FlightSearchDto flightSearchDto);
}
