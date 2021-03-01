package com.ss.utopia.flights.service;

import com.ss.utopia.flights.dto.airplane.AirplaneDto;
import com.ss.utopia.flights.entity.airplane.Airplane;
import java.util.List;

public interface AirplaneService {

  List<Airplane> getAllAirplanes();
  Airplane getAirplaneById(Long id);
  Airplane createNewAirplane(AirplaneDto airplaneDto);
  void updateAirplane(Long id, AirplaneDto updateAirplaneDto);
  void deleteAirplane(Long id);
}
