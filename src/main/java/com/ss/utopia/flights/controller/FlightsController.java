package com.ss.utopia.flights.controller;

import com.ss.utopia.flights.dto.flight.CreateFlightDto;
import com.ss.utopia.flights.dto.flight.UpdateFlightDto;
import com.ss.utopia.flights.dto.flight.UpdateSeatDto;
import com.ss.utopia.flights.entity.flight.Flight;
import com.ss.utopia.flights.entity.flight.Seat;
import com.ss.utopia.flights.service.FlightService;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(EndpointConstants.FLIGHTS_ENDPOINT)
public class FlightsController {

  public static final String MAPPING = EndpointConstants.FLIGHTS_ENDPOINT;

  private final FlightService service;

  public FlightsController(FlightService service) {
    this.service = service;
  }

  @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
  public ResponseEntity<List<Flight>> getAllFlights() {
    log.info("GET Flight all");
    var airports = service.getAllFlights();
    if (airports.isEmpty()) {
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.ok(airports);
  }

  @GetMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE,
      MediaType.APPLICATION_XML_VALUE})
  public ResponseEntity<Flight> getFlightById(@PathVariable Long id) {
    log.info("GET Flight id=" + id);
    return ResponseEntity.of(Optional.ofNullable(service.getFlightById(id)));
  }

  @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
  public ResponseEntity<Flight> createNewFlight(@Valid @RequestBody CreateFlightDto airplaneDto) {
    log.info("POST Flight");
    var flight = service.createNewFlight(airplaneDto);
    var uri = URI.create(MAPPING + "/" + flight.getId());
    return ResponseEntity.created(uri).body(flight);
  }

  @PutMapping(value = "/{id}", consumes = {MediaType.APPLICATION_JSON_VALUE,
      MediaType.APPLICATION_XML_VALUE})
  public ResponseEntity<?> updateFlight(@PathVariable Long id,
                                        @Valid @RequestBody UpdateFlightDto airplaneDto) {
    //todo this is a complicated use case that needs to be carefully considered
    // ideally, we would prefer a method that updates individual seats
    log.info("PUT Flight id=" + id);
    service.updateFlight(id, airplaneDto);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteFlight(@PathVariable Long id) {
    log.info("DELETE Flight id=" + id);
    service.deleteFlight(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{flightId}/seats")
  public ResponseEntity<List<Seat>> getFlightSeats(@PathVariable Long flightId) {
    log.info("GET Seat all flightId=" + flightId);
    return ResponseEntity.of(Optional.ofNullable(service.getFlightSeats(flightId)));
  }

  @PutMapping("/{flightId}/seats")
  public ResponseEntity<?> updateSeat(@PathVariable Long flightId,
                                      @Valid @RequestBody Map<String, UpdateSeatDto> seatDtoMap) {
    log.info("PUT Seat flightId=" + flightId);
    service.updateFlightSeats(flightId, seatDtoMap);
    return ResponseEntity.noContent().build();
  }
}
