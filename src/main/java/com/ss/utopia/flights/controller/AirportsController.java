package com.ss.utopia.flights.controller;

import com.ss.utopia.flights.dto.airport.CreateAirportDto;
import com.ss.utopia.flights.dto.airport.UpdateAirportDto;
import com.ss.utopia.flights.entity.airport.Airport;
import com.ss.utopia.flights.service.AirportService;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@RestController
@RequestMapping("/airports")
public class AirportsController {

  public static final String MAPPING = "/airports";

  private static final Logger LOGGER = LoggerFactory.getLogger(AirportsController.class);
  private final AirportService service;

  public AirportsController(AirportService service) {
    this.service = service;
  }


  @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
  public ResponseEntity<List<Airport>> getAllAirports() {
    LOGGER.info("GET Airport all");
    var airports = service.getAllAirports();
    if (airports.isEmpty()) {
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.ok(airports);
  }

  @GetMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE,
      MediaType.APPLICATION_XML_VALUE})
  public ResponseEntity<Airport> getAirportById(@PathVariable String id) {
    LOGGER.info("GET Airport id=" + id);
    return ResponseEntity.of(Optional.ofNullable(service.getAirportById(id)));
  }


  @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
  public ResponseEntity<Airport> createNewAirport(@Valid @RequestBody CreateAirportDto createAirportDto) {
    LOGGER.info("POST Airport");
    var airport = service.createNewAirport(createAirportDto);
    var uri = URI.create(MAPPING + "/" + airport.getIataId());
    return ResponseEntity.created(uri).body(airport);
  }

  @PutMapping(value = "/{id}", consumes = {MediaType.APPLICATION_JSON_VALUE,
      MediaType.APPLICATION_XML_VALUE})
  public ResponseEntity<?> updateAirport(@PathVariable String id,
                                         @Valid @RequestBody UpdateAirportDto updateAirportDto) {
    LOGGER.info("PUT Airport id=" + id);
    service.updateAirport(id, updateAirportDto);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteAirport(@PathVariable String id) {
    LOGGER.info("DELETE Airport id=" + id);
    service.deleteAirport(id);
    return ResponseEntity.noContent().build();
  }
}