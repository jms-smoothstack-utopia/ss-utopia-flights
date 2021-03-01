package com.ss.utopia.flights.service;

import com.ss.utopia.flights.dto.airport.CreateAirportDto;
import com.ss.utopia.flights.dto.airport.UpdateAirportDto;
import com.ss.utopia.flights.entity.airport.Airport;
import com.ss.utopia.flights.exception.DuplicateAirportException;
import com.ss.utopia.flights.exception.NoSuchAirportException;
import com.ss.utopia.flights.repository.AirportRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class AirportServiceImpl implements AirportService {

  private final AirportRepository repository;

  public AirportServiceImpl(AirportRepository repository) {
    this.repository = repository;

  }

  public Airport getAirportById(String id) {
    return repository.findById(id)
        .orElseThrow(() -> new NoSuchAirportException(id));
  }

  public List<Airport> getAllAirports() {
    return repository.findAll();
  }

  @Override
  public Airport createNewAirport(CreateAirportDto createAirportDto) {
    repository.findById(createAirportDto.getIataId())
        .ifPresent(airport -> {
          throw new DuplicateAirportException(airport.getIataId());
        });

    return repository.save(createAirportDto.mapToEntity());
  }

  public void updateAirport(String id, UpdateAirportDto updateAirportDto) {
    var toUpdate = getAirportById(id);
    updateAirportDto.update(toUpdate);
    repository.save(toUpdate);
  }

  public void deleteAirport(String id) {
    //fixme handle FK constraint
    repository.findById(id)
        .ifPresent(repository::delete);
  }
}
