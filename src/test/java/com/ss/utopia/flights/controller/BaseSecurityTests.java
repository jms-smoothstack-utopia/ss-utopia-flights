package com.ss.utopia.flights.controller;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.ss.utopia.flights.dto.airplane.AirplaneDto;
import com.ss.utopia.flights.dto.airport.CreateAirportDto;
import com.ss.utopia.flights.dto.airport.ServicingAreaDto;
import com.ss.utopia.flights.dto.airport.UpdateAirportDto;
import com.ss.utopia.flights.dto.flight.CreateFlightDto;
import com.ss.utopia.flights.dto.flight.UpdateFlightDto;
import com.ss.utopia.flights.entity.airplane.Airplane;
import com.ss.utopia.flights.entity.airplane.SeatConfiguration;
import com.ss.utopia.flights.entity.airport.Airport;
import com.ss.utopia.flights.entity.airport.ServicingArea;
import com.ss.utopia.flights.entity.flight.Flight;
import com.ss.utopia.flights.entity.flight.Seat;
import com.ss.utopia.flights.entity.flight.SeatStatus;
import com.ss.utopia.flights.entity.shared.SeatClass;
import com.ss.utopia.flights.security.SecurityConstants;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
public abstract class BaseSecurityTests {

  final Date expiresAt = Date.from(LocalDateTime.now().plusDays(1).toInstant(ZoneOffset.UTC));

  @Autowired
  WebApplicationContext wac;
  @MockBean
  SecurityConstants securityConstants;

  ServicingArea mockServicingArea = ServicingArea.builder()
      .id(1L)
      .areaName("somewhere")
      .build();

  Airport mockAirport = Airport.builder()
      .iataId("ATL")
      .name("Hartsfield–Jackson Atlanta International Airport")
      .streetAddress("123 airport street")
      .city("Atlanta")
      .state("GA")
      .zipcode("12345")
      .servicingArea(mockServicingArea)
      .build();

  CreateAirportDto mockCreateDto = CreateAirportDto.builder()
      .iataId(mockAirport.getIataId())
      .name(mockAirport.getName())
      .streetAddress(mockAirport.getStreetAddress())
      .city(mockAirport.getCity())
      .state(mockAirport.getState())
      .zipcode(mockAirport.getZipcode())
      .servicingAreaId(mockAirport.getServicingArea().getId())
      .build();
  UpdateAirportDto updateAirportDto = UpdateAirportDto.builder()
      .name(mockAirport.getName())
      .streetAddress(mockAirport.getStreetAddress())
      .city(mockAirport.getCity())
      .state(mockAirport.getState())
      .zipcode(mockAirport.getZipcode())
      .build();

  Airplane mockAirplane = Airplane.builder()
      .id(1L)
      .name("Jay-Jay the Jet")
      .seatConfigurations(List.of(SeatConfiguration.builder()
                                      .id(1L)
                                      .numRows(1)
                                      .numSeatsPerRow(1)
                                      .seatClass(SeatClass.FIRST_CLASS)
                                      .build()))
      .build();

  AirplaneDto mockAirplaneDto = AirplaneDto.builder()
      .name(mockAirplane.getName())
      .seatConfigurations(mockAirplane.getSeatConfigurations())
      .build();

  ServicingAreaDto mockServicingAreaDto = ServicingAreaDto.builder()
      .servicingArea(mockServicingArea.getAreaName())
      .build();


  Flight mockFlight = Flight.builder()
      .id(1L)
      .possibleLoyaltyPoints(5)
      .origin(mockAirport)
      .destination(mockAirport)
      .airplane(mockAirplane)
      .seats(List.of(Seat.builder()
                         .id("1-A")
                         .seatRow(1)
                         .seatColumn('A')
                         .seatClass(SeatClass.FIRST_CLASS)
                         .seatStatus(SeatStatus.AVAILABLE)
                         .price(BigDecimal.ONE)
                         .build()))
      .creationDateTime(ZonedDateTime.now())
      .approximateDateTimeStart(ZonedDateTime.now())
      .approximateDateTimeEnd(ZonedDateTime.now())
      .flightActive(true)
      .build();

  LocalDateTime departure = LocalDateTime.now().plusDays(1);
  LocalDateTime arrival = LocalDateTime.now().plusWeeks(1);

  //fixme This is not great. Need to implement serialization of LocalDateTime with JSON to be string to fix this.
  String mockFlightJson = "{\n"
      + "    \"originId\":\"ATL\",\n"
      + "    \"destinationId\":\"LAX\",\n"
      + "    \"airplaneId\":\"1\",\n"
      + "    \"approximateDateTimeStart\":\""+departure.toString()+"\",\n"
      + "    \"approximateDateTimeEnd\":\""+arrival.toString()+"\",\n"
      + "    \"baseSeatPrice\":\"12.0\",\n"
      + "    \"loyaltyPoints\":\"5\"\n"
      + "}";

  UpdateFlightDto mockUpdateFlightDto = UpdateFlightDto.builder()
      .originId(mockFlight.getOrigin().getIataId())
      .destinationId(mockFlight.getDestination().getIataId())
      .airplaneId(mockAirplane.getId())
      .build();

  MockMvc mvc;

  @BeforeEach
  void beforeEach() {
    mvc = MockMvcBuilders
        .webAppContextSetup(wac)
        .apply(springSecurity())
        .build();

    when(securityConstants.getEndpoint()).thenReturn("/authenticate");
    when(securityConstants.getJwtIssuer()).thenReturn("test-issuer");
    when(securityConstants.getExpiresAt()).thenReturn(expiresAt);
    when(securityConstants.getJwtSecret()).thenReturn("superSecret");
    when(securityConstants.getUserIdClaimKey()).thenReturn("userId");
    when(securityConstants.getAuthorityClaimKey()).thenReturn("Authorities");
    when(securityConstants.getJwtHeaderName()).thenReturn("Authorization");
    when(securityConstants.getJwtHeaderPrefix()).thenReturn("Bearer ");
  }

  String getJwt(MockUser mockUser) {
    var jwt = JWT.create()
        .withSubject(mockUser.email)
        .withIssuer(securityConstants.getJwtIssuer())
        .withClaim(securityConstants.getUserIdClaimKey(), mockUser.id)
        .withClaim(securityConstants.getAuthorityClaimKey(), List.of(mockUser.getAuthority()))
        .withExpiresAt(expiresAt)
        .sign(Algorithm.HMAC512(securityConstants.getJwtSecret()));
    return "Bearer " + jwt;
  }

  enum MockUser {
    DEFAULT("default@test.com", "ROLE_DEFAULT", UUID.randomUUID().toString()),
    CUSTOMER("eddy_grant@test.com", "ROLE_CUSTOMER", UUID.randomUUID().toString()),
    EMPLOYEE("employee@test.com", "ROLE_EMPLOYEE", UUID.randomUUID().toString()),
    TRAVEL_AGENT("travel_agent@test.com", "ROLE_TRAVEL_AGENT", UUID.randomUUID().toString()),
    ADMIN("admin@test.com", "ROLE_ADMIN", UUID.randomUUID().toString());

    final String email;
    final GrantedAuthority grantedAuthority;
    final String id;

    MockUser(String email, String grantedAuthority, String id) {
      this.email = email;
      this.grantedAuthority = new SimpleGrantedAuthority(grantedAuthority);
      this.id = id;
    }

    public String getAuthority() {
      return grantedAuthority.getAuthority();
    }
  }
}
