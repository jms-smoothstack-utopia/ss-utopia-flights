package com.ss.utopia.flights.service;

import com.ss.utopia.flights.dto.flight.CreateFlightDto;
import com.ss.utopia.flights.dto.flight.UpdateFlightDto;
import com.ss.utopia.flights.dto.flight.UpdateSeatDto;
import com.ss.utopia.flights.entity.flight.Flight;
import com.ss.utopia.flights.entity.flight.Seat;
import com.ss.utopia.flights.entity.flight.SeatStatus;
import com.ss.utopia.flights.exception.NoSuchFlightException;
import com.ss.utopia.flights.repository.FlightRepository;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class FlightServiceImpl implements FlightService {

  private final FlightRepository repository;
  private final AirportService airportService;
  private final AirplaneService airplaneService;

  public FlightServiceImpl(FlightRepository repository,
                           AirportService airportService,
                           AirplaneService airplaneService) {
    this.repository = repository;
    this.airportService = airportService;
    this.airplaneService = airplaneService;
  }

  @Override
  public List<Flight> getAllFlights() {
    return repository.findAll();
  }

  @Override
  public Flight getFlightById(Long id) {
    return repository.findById(id)
        .orElseThrow(() -> new NoSuchFlightException(id));
  }

  @Override
  public Flight createNewFlight(CreateFlightDto createFlightDto) {
    var origin = airportService.getAirportById(createFlightDto.getOriginId());
    var destination = airportService.getAirportById(createFlightDto.getDestinationId());
    var airplane = airplaneService.getAirplaneById(createFlightDto.getAirplaneId());

    var flight = Flight.builder()
        .origin(origin)
        .destination(destination)
        .airplane(airplane)
        .build();

    flight = repository.save(flight);

    // add seats to the flight
    var flightId = flight.getId();
    var seats = new ArrayList<Seat>();

    airplane.getSeatConfigurations()
        .forEach(config -> {
          for (int row = 0; row < config.getNumRows(); row++) {
            for (char col = 'A'; col < config.getNumSeatsPerRow(); col++) {
              var seat = Seat.builder()
                  .id(String.format("%d-%d%c", flightId, row, col))
                  .seatRow(row + 1)
                  .seatColumn(col)
                  .seatClass(config.getSeatClass())
                  .seatStatus(SeatStatus.AVAILABLE)
                  .price(createFlightDto.getBaseSeatPrice())
                  .build();
              seats.add(seat);
            }
          }
        });

    flight.setSeats(seats);

    return repository.save(flight);
  }

  @Override
  public void updateFlight(Long id, UpdateFlightDto updateFlightDto) {
    //todo this is a complicated use case
    throw new IllegalStateException("NOT IMPLEMENTED");
//    var toUpdate = getFlightById(id);
//    updateFlightDto.update(toUpdate);
//    repository.save(toUpdate);
  }

  @Override
  public void deleteFlight(Long id) {
    repository.findById(id)
        .ifPresent(repository::delete);
  }

  public List<Seat> getFlightSeats(Long flightId) {
    var flight = getFlightById(flightId);
    return flight.getSeats();
  }

  @Override
  public void updateFlightSeats(Long flightId, Map<String, UpdateSeatDto> seatDtoMap) {
    var flight = getFlightById(flightId);

    flight.getSeats()
        .stream()
        .filter(seat -> seatDtoMap.containsKey(seat.getId()))
        .forEach(seat -> {
          var dto = seatDtoMap.get(seat.getId());
          if (dto.getPrice() != null && dto.getPrice().compareTo(BigDecimal.ZERO) > 0) {
            seat.setPrice(dto.getPrice());
          }
          if (dto.getSeatStatus() != null) {
            seat.setSeatStatus(dto.getSeatStatus());
          }
        });

    repository.save(flight);
  }
}
