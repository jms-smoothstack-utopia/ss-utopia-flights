package com.ss.utopia.flights.controller;

import com.ss.utopia.flights.dto.airport.ServicingAreaDto;
import com.ss.utopia.flights.entity.airport.ServicingArea;
import com.ss.utopia.flights.service.ServicingAreaService;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/servicing-area")
public class ServicingAreaController {


private final ServicingAreaService servicingAreaService;

  public ServicingAreaController(ServicingAreaService servicingAreaService) {
    this.servicingAreaService = servicingAreaService;
  }

  @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
  public ResponseEntity<List<ServicingArea>> getAllServicingAreas()
  {
    log.info("Get all servicing areas");
    List<ServicingArea> servicingAreas = servicingAreaService.getAllAreas();
    if (servicingAreas.isEmpty()) {
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.ok(servicingAreas);
  }

  @GetMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE,
      MediaType.APPLICATION_XML_VALUE})
  public ResponseEntity<ServicingArea> getServicingAreaById(@PathVariable Long id) {
    log.info("GET Servicing Area id=" + id);
    return ResponseEntity.of(Optional.ofNullable(servicingAreaService.getServicingAreaById(id)));
  }

  @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
  public ResponseEntity<ServicingArea> createNewServicingArea(@Valid @RequestBody ServicingAreaDto servicingAreaDto){
    log.info("Posting new Servicing Area");
    ServicingArea servicingArea = servicingAreaService.createNewServicingArea(servicingAreaDto);
    var uri = URI.create("/servicing-area/" + servicingArea.getId());
    return ResponseEntity.created(uri).body(servicingArea);
  }
}
