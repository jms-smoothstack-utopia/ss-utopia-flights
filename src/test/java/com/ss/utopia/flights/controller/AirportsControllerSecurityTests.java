package com.ss.utopia.flights.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ss.utopia.flights.service.AirportService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

public class AirportsControllerSecurityTests extends BaseSecurityTests {

  @MockBean
  AirportService airportService;

  @BeforeEach
  @Override
  void beforeEach() {
    super.beforeEach();
    Mockito.reset(airportService);
    when(airportService.getAllAirports()).thenReturn(List.of(mockAirport));
    when(airportService.getAirportById(mockAirport.getIataId())).thenReturn(mockAirport);
    when(airportService.createNewAirport(mockCreateDto)).thenReturn(mockAirport);
  }

  @Test
  void test_getAllAirports_AllowedByAll() throws Exception {
    var alwaysAuthed = List.of(MockUser.ADMIN,
                               MockUser.EMPLOYEE,
                               MockUser.TRAVEL_AGENT,
                               MockUser.CUSTOMER,
                               MockUser.DEFAULT);
    for (var user : alwaysAuthed) {
      mvc
          .perform(
              get(EndpointConstants.API_V_0_1_AIRPORTS)
                  .header("Authorization", getJwt(user)))
          .andExpect(status().isOk());
    }

    mvc
        .perform(
            get(EndpointConstants.API_V_0_1_AIRPORTS))
        .andExpect(status().isOk());
  }

  @Test
  void test_getAirportById_AllowedByAll() throws Exception {
    var alwaysAuthed = List.of(MockUser.ADMIN,
                               MockUser.EMPLOYEE,
                               MockUser.TRAVEL_AGENT,
                               MockUser.CUSTOMER,
                               MockUser.DEFAULT);
    for (var user : alwaysAuthed) {
      mvc
          .perform(
              get(EndpointConstants.API_V_0_1_AIRPORTS + "/" + mockAirport.getIataId())
                  .header("Authorization", getJwt(user)))
          .andExpect(status().isOk());
    }

    mvc
        .perform(
            get(EndpointConstants.API_V_0_1_AIRPORTS + "/" + mockAirport.getIataId()))
        .andExpect(status().isOk());
  }

  @Test
  void test_createNewAirport_OnlyAllowedByEmployeeOrAdmin() throws Exception {
    var alwaysAuthed = List.of(MockUser.ADMIN, MockUser.EMPLOYEE);
    for (var user : alwaysAuthed) {
      mvc
          .perform(
              post(EndpointConstants.API_V_0_1_AIRPORTS)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(new ObjectMapper().writeValueAsString(mockCreateDto))
                  .header("Authorization", getJwt(user)))
          .andExpect(status().isCreated());
    }

    var notauthed = List.of(MockUser.TRAVEL_AGENT, MockUser.CUSTOMER, MockUser.DEFAULT);

    for (var user : notauthed) {
      mvc
          .perform(
              post(EndpointConstants.API_V_0_1_AIRPORTS)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(new ObjectMapper().writeValueAsString(mockCreateDto))
                  .header("Authorization", getJwt(user)))
          .andExpect(status().isForbidden());
    }

    mvc
        .perform(
            post(EndpointConstants.API_V_0_1_AIRPORTS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(mockCreateDto)))
        .andExpect(status().isForbidden());
  }

  @Test
  void test_updateAirport_OnlyAllowedByEmployeeOrAdmin() throws Exception {
    var alwaysAuthed = List.of(MockUser.ADMIN, MockUser.EMPLOYEE);
    for (var user : alwaysAuthed) {
      mvc
          .perform(
              put(EndpointConstants.API_V_0_1_AIRPORTS + "/" + mockAirport.getIataId())
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(new ObjectMapper().writeValueAsString(updateAirportDto))
                  .header("Authorization", getJwt(user)))
          .andExpect(status().isNoContent());
    }

    var notauthed = List.of(MockUser.TRAVEL_AGENT, MockUser.CUSTOMER, MockUser.DEFAULT);

    for (var user : notauthed) {
      mvc
          .perform(
              put(EndpointConstants.API_V_0_1_AIRPORTS + "/" + mockAirport.getIataId())
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(new ObjectMapper().writeValueAsString(updateAirportDto))
                  .header("Authorization", getJwt(user)))
          .andExpect(status().isForbidden());
    }

    mvc
        .perform(
            put(EndpointConstants.API_V_0_1_AIRPORTS + "/" + mockAirport.getIataId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(updateAirportDto)))
        .andExpect(status().isForbidden());
  }

  @Test
  void test_deleteAirport_OnlyAllowedByAdmin() throws Exception {
    var alwaysAuthed = List.of(MockUser.ADMIN);

    for (var user : alwaysAuthed) {
      mvc
          .perform(
              delete(EndpointConstants.API_V_0_1_AIRPORTS + "/" + mockAirport.getIataId())
                  .header("Authorization", getJwt(user)))
          .andExpect(status().isNoContent());
    }

    var notauthed = List.of(MockUser.EMPLOYEE,
                            MockUser.TRAVEL_AGENT,
                            MockUser.CUSTOMER,
                            MockUser.DEFAULT);

    for (var user : notauthed) {
      mvc
          .perform(
              delete(EndpointConstants.API_V_0_1_AIRPORTS + "/" + mockAirport.getIataId())
                  .header("Authorization", getJwt(user)))
          .andExpect(status().isForbidden());
    }

    mvc
        .perform(
            delete(EndpointConstants.API_V_0_1_AIRPORTS + "/" + mockAirport.getIataId()))
        .andExpect(status().isForbidden());
  }
}
