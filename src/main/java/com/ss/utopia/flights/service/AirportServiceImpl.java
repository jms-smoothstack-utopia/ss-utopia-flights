package com.ss.utopia.flights.service;

import com.ss.utopia.flights.dto.airport.CreateAirportDto;
import com.ss.utopia.flights.dto.airport.UpdateAirportDto;
import com.ss.utopia.flights.entity.airport.Airport;
import com.ss.utopia.flights.entity.airport.ServicingArea;
import com.ss.utopia.flights.exception.DuplicateAirportException;
import com.ss.utopia.flights.exception.NoSuchAirportException;
import com.ss.utopia.flights.repository.AirportRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AirportServiceImpl implements AirportService {

  private final AirportRepository repository;
  private final ServicingAreaService servicingAreaService;

  @Override
  public Airport getAirportById(String id) {
    return repository.findById(id)
        .orElseThrow(() -> new NoSuchAirportException(id));
  }

  @Override
  public List<Airport> getAllAirports() {
    return repository.findAll();
  }

  @Override
  public Airport createNewAirport(CreateAirportDto createAirportDto) {
    repository.findById(createAirportDto.getIataId())
        .ifPresent(airport -> {
          throw new DuplicateAirportException(airport.getIataId());
        });

    //Need to map to entity
    ServicingArea servicingArea = servicingAreaService.getServicingAreaById(createAirportDto.getServicingAreaId());

    return repository.save(createAirportDto.mapToEntity(servicingArea));
  }

  @Override
  public void updateAirport(String id, UpdateAirportDto updateAirportDto) {
    var toUpdate = getAirportById(id);
    updateAirportDto.update(toUpdate);
    repository.save(toUpdate);
  }

  @Override
  public void deleteAirport(String id) {
    //fixme handle FK constraint
    repository.findById(id)
        .ifPresent(repository::delete);
  }
}
