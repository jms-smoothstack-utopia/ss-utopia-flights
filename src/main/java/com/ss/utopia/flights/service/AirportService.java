package com.ss.utopia.flights.service;

import com.ss.utopia.flights.dto.airport.CreateAirportDto;
import com.ss.utopia.flights.dto.airport.UpdateAirportDto;
import com.ss.utopia.flights.entity.airport.Airport;
import java.util.List;

public interface AirportService {

  Airport getAirportById(String id);

  List<Airport> getAllAirports();

  Airport createNewAirport(CreateAirportDto createAirportDto);

  void updateAirport(String id, UpdateAirportDto updateAirportDto);

  void deleteAirport(String id);

}
