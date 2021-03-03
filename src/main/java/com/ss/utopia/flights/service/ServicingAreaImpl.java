package com.ss.utopia.flights.service;

import com.ss.utopia.flights.dto.airport.ServicingAreaDto;
import com.ss.utopia.flights.entity.airport.ServicingArea;
import com.ss.utopia.flights.exception.DuplicateAirportException;
import com.ss.utopia.flights.repository.ServicingAreaRepository;
import java.util.List;
import java.util.NoSuchElementException;
import javax.naming.NameAlreadyBoundException;
import org.springframework.stereotype.Service;

@Service
public class ServicingAreaImpl implements ServicingAreaService {

  private final ServicingAreaRepository servicingAreaRepository;

  public ServicingAreaImpl(
      ServicingAreaRepository servicingAreaRepository) {
    this.servicingAreaRepository = servicingAreaRepository;
  }

  @Override
  public List<ServicingArea> getAllAreas() {
    return servicingAreaRepository.findAll();
  }

  @Override
  public ServicingArea getServicingAreaById(Long id) {
    return servicingAreaRepository.findById(id).orElseThrow(NoSuchElementException::new);
  }

  @Override
  public ServicingArea createNewServicingArea(ServicingAreaDto servicingAreaDto) {
    servicingAreaRepository.findByServicingArea(servicingAreaDto.getServicingArea())
        .ifPresent(airport -> { throw new Exception});


    var servicingAreaEntity = ServicingArea
        .builder()
        .servicingArea(servicingAreaDto.getServicingArea())
        .build();
    return servicingAreaRepository.save(servicingAreaEntity);
  }
}
