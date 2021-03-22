package com.ss.utopia.flights;

import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ss.utopia.flights.controller.EndpointConstants;
import com.ss.utopia.flights.security.SecurityConstants;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@ActiveProfiles("integration-test")
@SpringBootTest
public class BaseIntegrationTest {


  @Autowired
  WebApplicationContext wac;
  @MockBean
  SecurityConstants securityConstants;

  MockMvc mvc;

  @BeforeEach
  void beforeEach() {
    mvc = MockMvcBuilders
        .webAppContextSetup(wac)
        .build();
  }

  @Test
  void test_DoesNotThrowLazyInitializationException() throws Exception {
    var endpoint = EndpointConstants.API_V_0_1_FLIGHTS
        + "/flight-search?origin=IAD&destinations=LAX&departure=2021-05-17&return=2021-05-28";
    try {
      mvc
          .perform(
              get(endpoint))
          .andExpect(status().isOk());
    } catch (LazyInitializationException ex) {
      ex.printStackTrace();
      fail(ex);
    } catch (Exception ex) {
      ex.printStackTrace();
      throw ex;
    }

  }

  @Test
  void test_DoesNotThrowUnsafeThreadOperation() {
    var endpoint = EndpointConstants.API_V_0_1_FLIGHTS
        + "/flight-search?origin=D.C&destinations=LAX&departure=2021-05-17&multihop=true";
    try {
      mvc
          .perform(
              get(endpoint))
          .andExpect(status().isOk());
    } catch (Exception ex) {
      fail(ex);
    }
  }
}
