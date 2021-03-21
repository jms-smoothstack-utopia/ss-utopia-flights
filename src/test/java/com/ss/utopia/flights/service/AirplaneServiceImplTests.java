package com.ss.utopia.flights.service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.ss.utopia.flights.dto.airplane.AirplaneDto;
import com.ss.utopia.flights.entity.airplane.Airplane;
import com.ss.utopia.flights.exception.NoSuchAirplaneException;
import com.ss.utopia.flights.repository.AirplaneRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class AirplaneServiceImplTests {

  AirplaneRepository airplaneRepository = Mockito.mock(AirplaneRepository.class);
  AirplaneService service = new AirplaneServiceImpl(airplaneRepository);

  @Test
  void test_getAllAirplanes_ReturnsExpectedList() {
    var expected = List.of(Airplane.builder().build());

    when(airplaneRepository.findAll()).thenReturn(expected);

    var actual = service.getAllAirplanes();

    assertEquals(expected, actual);
  }

  @Test
  void test_getAirplaneById_ThrowsNoSuchAirplaneExceptionOnNotFound() {
    when(airplaneRepository.findById(any()))
        .thenReturn(Optional.empty());

    assertThrows(NoSuchAirplaneException.class,
                 () -> service.getAirplaneById(1L));
  }

  @Test
  void test_getAirplaneById_ReturnsExpectedResult() {
    var expected = Airplane.builder().id(1L).build();
    when(airplaneRepository.findById(expected.getId()))
        .thenReturn(Optional.of(expected));

    var actual = service.getAirplaneById(expected.getId());

    assertEquals(expected, actual);
  }

  @Test
  void test_createNewAirplane_SavesToRepository() {
    service.createNewAirplane(AirplaneDto.builder().build());
    Mockito.verify(airplaneRepository).save(any());
  }

  @Test
  void test_deleteAirplane_PerformsDeletion() {
    var expected = Airplane.builder().id(1L).build();
    when(airplaneRepository.findById(expected.getId()))
        .thenReturn(Optional.of(expected));

    service.deleteAirplane(expected.getId());

    Mockito.verify(airplaneRepository).findById(expected.getId());
    Mockito.verify(airplaneRepository).delete(expected);
  }
}
