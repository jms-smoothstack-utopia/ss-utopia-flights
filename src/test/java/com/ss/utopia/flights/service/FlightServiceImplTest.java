package com.ss.utopia.flights.service;

import com.ss.utopia.flights.dto.flight.CreateFlightDto;
import com.ss.utopia.flights.dto.flight.FlightSearchDto;
import com.ss.utopia.flights.entity.airplane.Airplane;
import com.ss.utopia.flights.entity.airplane.SeatConfiguration;
import com.ss.utopia.flights.entity.airport.Airport;
import com.ss.utopia.flights.entity.airport.ServicingArea;
import com.ss.utopia.flights.entity.flight.Flight;
import com.ss.utopia.flights.entity.shared.SeatClass;
import com.ss.utopia.flights.exception.NoSuchAirportException;
import com.ss.utopia.flights.repository.AirportRepository;
import com.ss.utopia.flights.repository.FlightRepository;
import com.ss.utopia.flights.repository.SeatRepository;
import com.ss.utopia.flights.repository.ServicingAreaRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@SpringBootTest
public class FlightServiceImplTest {

    ServicingAreaRepository servicingAreaRepository = Mockito.mock(ServicingAreaRepository.class);

    AirportRepository airportRepository = Mockito.mock(AirportRepository.class);

    SeatRepository seatRepository = Mockito.mock(SeatRepository.class);

    AirportService airportService = Mockito.mock(AirportService.class);

    AirplaneService airplaneService = Mockito.mock(AirplaneService.class);

    FlightRepository flightRepository = Mockito.mock(FlightRepository.class);

    FlightService flightService = new FlightServiceImpl(
            flightRepository,
            airportService,
            airplaneService,
            servicingAreaRepository,
            airportRepository,
            seatRepository);

    final ServicingArea DCRegion = ServicingArea.builder()
            .id(1L)
            .areaName("D.C")
            .build();

    final ServicingArea LARegion = ServicingArea.builder()
            .id(2L)
            .areaName("LA")
            .build();

    final Airport DullesAirport = Airport.builder()
            .iataId("IAD")
            .name("Dulles International Airport")
            .streetAddress("1 Saarinen Cir")
            .city("Dulles")
            .state("VA")
            .servicingArea(DCRegion)
            .zipcode("20166")
            .build();

    final Airport Reagan = Airport.builder()
            .iataId("DCA")
            .name("Ronald Reagan Washington National Airport")
            .streetAddress("2401 Smith Blvd")
            .city("Arlington")
            .state("VA")
            .servicingArea(DCRegion)
            .zipcode("22202")
            .build();

    final Airport LAX = Airport.builder()
            .iataId("LAX")
            .name("Los Angeles International Airport")
            .streetAddress("1 World Way")
            .city("Los Angeles")
            .state("CA")
            .servicingArea(LARegion)
            .zipcode("90045")
            .build();

    final Airplane Boeing747 = Airplane.builder()
            .id(1L)
            .name("Boeing 747")
            .seatConfigurations(List.of(
                    SeatConfiguration.builder().numRows(12).numSeatsPerRow(9).seatClass(SeatClass.ECONOMY).build(),
                    SeatConfiguration.builder().numRows(6).numSeatsPerRow(5).seatClass(SeatClass.BUSINESS).build()
            ))
            .build();

    final Airplane Boeing777 = Airplane.builder().name("Boeing 737")
            .id(2L)
            .seatConfigurations(List.of(
                    SeatConfiguration.builder().numRows(6).numSeatsPerRow(6).seatClass(SeatClass.ECONOMY).build(),
                    SeatConfiguration.builder().numRows(3).numSeatsPerRow(2).seatClass(SeatClass.BUSINESS).build()
            ))
            .build();

    @Test
    void test_getAllFlights_returnList(){
        LocalDateTime departure = LocalDateTime.of(2030, 5, 17, 12, 10);
        LocalDateTime arrival = LocalDateTime.of(2030, 5, 17, 16,0);

        var dto = CreateFlightDto.builder()
                .originId("IAD")
                .destinationId("LAX")
                .airplaneId(1L)
                .approximateDateTimeStart(departure)
                .approximateDateTimeEnd(arrival)
                .loyaltyPoints(50)
                .build();

        var testFlight = Flight.builder()
                .id(1L)
                .origin(DullesAirport)
                .destination(LAX)
                .airplane(Boeing747)
                .possibleLoyaltyPoints(50)
                .approximateDateTimeStart(dto.getApproximateDateTimeStart().atZone(ZoneId.of("UTC")))
                .approximateDateTimeEnd(dto.getApproximateDateTimeEnd().atZone(ZoneId.of("UTC")))
                .build();
        when(flightRepository.findAll()).thenReturn(List.of(testFlight));

        var result = flightService.getAllFlights();

        assertEquals(List.of(testFlight), result);
    }

    @Test
    void test_createNewFlight_returnsNewlyCreatedFlightBack(){

        LocalDateTime departure = LocalDateTime.of(2030, 5, 17, 12, 10);
        LocalDateTime arrival = LocalDateTime.of(2030, 5, 17, 16,0);

        var dto = CreateFlightDto.builder()
                .originId("IAD")
                .destinationId("LAX")
                .airplaneId(1L)
                .approximateDateTimeStart(departure)
                .approximateDateTimeEnd(arrival)
                .loyaltyPoints(50)
                .build();

        var testFlight = Flight.builder()
                .id(1L)
                .origin(DullesAirport)
                .destination(LAX)
                .airplane(Boeing747)
                .possibleLoyaltyPoints(50)
                .approximateDateTimeStart(dto.getApproximateDateTimeStart().atZone(ZoneId.of("UTC")))
                .approximateDateTimeEnd(dto.getApproximateDateTimeEnd().atZone(ZoneId.of("UTC")))
                .build();

        when(airportService.getAirportById("IAD")).thenReturn(DullesAirport);
        when(airportService.getAirportById("LAX")).thenReturn(LAX);
        when(airplaneService.getAirplaneById(1L)).thenReturn(Boeing747);
        when(flightRepository.save(any(Flight.class))).thenReturn(testFlight);

        var result = flightService.createNewFlight(dto);

        //Check if the id is good
        assertEquals(testFlight.getId(),result.getId());

        //Check if seats is not empty
        assertNotNull(result.getSeats());
    }

    @Test
    void test_getFlightSeats_ReturnsTheSeatsOfTheFlightsToTheUser(){
        LocalDateTime departure = LocalDateTime.of(2030, 5, 17, 12, 10);
        LocalDateTime arrival = LocalDateTime.of(2030, 5, 17, 16,0);

        var dto = CreateFlightDto.builder()
                .originId("IAD")
                .destinationId("LAX")
                .airplaneId(1L)
                .approximateDateTimeStart(departure)
                .approximateDateTimeEnd(arrival)
                .loyaltyPoints(50)
                .build();

        var testFlight = Flight.builder()
                .id(1L)
                .origin(DullesAirport)
                .destination(LAX)
                .airplane(Boeing747)
                .possibleLoyaltyPoints(50)
                .approximateDateTimeStart(dto.getApproximateDateTimeStart().atZone(ZoneId.of("UTC")))
                .approximateDateTimeEnd(dto.getApproximateDateTimeEnd().atZone(ZoneId.of("UTC")))
                .build();

        when(airportService.getAirportById("IAD")).thenReturn(DullesAirport);
        when(airportService.getAirportById("LAX")).thenReturn(LAX);
        when(airplaneService.getAirplaneById(1L)).thenReturn(Boeing747);
        when(flightRepository.save(any(Flight.class))).thenReturn(testFlight);

        var result = flightService.createNewFlight(dto);
        when(flightRepository.findById(any(Long.class))).thenReturn(java.util.Optional.ofNullable(result));

        var seatsResult = flightService.getFlightSeats(testFlight.getId());
        assert result != null;
        assertEquals(result.getSeats(), seatsResult);
    }

    @Test
    void test_getFlightByCriteria_CouldNotFindCertainStringOfAirportOrServicingArea(){

        LocalDateTime departure = LocalDateTime.of(2030, 5, 17, 12, 10);
        LocalDateTime arrival = LocalDateTime.of(2030, 5, 17, 16,0);

        var testFlight = Flight.builder()
                .id(1L)
                .origin(DullesAirport)
                .destination(LAX)
                .airplane(Boeing747)
                .possibleLoyaltyPoints(50)
                .approximateDateTimeStart(ZonedDateTime.of(departure, ZoneId.of("EST", ZoneId.SHORT_IDS)))
                .approximateDateTimeEnd(ZonedDateTime.of(arrival, ZoneId.of("EST", ZoneId.SHORT_IDS)))
                .build();

        String[] doesNotExistOrigins = {"DoesNotExist"};
        String[] doesNotExistDestinations = {"DoesNotExist"};
        var dto = FlightSearchDto.builder()
                .origins(doesNotExistOrigins)
                .destinations(doesNotExistDestinations)
                .departureDate(LocalDate.now())
                .build();

        when(flightService.getAllFlights()).thenReturn(List.of(testFlight));
        when(servicingAreaRepository.findByAreaName(any(String.class))).thenReturn(Optional.empty());
        when(airportRepository.findById(any(String.class))).thenReturn(Optional.empty());

        assertThrows(NoSuchAirportException.class, () -> flightService.getFlightByCriteria(dto));
    }

    @Test
    void test_getFlightByCriteria_NoAvailableFlights(){

        LocalDateTime departure = LocalDateTime.of(2030, 5, 17, 12, 10);
        LocalDateTime arrival = LocalDateTime.of(2030, 5, 17, 16,0);

        var testFlight = Flight.builder()
                .id(1L)
                .origin(DullesAirport)
                .destination(LAX)
                .airplane(Boeing747)
                .possibleLoyaltyPoints(50)
                .approximateDateTimeStart(ZonedDateTime.of(departure, ZoneId.of("EST", ZoneId.SHORT_IDS)))
                .approximateDateTimeEnd(ZonedDateTime.of(arrival, ZoneId.of("EST", ZoneId.SHORT_IDS)))
                .build();

        String[] doesNotExistOrigins = {"DoesNotExist"};
        String[] doesNotExistDestinations = {"DoesNotExist"};
        var dto = FlightSearchDto.builder()
                .origins(doesNotExistOrigins)
                .destinations(doesNotExistDestinations)
                .departureDate(LocalDate.now())
                .build();

        when(flightService.getAllFlights()).thenReturn(Collections.emptyList());

        var result = flightService.getFlightByCriteria(dto);
        assertNull(result);
    }
}
