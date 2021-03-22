package com.ss.utopia.flights.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.ss.utopia.flights.dto.airport.ServicingAreaDto;
import com.ss.utopia.flights.entity.airport.ServicingArea;
import com.ss.utopia.flights.repository.ServicingAreaRepository;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.dao.DuplicateKeyException;

public class ServicingAreaServiceImplTests {

  ServicingAreaRepository servicingAreaRepository =
      Mockito.mock(ServicingAreaRepository.class);

  ServicingAreaService servicingAreaService =
      new ServicingAreaServiceImpl(servicingAreaRepository);

  @Test
  void test_getAllAreas_ReturnsAList() {
    var expected = List.of(ServicingArea.builder().id(1L).build());

    when(servicingAreaRepository.findAll()).thenReturn(expected);

    var result = servicingAreaService.getAllAreas();

    assertEquals(expected, result);
  }

  @Test
  void test_getServicingAreaById_ReturnsExpectedResult() {
    var expected = ServicingArea.builder().id(1L).build();

    when(servicingAreaRepository.findById(expected.getId()))
        .thenReturn(Optional.of(expected));

    var actual = servicingAreaService.getServicingAreaById(expected.getId());

    assertEquals(expected, actual);
  }

  @Test
  void test_getServicingAreaById_ThrowsNoSuchElementExceptionOnNotFound() {
    when(servicingAreaRepository.findById(any())).thenReturn(Optional.empty());

    assertThrows(NoSuchElementException.class,
                 () -> servicingAreaService.getServicingAreaById(1L));
  }

  @Test
  void test_returnServicingArea_ReturnsExpectedResult() {
    var expected = ServicingArea.builder().id(1L).areaName("wherever").build();

    when(servicingAreaRepository.findByAreaName(expected.getAreaName()))
        .thenReturn(Optional.of(expected));

    var actual = servicingAreaService.returnServicingArea(expected.getAreaName());

    assertEquals(expected, actual);
  }

  @Test
  void test_returnServicingArea_ThrowsNoSuchElementIfNotFound() {
    when(servicingAreaRepository.findByAreaName(any()))
        .thenReturn(Optional.empty());

    assertThrows(NoSuchElementException.class,
                 () -> servicingAreaService.returnServicingArea("whatever"));
  }

  @Test
  void test_createNewServicingArea_ThrowsExceptionIfAlreadyPresent() {
    var expected = ServicingArea.builder().id(1L).areaName("wherever").build();
    when(servicingAreaRepository.findByAreaName(any()))
        .thenReturn(Optional.of(expected));

    assertThrows(DuplicateKeyException.class,
                 () -> servicingAreaService.createNewServicingArea(
                     ServicingAreaDto.builder().build()));
  }

  @Test
  void test_createNewServicingArea_SavesToRepository() {
    var expected = ServicingArea.builder().areaName("somewhere").build();

    when(servicingAreaRepository.findByAreaName(any()))
        .thenReturn(Optional.empty());

    when(servicingAreaRepository.save(any())).thenReturn(expected);

    var actual = servicingAreaService.createNewServicingArea(ServicingAreaDto.builder()
                                                                 .servicingArea(expected.getAreaName())
                                                                 .build());

    assertEquals(expected, actual);

    Mockito.verify(servicingAreaRepository).save(expected);
  }
}
