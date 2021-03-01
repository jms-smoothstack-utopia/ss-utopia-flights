package com.ss.utopia.flights.service;

import com.ss.utopia.flights.dto.airplane.AirplaneDto;
import com.ss.utopia.flights.entity.airplane.Airplane;
import com.ss.utopia.flights.exception.NoSuchAirplaneException;
import com.ss.utopia.flights.repository.AirplaneRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class AirplaneServiceImpl implements AirplaneService {

  private final AirplaneRepository repository;

  public AirplaneServiceImpl(AirplaneRepository repository) {
    this.repository = repository;
  }

  @Override
  public List<Airplane> getAllAirplanes() {
    return repository.findAll();
  }

  @Override
  public Airplane getAirplaneById(Long id) {
    return repository.findById(id)
        .orElseThrow(() -> new NoSuchAirplaneException(id));
  }

  @Override
  public Airplane createNewAirplane(AirplaneDto airplaneDto) {
    var airplane = airplaneDto.mapToEntity();
    return repository.save(airplane);
  }

  @Override
  public void updateAirplane(Long id, AirplaneDto updateAirplaneDto) {
    var airplane = getAirplaneById(id);
    updateAirplaneDto.update(airplane);
    repository.save(airplane);
  }

  @Override
  public void deleteAirplane(Long id) {
    repository.findById(id)
        .ifPresent(repository::delete);
  }
}
