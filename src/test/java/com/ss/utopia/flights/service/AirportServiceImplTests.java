package com.ss.utopia.flights.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.ss.utopia.flights.dto.airport.CreateAirportDto;
import com.ss.utopia.flights.dto.airport.UpdateAirportDto;
import com.ss.utopia.flights.entity.airport.Airport;
import com.ss.utopia.flights.exception.DuplicateAirportException;
import com.ss.utopia.flights.exception.NoSuchAirportException;
import com.ss.utopia.flights.repository.AirportRepository;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class AirportServiceImplTests {

  AirportRepository airportRepository = Mockito.mock(AirportRepository.class);
  ServicingAreaService servicingAreaService = Mockito.mock(ServicingAreaService.class);

  AirportService airportService = new AirportServiceImpl(airportRepository, servicingAreaService);


  @Test
  void test_getAllAirports_ReturnsAListOfAirports() {
    when(airportRepository.findAll()).thenReturn(List.of(Airport.builder().build()));

    assertEquals(1, airportService.getAllAirports().size());
  }

  @Test
  void test_createNewAirport_ThrowsDuplicateAirportExceptionOnDuplicateIata() {
    when(airportRepository.findById(any())).thenReturn(Optional.of(Airport.builder().build()));

    assertThrows(DuplicateAirportException.class,
                 () -> airportService.createNewAirport(CreateAirportDto.builder().build()));
  }

  @Test
  void test_createNewAirport_VerifiesServicingAreaExists() {
    when(airportRepository.findById(any())).thenReturn(Optional.empty());

    airportService.createNewAirport(CreateAirportDto.builder().servicingAreaId(5L).build());

    Mockito.verify(servicingAreaService).getServicingAreaById(5L);
    Mockito.verify(airportRepository).save(any());
  }

  @Test
  void test_createNewAirport_ThrowsNoSuchElementExceptionIfServicingAreaDoesNotExist() {
    when(airportRepository.findById(any())).thenReturn(Optional.empty());

    when(servicingAreaService.getServicingAreaById(any()))
        .thenThrow(new NoSuchElementException());

    assertThrows(NoSuchElementException.class,
                 () -> airportService.createNewAirport(CreateAirportDto.builder().build()));
  }

  @Test
  void test_updateAirport_ThrowsNoSuchAirportIfNotFound() {
    when(airportRepository.findById(any())).thenReturn(Optional.empty());

    assertThrows(NoSuchAirportException.class,
                 () -> airportService.updateAirport("LAX", UpdateAirportDto.builder().build()));
  }

  @Test
  void test_updateAirport_SavesToRepository() {
    when(airportRepository.findById(any()))
        .thenReturn(Optional.ofNullable(Airport.builder().build()));

    airportService.updateAirport("LAX", UpdateAirportDto.builder().build());

    Mockito.verify(airportRepository).save(any());
  }

  @Test
  void test_deleteAirport_DeletesOnFind() {
    var airport = Airport.builder().build();
    when(airportRepository.findById("LAX"))
        .thenReturn(Optional.of(airport));

    airportService.deleteAirport("LAX");

    Mockito.verify(airportRepository).findById("LAX");
    Mockito.verify(airportRepository).delete(airport);
  }
}
