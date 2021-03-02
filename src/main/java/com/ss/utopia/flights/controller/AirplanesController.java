package com.ss.utopia.flights.controller;

import com.ss.utopia.flights.dto.airplane.AirplaneDto;
import com.ss.utopia.flights.entity.airplane.Airplane;
import com.ss.utopia.flights.service.AirplaneService;
import java.net.URI;
import java.util.List;
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
@RequestMapping(EndpointConstants.AIRPLANES_ENDPOINT)
public class AirplanesController {

  public static final String MAPPING = EndpointConstants.AIRPLANES_ENDPOINT;

  private final AirplaneService service;

  public AirplanesController(AirplaneService service) {
    this.service = service;
  }

  @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
  public ResponseEntity<List<Airplane>> getAllAirplanes() {
    log.info("GET Airplane all");
    var airports = service.getAllAirplanes();
    if (airports.isEmpty()) {
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.ok(airports);
  }

  @GetMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE,
      MediaType.APPLICATION_XML_VALUE})
  public ResponseEntity<Airplane> getAirplaneById(@PathVariable Long id) {
    log.info("GET Airplane id=" + id);
    return ResponseEntity.of(Optional.ofNullable(service.getAirplaneById(id)));
  }

  @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
  public ResponseEntity<Airplane> createNewAirplane(@Valid @RequestBody AirplaneDto airplaneDto) {
    log.info("POST Airplane");
    var airplane = service.createNewAirplane(airplaneDto);
    var uri = URI.create(MAPPING + "/" + airplane.getId());
    return ResponseEntity.created(uri).body(airplane);
  }

  @PutMapping(value = "/{id}", consumes = {MediaType.APPLICATION_JSON_VALUE,
      MediaType.APPLICATION_XML_VALUE})
  public ResponseEntity<?> updateAirplane(@PathVariable Long id,
                                          @Valid @RequestBody AirplaneDto airplaneDto) {
    log.info("PUT Airplane id=" + id);
    service.updateAirplane(id, airplaneDto);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteAirplane(@PathVariable Long id) {
    log.info("DELETE Airplane id=" + id);
    service.deleteAirplane(id);
    return ResponseEntity.noContent().build();
  }
}
