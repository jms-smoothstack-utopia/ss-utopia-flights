package com.ss.utopia.flights.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ss.utopia.flights.service.ServicingAreaService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

public class ServicingAreaControllerSecurityTests extends BaseSecurityTests {

  @MockBean
  ServicingAreaService servicingAreaService;

  @BeforeEach
  @Override
  void beforeEach() {
    super.beforeEach();
    Mockito.reset(servicingAreaService);
    when(servicingAreaService.getAllAreas()).thenReturn(List.of(mockServicingArea));
    when(servicingAreaService.getServicingAreaById(mockServicingArea.getId()))
        .thenReturn(mockServicingArea);
    when(servicingAreaService.createNewServicingArea(mockServicingAreaDto)).thenReturn(
        mockServicingArea);
  }

  @Test
  void test_getAllServicingAreas_AllowedByAll() throws Exception {
    var alwaysAuthed = List.of(MockUser.ADMIN,
                               MockUser.EMPLOYEE,
                               MockUser.TRAVEL_AGENT,
                               MockUser.CUSTOMER,
                               MockUser.DEFAULT);
    for (var user : alwaysAuthed) {
      mvc
          .perform(
              get(EndpointConstants.API_V_0_1_SERVICING_AREA)
                  .header("Authorization", getJwt(user)))
          .andExpect(status().isOk());
    }
    mvc
        .perform(
            get(EndpointConstants.API_V_0_1_SERVICING_AREA))
        .andExpect(status().isOk());
  }

  @Test
  void test_getServicingAreaById_AllowedByAll() throws Exception {
    var alwaysAuthed = List.of(MockUser.ADMIN,
                               MockUser.EMPLOYEE,
                               MockUser.TRAVEL_AGENT,
                               MockUser.CUSTOMER,
                               MockUser.DEFAULT);
    for (var user : alwaysAuthed) {
      mvc
          .perform(
              get(EndpointConstants.API_V_0_1_SERVICING_AREA + "/" + mockServicingArea.getId())
                  .header("Authorization", getJwt(user)))
          .andExpect(status().isOk());
    }
    mvc
        .perform(
            get(EndpointConstants.API_V_0_1_SERVICING_AREA + "/" + mockServicingArea.getId()))
        .andExpect(status().isOk());
  }

  @Test
  void test_createNewServicingArea_OnlyAllowedByAdmin() throws Exception {
    var alwaysAuthed = List.of(MockUser.ADMIN);

    for (var user : alwaysAuthed) {
      mvc
          .perform(
              post(EndpointConstants.API_V_0_1_SERVICING_AREA)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(new ObjectMapper().writeValueAsString(mockServicingAreaDto))
                  .header("Authorization", getJwt(user)))
          .andExpect(status().isCreated());
    }

    var notauthed = List.of(MockUser.EMPLOYEE,
                            MockUser.TRAVEL_AGENT,
                            MockUser.CUSTOMER,
                            MockUser.DEFAULT);

    for (var user : notauthed) {
      mvc
          .perform(
              post(EndpointConstants.API_V_0_1_SERVICING_AREA)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(new ObjectMapper().writeValueAsString(mockServicingAreaDto))
                  .header("Authorization", getJwt(user)))
          .andExpect(status().isForbidden());
    }

    mvc
        .perform(
            post(EndpointConstants.API_V_0_1_SERVICING_AREA)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(mockServicingAreaDto)))
        .andExpect(status().isForbidden());
  }
}
