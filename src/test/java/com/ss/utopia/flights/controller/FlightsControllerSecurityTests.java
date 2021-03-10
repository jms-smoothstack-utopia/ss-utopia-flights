package com.ss.utopia.flights.controller;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ss.utopia.flights.dto.flight.UpdateSeatDto;
import com.ss.utopia.flights.entity.flight.SeatStatus;
import com.ss.utopia.flights.service.FlightService;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

public class FlightsControllerSecurityTests extends BaseSecurityTests {

  @MockBean
  FlightService flightService;

  @BeforeEach
  @Override
  void beforeEach() {
    super.beforeEach();
    Mockito.reset(flightService);
    when(flightService.getAllFlights()).thenReturn(List.of(mockFlight));
    when(flightService.getFlightById(mockFlight.getId())).thenReturn(mockFlight);
    when(flightService.getFlightSeats(mockFlight.getId())).thenReturn(mockFlight.getSeats());
    when(flightService.getFlightByCriteria(any())).thenReturn(Collections.emptyMap());
    when(flightService.createNewFlight(any())).thenReturn(mockFlight);
  }

  @Test
  void test_getAllFlights_AllowedByAll() throws Exception {
    var alwaysAuthed = List.of(MockUser.ADMIN,
                               MockUser.EMPLOYEE,
                               MockUser.TRAVEL_AGENT,
                               MockUser.CUSTOMER,
                               MockUser.DEFAULT);
    for (var user : alwaysAuthed) {
      mvc
          .perform(
              get(EndpointConstants.API_V_0_1_FLIGHTS)
                  .header("Authorization", getJwt(user)))
          .andExpect(status().isOk());
    }
    mvc
        .perform(
            get(EndpointConstants.API_V_0_1_FLIGHTS))
        .andExpect(status().isOk());
  }

  @Test
  void test_getFlightById_AllowedByAll() throws Exception {
    var alwaysAuthed = List.of(MockUser.ADMIN,
                               MockUser.EMPLOYEE,
                               MockUser.TRAVEL_AGENT,
                               MockUser.CUSTOMER,
                               MockUser.DEFAULT);
    for (var user : alwaysAuthed) {
      mvc
          .perform(
              get(EndpointConstants.API_V_0_1_FLIGHTS + "/" + mockFlight.getId())
                  .header("Authorization", getJwt(user)))
          .andExpect(status().isOk());
    }
    mvc
        .perform(
            get(EndpointConstants.API_V_0_1_FLIGHTS + "/" + mockFlight.getId()))
        .andExpect(status().isOk());
  }

  @Test
  void test_getFlightByCriteria_AllowedByAll() throws Exception {
    var alwaysAuthed = List.of(MockUser.ADMIN,
                               MockUser.EMPLOYEE,
                               MockUser.TRAVEL_AGENT,
                               MockUser.CUSTOMER,
                               MockUser.DEFAULT);
    for (var user : alwaysAuthed) {
      var result = mvc
          .perform(
              get(EndpointConstants.API_V_0_1_FLIGHTS + "/flight-search")
                  .header("Authorization", getJwt(user)))
          .andReturn();
      assertNotEquals(403, result.getResponse().getStatus());
      assertNotEquals(404, result.getResponse().getStatus());
    }
    var result = mvc
        .perform(
            get(EndpointConstants.API_V_0_1_FLIGHTS + "/" + mockFlight.getId()))
        .andReturn();
    assertNotEquals(403, result.getResponse().getStatus());
    assertNotEquals(404, result.getResponse().getStatus());
  }

  @Test
  void test_createNewFlight_OnlyAllowedByEmployeeOrAdmin() throws Exception {
    var alwaysAuthed = List.of(MockUser.ADMIN, MockUser.EMPLOYEE);
    for (var user : alwaysAuthed) {
      mvc
          .perform(
              post(EndpointConstants.API_V_0_1_FLIGHTS)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(mockFlightJson)
                  .header("Authorization", getJwt(user)))
          .andExpect(status().isCreated());
    }

    var notauthed = List.of(MockUser.TRAVEL_AGENT, MockUser.CUSTOMER, MockUser.DEFAULT);
    for (var user : notauthed) {
      mvc
          .perform(
              post(EndpointConstants.API_V_0_1_FLIGHTS)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(mockFlightJson)
                  .header("Authorization", getJwt(user)))
          .andExpect(status().isForbidden());
    }

    mvc
        .perform(
            post(EndpointConstants.API_V_0_1_FLIGHTS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mockFlightJson))
        .andExpect(status().isForbidden());
  }

  @Test
  void test_updateFlight_OnlyAllowedByEmployeeOrAdmin() throws Exception {
    var alwaysAuthed = List.of(MockUser.ADMIN, MockUser.EMPLOYEE);
    for (var user : alwaysAuthed) {
      mvc
          .perform(
              put(EndpointConstants.API_V_0_1_FLIGHTS + "/" + mockFlight.getId())
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(new ObjectMapper().writeValueAsString(mockUpdateFlightDto))
                  .header("Authorization", getJwt(user)))
          .andExpect(status().isNoContent());
    }

    var notauthed = List.of(MockUser.TRAVEL_AGENT, MockUser.CUSTOMER, MockUser.DEFAULT);
    for (var user : notauthed) {
      mvc
          .perform(
              put(EndpointConstants.API_V_0_1_FLIGHTS + "/" + mockFlight.getId())
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(new ObjectMapper().writeValueAsString(mockUpdateFlightDto))
                  .header("Authorization", getJwt(user)))
          .andExpect(status().isForbidden());
    }

    mvc
        .perform(
            put(EndpointConstants.API_V_0_1_FLIGHTS + "/" + mockFlight.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(mockUpdateFlightDto)))
        .andExpect(status().isForbidden());
  }

  @Test
  void test_deleteFlight_OnlyAllowedByAdmin() throws Exception {
    var alwaysAuthed = List.of(MockUser.ADMIN);
    for (var user : alwaysAuthed) {
      mvc
          .perform(
              delete(EndpointConstants.API_V_0_1_FLIGHTS + "/" + mockFlight.getId())
                  .header("Authorization", getJwt(user)))
          .andExpect(status().isNoContent());
    }

    var notauthed = List.of(MockUser.TRAVEL_AGENT,
                            MockUser.CUSTOMER,
                            MockUser.DEFAULT,
                            MockUser.EMPLOYEE);
    for (var user : notauthed) {
      mvc
          .perform(
              delete(EndpointConstants.API_V_0_1_FLIGHTS + "/" + mockFlight.getId())
                  .header("Authorization", getJwt(user)))
          .andExpect(status().isForbidden());
    }

    mvc
        .perform(
            delete(EndpointConstants.API_V_0_1_FLIGHTS + "/" + mockFlight.getId()))
        .andExpect(status().isForbidden());
  }

  @Test
  void test_getFlightSeats_AllowedByAll() throws Exception {
    var alwaysAuthed = List.of(MockUser.ADMIN,
                               MockUser.EMPLOYEE,
                               MockUser.TRAVEL_AGENT,
                               MockUser.CUSTOMER,
                               MockUser.DEFAULT);
    for (var user : alwaysAuthed) {
      mvc
          .perform(
              get(EndpointConstants.API_V_0_1_FLIGHTS + "/" + mockFlight.getId() + "/seats")
                  .header("Authorization", getJwt(user)))
          .andExpect(status().isOk());
    }
    mvc
        .perform(
            get(EndpointConstants.API_V_0_1_FLIGHTS + "/" + mockFlight.getId() + "/seats"))
        .andExpect(status().isOk());
  }

  @Test
  void test_updateSeat_OnlyAllowedByEmployeeOrAdmin() throws Exception {
    var alwaysAuthed = List.of(MockUser.ADMIN, MockUser.EMPLOYEE);

    var input = Map.of("some string", UpdateSeatDto.builder()
        .seatStatus(SeatStatus.HELD)
        .price(BigDecimal.ONE)
        .build());

    for (var user : alwaysAuthed) {
      mvc
          .perform(
              put(EndpointConstants.API_V_0_1_FLIGHTS + "/" + mockFlight.getId() + "/seats")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(new ObjectMapper().writeValueAsString(input))
                  .header("Authorization", getJwt(user)))
          .andExpect(status().isNoContent());
    }

    var notauthed = List.of(MockUser.TRAVEL_AGENT, MockUser.CUSTOMER, MockUser.DEFAULT);
    for (var user : notauthed) {
      mvc
          .perform(
              put(EndpointConstants.API_V_0_1_FLIGHTS + "/" + mockFlight.getId() + "/seats")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(new ObjectMapper().writeValueAsString(input))
                  .header("Authorization", getJwt(user)))
          .andExpect(status().isForbidden());
    }

    mvc
        .perform(
            put(EndpointConstants.API_V_0_1_FLIGHTS + "/" + mockFlight.getId() + "/seats")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(input)))
        .andExpect(status().isForbidden());
  }
}
