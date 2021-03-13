package com.ss.utopia.flights.bootstrap;


import com.ss.utopia.flights.dto.flight.CreateFlightDto;
import com.ss.utopia.flights.entity.airplane.Airplane;
import com.ss.utopia.flights.entity.airplane.SeatConfiguration;
import com.ss.utopia.flights.entity.airport.Airport;
import com.ss.utopia.flights.entity.airport.ServicingArea;
import com.ss.utopia.flights.entity.shared.SeatClass;
import com.ss.utopia.flights.repository.AirplaneRepository;
import com.ss.utopia.flights.repository.AirportRepository;
import com.ss.utopia.flights.repository.FlightRepository;
import com.ss.utopia.flights.repository.ServicingAreaRepository;
import com.ss.utopia.flights.service.FlightService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Profile({"local-h2", "integration-test"})
@RequiredArgsConstructor
public class H2DataBootstrap implements CommandLineRunner {

  private final AirplaneRepository airplaneRepository;
  private final AirportRepository airportRepository;
  private final FlightRepository flightRepository;
  private final FlightService flightService;
  private final ServicingAreaRepository servicingAreaRepository;

  @Override
  public void run(String... args) {
    log.info("Running in H2, adding new items");
    if (airplaneRepository.count() == 0 || airportRepository.count() == 0
        || flightRepository.count() == 0 || servicingAreaRepository.count() == 0) {
      log.info("Now, populating h2");
      loadAllData();
    }
    log.info("Finished populating of h2");
  }

  private void loadAllData() {
    loadServicingAreas();
    loadAirports();
    loadAirplanes();
    loadFlights();
  }

  private void loadFlights() {
    //Going to add static data at first to verify

    LocalDateTime departure = LocalDateTime.of(2021, 5, 17, 12, 10);
    LocalDateTime arrival = LocalDateTime.of(2021, 5, 17, 16, 0);

    CreateFlightDto firstFlightDto = CreateFlightDto.builder()
        .originId("IAD")
        .destinationId("LAX")
        .airplaneId(1L)
        .approximateDateTimeStart(departure)
        .approximateDateTimeEnd(arrival)
        .build();
    flightService.createNewFlight(firstFlightDto);

    LocalDateTime anotherArrival = LocalDateTime.of(2021, 5, 17, 12, 20);
    LocalDateTime anotherDeparture = LocalDateTime.of(2021, 5, 17, 16, 0);
    CreateFlightDto anotherFlight = CreateFlightDto.builder()
        .originId("DCA")
        .destinationId("LAX")
        .airplaneId(1L)
        .approximateDateTimeStart(anotherArrival)
        .approximateDateTimeEnd(anotherDeparture)
        .build();
    flightService.createNewFlight(anotherFlight);

    LocalDateTime multiHopJFKDeparture = LocalDateTime.of(2021, 5, 17, 17, 20);
    LocalDateTime multiHopJFKArrival = LocalDateTime.of(2021, 5, 17, 21, 0);
    CreateFlightDto multihopJFKFlight = CreateFlightDto.builder()
        .originId("DCA")
        .destinationId("JFK")
        .airplaneId(1L)
        .approximateDateTimeStart(multiHopJFKDeparture)
        .approximateDateTimeEnd(multiHopJFKArrival)
        .build();
    flightService.createNewFlight(multihopJFKFlight);

    LocalDateTime multiHopSFODeparture = LocalDateTime.of(2021, 5, 17, 17, 20);
    LocalDateTime multiHopSFOArrival = LocalDateTime.of(2021, 5, 17, 21, 0);
    CreateFlightDto multihopSFOFlight = CreateFlightDto.builder()
        .originId("DCA")
        .destinationId("SFO")
        .airplaneId(1L)
        .approximateDateTimeStart(multiHopSFODeparture)
        .approximateDateTimeEnd(multiHopSFOArrival)
        .build();
    flightService.createNewFlight(multihopSFOFlight);

    LocalDateTime multiHopSFOLAXDeparture = LocalDateTime.of(2021, 5, 17, 22, 20);
    LocalDateTime multiHopSFOLAXArrival = LocalDateTime.of(2021, 5, 18, 2, 0);
    CreateFlightDto multihopSFOLAXFlight = CreateFlightDto.builder()
        .originId("SFO")
        .destinationId("LAX")
        .airplaneId(1L)
        .approximateDateTimeStart(multiHopSFOLAXDeparture)
        .approximateDateTimeEnd(multiHopSFOLAXArrival)
        .build();
    flightService.createNewFlight(multihopSFOLAXFlight);

    LocalDateTime multiHopLAXDeparture = LocalDateTime.of(2021, 5, 17, 22, 20);
    LocalDateTime multiHopLAXArrival = LocalDateTime.of(2021, 5, 18, 2, 0);
    CreateFlightDto multihopLAXFlight = CreateFlightDto.builder()
        .originId("JFK")
        .destinationId("LAX")
        .airplaneId(1L)
        .approximateDateTimeStart(multiHopLAXDeparture)
        .approximateDateTimeEnd(multiHopLAXArrival)
        .build();
    flightService.createNewFlight(multihopLAXFlight);

    LocalDateTime multiHopJFKSFODeparture = LocalDateTime.of(2021, 5, 17, 22, 20);
    LocalDateTime multiHopJFKSFOArrival = LocalDateTime.of(2021, 5, 18, 2, 0);
    CreateFlightDto multihopKFJSFOFlight = CreateFlightDto.builder()
        .originId("JFK")
        .destinationId("SFO")
        .airplaneId(1L)
        .approximateDateTimeStart(multiHopJFKSFODeparture)
        .approximateDateTimeEnd(multiHopJFKSFOArrival)
        .build();
    flightService.createNewFlight(multihopKFJSFOFlight);

    LocalDateTime multiHopSFOLAXDeparture1 = LocalDateTime.of(2021, 5, 18, 5, 20);
    LocalDateTime multiHopSFOLAXArrival1 = LocalDateTime.of(2021, 5, 18, 7, 0);
    CreateFlightDto multihopSFOLAXFlight1 = CreateFlightDto.builder()
        .originId("SFO")
        .destinationId("LAX")
        .airplaneId(1L)
        .approximateDateTimeStart(multiHopSFOLAXDeparture1)
        .approximateDateTimeEnd(multiHopSFOLAXArrival1)
        .build();
    flightService.createNewFlight(multihopSFOLAXFlight1);

    LocalDateTime departure1 = LocalDateTime.of(2021, 4, 15, 12, 20);
    LocalDateTime arrival1 = LocalDateTime.of(2021, 4, 15, 16, 0);
    CreateFlightDto secondFlight = CreateFlightDto.builder()
        .originId("LAX")
        .destinationId("IAD")
        .airplaneId(1L)
        .approximateDateTimeStart(departure1)
        .approximateDateTimeEnd(arrival1)
        .build();
    flightService.createNewFlight(secondFlight);

    LocalDateTime departure2 = LocalDateTime.of(2021, 4, 3, 10, 15);
    LocalDateTime arrival2 = LocalDateTime.of(2021, 4, 3, 14, 30);
    CreateFlightDto thirdFlight = CreateFlightDto.builder()
        .originId("IAD")
        .destinationId("JFK")
        .airplaneId(1L)
        .approximateDateTimeStart(departure2)
        .approximateDateTimeEnd(arrival2)
        .build();
    flightService.createNewFlight(thirdFlight);

    LocalDateTime departure3 = LocalDateTime.of(2021, 3, 4, 10, 15);
    LocalDateTime arrival3 = LocalDateTime.of(2021, 3, 4, 14, 30);
    CreateFlightDto fourthFlight = CreateFlightDto.builder()
        .originId("JFK")
        .destinationId("LAX")
        .airplaneId(1L)
        .approximateDateTimeStart(departure3)
        .approximateDateTimeEnd(arrival3)
        .build();
    flightService.createNewFlight(fourthFlight);

    LocalDateTime departure4 = LocalDateTime.of(2021, 5, 28, 10, 15);
    LocalDateTime arrival4 = LocalDateTime.of(2021, 5, 28, 14, 30);
    CreateFlightDto fifthFlight = CreateFlightDto.builder()
        .originId("LAX")
        .destinationId("IAD")
        .airplaneId(1L)
        .approximateDateTimeStart(departure4)
        .approximateDateTimeEnd(arrival4)
        .build();
    flightService.createNewFlight(fifthFlight);
  }

  private void loadAirplanes() {
    Airplane Boeing747 = Airplane.builder()
        .name("Boeing 747")
        .seatConfigurations(List.of(
            SeatConfiguration.builder()
                .numRows(12)
                .numSeatsPerRow(9)
                .seatClass(SeatClass.ECONOMY)
                .build(),
            SeatConfiguration.builder()
                .numRows(6)
                .numSeatsPerRow(5)
                .seatClass(SeatClass.BUSINESS)
                .build()
        ))
        .build();
    airplaneRepository.save(Boeing747);
    Airplane Boeing777 = Airplane.builder()
        .name("Boeing 777")
        .seatConfigurations(List.of(
            SeatConfiguration.builder()
                .numRows(14)
                .numSeatsPerRow(12)
                .seatClass(SeatClass.ECONOMY)
                .build(),
            SeatConfiguration.builder()
                .numRows(8)
                .numSeatsPerRow(3)
                .seatClass(SeatClass.BUSINESS)
                .build()
        ))
        .build();
    airplaneRepository.save(Boeing777);
    Airplane Boeing737 = Airplane.builder()
        .name("Boeing 737")
        .seatConfigurations(List.of(
            SeatConfiguration.builder()
                .numRows(6)
                .numSeatsPerRow(6)
                .seatClass(SeatClass.ECONOMY)
                .build(),
            SeatConfiguration.builder()
                .numRows(3)
                .numSeatsPerRow(2)
                .seatClass(SeatClass.BUSINESS)
                .build()
        ))
        .build();
    airplaneRepository.save(Boeing737);
  }

  private void loadAirports() {

    Airport Dulles = Airport.builder()
        .iataId("IAD")
        .name("Dulles International Airport")
        .streetAddress("1 Saarinen Cir")
        .city("Dulles")
        .state("VA")
        .servicingArea(servicingAreaRepository.findByServicingArea("D.C").orElseThrow())
        .zipcode("20166")
        .build();
    airportRepository.save(Dulles);
    Airport Reagan = Airport.builder()
        .iataId("DCA")
        .name("Ronald Reagan Washington National Airport")
        .streetAddress("2401 Smith Blvd")
        .city("Arlington")
        .state("VA")
        .servicingArea(servicingAreaRepository.findByServicingArea("D.C").orElseThrow())
        .zipcode("22202")
        .build();
    airportRepository.save(Reagan);
    Airport Baltimore = Airport.builder()
        .iataId("BWI")
        .name("Baltimore/Washington International Thurgood Marshall Airport")
        .streetAddress("Baltimore")
        .city("Baltimore")
        .state("MD")
        .servicingArea(servicingAreaRepository.findByServicingArea("D.C").orElseThrow())
        .zipcode("21240")
        .build();
    airportRepository.save(Baltimore);
    Airport JFK = Airport.builder()
        .iataId("JFK")
        .name("John F. Kennedy International Airport")
        .streetAddress("Queens")
        .city("Queens")
        .state("NY")
        .servicingArea(servicingAreaRepository.findByServicingArea("NYC").orElseThrow())
        .zipcode("11430")
        .build();
    airportRepository.save(JFK);
    Airport LAX = Airport.builder()
        .iataId("LAX")
        .name("Los Angeles International Airport")
        .streetAddress("1 World Way")
        .city("Los Angeles")
        .state("CA")
        .servicingArea(servicingAreaRepository.findByServicingArea("LA").orElseThrow())
        .zipcode("90045")
        .build();
    airportRepository.save(LAX);
    Airport SanFran = Airport.builder()
        .iataId("SFO")
        .name("San Francisco International Airport")
        .streetAddress("San Francisco")
        .city("San Francisco")
        .state("CA")
        .servicingArea(servicingAreaRepository.findByServicingArea("SF").orElseThrow())
        .zipcode("94128")
        .build();
    airportRepository.save(SanFran);
  }

  private void loadServicingAreas() {
    String[] servicingAreas = {"D.C", "NYC", "LA", "SF", "SEA", "ATL", "HOU", "NSH", "CHI"};
    for (String area : servicingAreas) {
      var tempArea = ServicingArea.builder()
          .servicingArea(area)
          .build();
      servicingAreaRepository.save(tempArea);
    }
  }
}
