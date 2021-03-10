package com.ss.utopia.flights.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ss.utopia.flights.service.AirplaneService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

public class AirplanesControllerSecurityTests extends BaseSecurityTests {

  @MockBean
  AirplaneService airplaneService;

  @BeforeEach
  @Override
  void beforeEach() {
    super.beforeEach();

    when(airplaneService.getAllAirplanes()).thenReturn(List.of(mockAirplane));
    when(airplaneService.getAirplaneById(mockAirplane.getId())).thenReturn(mockAirplane);
    when(airplaneService.createNewAirplane(mockAirplaneDto)).thenReturn(mockAirplane);
  }

  @Test
  void test_getAllAirplanes_OnlyAllowedByEmployeeOrAdmin() throws Exception {
    var alwaysAuthed = List.of(MockUser.ADMIN, MockUser.EMPLOYEE);

    for (var user : alwaysAuthed) {
      mvc
          .perform(
              get(EndpointConstants.API_V_0_1_AIRPLANES)
                  .header("Authorization", getJwt(user)))
          .andExpect(status().isOk());
    }

    var notauthed = List.of(MockUser.TRAVEL_AGENT, MockUser.CUSTOMER, MockUser.DEFAULT);
    for (var user : notauthed) {
      mvc
          .perform(
              get(EndpointConstants.API_V_0_1_AIRPLANES)
                  .header("Authorization", getJwt(user)))
          .andExpect(status().isForbidden());
    }

    mvc
        .perform(
            get(EndpointConstants.API_V_0_1_AIRPLANES))
        .andExpect(status().isForbidden());
  }

  @Test
  void test_getAirplaneById_AllowedByAll() throws Exception {
    var alwaysAuthed = List.of(MockUser.ADMIN,
                               MockUser.EMPLOYEE,
                               MockUser.TRAVEL_AGENT,
                               MockUser.CUSTOMER,
                               MockUser.DEFAULT);
    for (var user : alwaysAuthed) {
      mvc
          .perform(
              get(EndpointConstants.API_V_0_1_AIRPLANES + "/" + mockAirplane.getId())
                  .header("Authorization", getJwt(user)))
          .andExpect(status().isOk());
    }
    mvc
        .perform(
            get(EndpointConstants.API_V_0_1_AIRPLANES + "/" + mockAirplane.getId()))
        .andExpect(status().isOk());
  }

  @Test
  void test_createNewAirplane_OnlyAllowedByEmployeeOrAdmin() throws Exception {
    var alwaysAuthed = List.of(MockUser.ADMIN, MockUser.EMPLOYEE);

    for (var user : alwaysAuthed) {
      mvc
          .perform(
              post(EndpointConstants.API_V_0_1_AIRPLANES)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(new ObjectMapper().writeValueAsString(mockAirplaneDto))
                  .header("Authorization", getJwt(user)))
          .andExpect(status().isCreated());
    }

    var notauthed = List.of(MockUser.TRAVEL_AGENT, MockUser.CUSTOMER, MockUser.DEFAULT);

    for (var user : notauthed) {
      mvc
          .perform(
              post(EndpointConstants.API_V_0_1_AIRPLANES)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(new ObjectMapper().writeValueAsString(mockAirplaneDto))
                  .header("Authorization", getJwt(user)))
          .andExpect(status().isForbidden());
    }

    mvc
        .perform(
            post(EndpointConstants.API_V_0_1_AIRPLANES)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(mockAirplaneDto)))
        .andExpect(status().isForbidden());
  }

  @Test
  void test_updateAirplane_OnlyAllowedByEmployeeOrAdmin() throws Exception {
    var alwaysAuthed = List.of(MockUser.ADMIN, MockUser.EMPLOYEE);

    for (var user : alwaysAuthed) {
      mvc
          .perform(
              put(EndpointConstants.API_V_0_1_AIRPLANES + "/" + mockAirplane.getId())
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(new ObjectMapper().writeValueAsString(mockAirplaneDto))
                  .header("Authorization", getJwt(user)))
          .andExpect(status().isNoContent());
    }

    var notauthed = List.of(MockUser.TRAVEL_AGENT, MockUser.CUSTOMER, MockUser.DEFAULT);

    for (var user : notauthed) {
      mvc
          .perform(
              put(EndpointConstants.API_V_0_1_AIRPLANES + "/" + mockAirplane.getId())
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(new ObjectMapper().writeValueAsString(mockAirplaneDto))
                  .header("Authorization", getJwt(user)))
          .andExpect(status().isForbidden());
    }

    mvc
        .perform(
            put(EndpointConstants.API_V_0_1_AIRPLANES + "/" + mockAirplane.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(mockAirplaneDto)))
        .andExpect(status().isForbidden());
  }

  @Test
  void test_deleteAirplane_OnlyAllowedByAdmin() throws Exception {
    var alwaysAuthed = List.of(MockUser.ADMIN);

    for (var user : alwaysAuthed) {
      mvc
          .perform(
              delete(EndpointConstants.API_V_0_1_AIRPLANES + "/" + mockAirplane.getId())
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
              delete(EndpointConstants.API_V_0_1_AIRPLANES + "/" + mockAirplane.getId())
                  .header("Authorization", getJwt(user)))
          .andExpect(status().isForbidden());
    }

    mvc
        .perform(
            delete(EndpointConstants.API_V_0_1_AIRPLANES + "/" + mockAirplane.getId()))
        .andExpect(status().isForbidden());
  }
}
