package com.ss.utopia.flights.util;

import com.ss.utopia.flights.entity.airplane.Airplane;
import com.ss.utopia.flights.entity.airplane.SeatConfiguration;
import com.ss.utopia.flights.entity.airport.Airport;
import com.ss.utopia.flights.entity.airport.ServicingArea;
import com.ss.utopia.flights.entity.flight.Flight;
import com.ss.utopia.flights.entity.flight.Seat;
import com.ss.utopia.flights.entity.flight.SeatStatus;
import com.ss.utopia.flights.entity.shared.SeatClass;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class FindAllPathsTest{

    //Need to create servicingAreas

    final ServicingArea DCRegion = ServicingArea.builder()
            .id(1L)
            .areaName("D.C")
            .build();

    final ServicingArea LARegion = ServicingArea.builder()
            .id(2L)
            .areaName("LA")
            .build();

    final ServicingArea NYCRegion = ServicingArea.builder()
            .id(3L)
            .areaName("NYC")
            .build();

    //Need to create airports

    final Airport Dulles = Airport.builder()
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

    final Airport JFK = Airport.builder()
            .iataId("JFK")
            .name("John F Kennedy International airport")
            .streetAddress("Somewhere in New York")
            .city("Queens")
            .state("NY")
            .servicingArea(NYCRegion)
            .zipcode("11430")
            .build();


    //Need to create airplanes

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

    //Need to create fake flights

    BigDecimal tempPrice =  new BigDecimal(50);

    LocalDateTime departure = LocalDateTime.of(2030, 5, 17, 12, 10);
    LocalDateTime arrival = LocalDateTime.of(2030, 5, 17, 16, 0);

    LocalDateTime departureMultiHop = LocalDateTime.of(2030, 5, 17, 17, 10);
    LocalDateTime arrivalMultiHop = LocalDateTime.of(2030, 5, 17, 20, 0);

    Flight firstFlight = Flight.builder()
            .id(1L)
            .possibleLoyaltyPoints(50)
            .origin(Reagan)
            .destination(LAX)
            .airplane(Boeing747)
            .seats(List.of(
                    Seat.builder()
                            .id("FLIGHT 1-1A ECONOMY")
                            .seatRow(1)
                            .seatColumn('A')
                            .seatClass(SeatClass.ECONOMY)
                            .seatStatus(SeatStatus.AVAILABLE)
                            .price(tempPrice)
                            .build(),
                    Seat.builder()
                            .id("FLIGHT 1-1A BUSINESS")
                            .seatRow(1)
                            .seatColumn('A')
                            .seatClass(SeatClass.BUSINESS)
                            .seatStatus(SeatStatus.AVAILABLE)
                            .price(tempPrice)
                            .build()
            ))
            .creationDateTime(ZonedDateTime.now())
            .approximateDateTimeStart(departure.atZone(ZoneId.of("UTC")))
            .approximateDateTimeEnd(arrival.atZone(ZoneId.of("UTC")))
            .flightActive(true)
            .build();

    Flight secondFlight = Flight.builder()
            .id(2L)
            .possibleLoyaltyPoints(50)
            .origin(Dulles)
            .destination(LAX)
            .airplane(Boeing777)
            .seats(List.of(
                    Seat.builder()
                            .id("FLIGHT 1-1A ECONOMY")
                            .seatRow(1)
                            .seatColumn('A')
                            .seatClass(SeatClass.ECONOMY)
                            .seatStatus(SeatStatus.AVAILABLE)
                            .price(tempPrice)
                            .build(),
                    Seat.builder()
                            .id("FLIGHT 1-1A BUSINESS")
                            .seatRow(1)
                            .seatColumn('A')
                            .seatClass(SeatClass.BUSINESS)
                            .seatStatus(SeatStatus.AVAILABLE)
                            .price(tempPrice)
                            .build()
            ))
            .creationDateTime(ZonedDateTime.now())
            .approximateDateTimeStart(departure.atZone(ZoneId.of("UTC")))
            .approximateDateTimeEnd(arrival.atZone(ZoneId.of("UTC")))
            .flightActive(true)
            .build();

    Flight thirdFlight = Flight.builder()
            .id(3L)
            .possibleLoyaltyPoints(50)
            .origin(Dulles)
            .destination(JFK)
            .airplane(Boeing777)
            .seats(List.of(
                    Seat.builder()
                            .id("FLIGHT 1-1A ECONOMY")
                            .seatRow(1)
                            .seatColumn('A')
                            .seatClass(SeatClass.ECONOMY)
                            .seatStatus(SeatStatus.AVAILABLE)
                            .price(tempPrice)
                            .build(),
                    Seat.builder()
                            .id("FLIGHT 1-1A BUSINESS")
                            .seatRow(1)
                            .seatColumn('A')
                            .seatClass(SeatClass.BUSINESS)
                            .seatStatus(SeatStatus.AVAILABLE)
                            .price(tempPrice)
                            .build()
            ))
            .creationDateTime(ZonedDateTime.now())
            .approximateDateTimeStart(departure.atZone(ZoneId.of("UTC")))
            .approximateDateTimeEnd(arrival.atZone(ZoneId.of("UTC")))
            .flightActive(true)
            .build();

    Flight fourthFlight = Flight.builder()
            .id(4L)
            .possibleLoyaltyPoints(50)
            .origin(JFK)
            .destination(LAX)
            .airplane(Boeing747)
            .seats(List.of(
                    Seat.builder()
                            .id("FLIGHT 1-1A ECONOMY")
                            .seatRow(1)
                            .seatColumn('A')
                            .seatClass(SeatClass.ECONOMY)
                            .seatStatus(SeatStatus.AVAILABLE)
                            .price(tempPrice)
                            .build(),
                    Seat.builder()
                            .id("FLIGHT 1-1A BUSINESS")
                            .seatRow(1)
                            .seatColumn('A')
                            .seatClass(SeatClass.BUSINESS)
                            .seatStatus(SeatStatus.AVAILABLE)
                            .price(tempPrice)
                            .build()
            ))
            .creationDateTime(ZonedDateTime.now())
            .approximateDateTimeStart(departureMultiHop.atZone(ZoneId.of("UTC")))
            .approximateDateTimeEnd(arrivalMultiHop.atZone(ZoneId.of("UTC")))
            .flightActive(true)
            .build();

    List<Airport> origin = List.of(Dulles, Reagan);
    List<Airport> destination = List.of(LAX);
    List<Flight> availableFlights = List.of(firstFlight, secondFlight, thirdFlight, fourthFlight);
    Integer passengerCount = 1;
    LocalDate departureDate = LocalDate.of(2030, 5, 17);


    //It can do it in different order so we need to test for that
    @Test
    public void test_returnAllValidFlights_withNonStopAndMultiHop(){
        FindAllPaths test = new FindAllPaths(origin, destination, availableFlights, passengerCount, departureDate);
        test.getAllPaths();
        var result = test.returnAllValidFlights();
        var expectedResult = List.of(List.of(firstFlight), List.of(secondFlight), List.of(thirdFlight, fourthFlight));
        assertTrue(result.size() == expectedResult.size() && result.containsAll(expectedResult) && expectedResult.containsAll(result));
    }

    @Test
    public void test_returnAllValidFlights_withOnlyOneAirport(){
        List<Airport> newOrigin = List.of(Dulles);
        FindAllPaths test = new FindAllPaths(newOrigin, destination, availableFlights, passengerCount, departureDate);
        test.getAllPaths();
        var result = test.returnAllValidFlights();
        var expectedResult = List.of(List.of(secondFlight), List.of(thirdFlight, fourthFlight));
        assertTrue(result.size() == expectedResult.size() && result.containsAll(expectedResult) && expectedResult.containsAll(result));
    }

    @Test
    public void test_returnAllValidFlights_withOnlyOneFlightInFlightList(){
        List<Airport> newOrigin = List.of(JFK);
        FindAllPaths test = new FindAllPaths(newOrigin, destination, availableFlights, passengerCount, departureDate);
        test.getAllPaths();
        var result = test.returnAllValidFlights();
        var expectedResult = List.of(List.of(fourthFlight));
        assertTrue(result.size() == expectedResult.size() && result.containsAll(expectedResult) && expectedResult.containsAll(result));
    }

    @Test
    public void test_returnAllValidFlights_returnAnEmptyListBecausePassengerCountIsGreaterThanAnyFlight(){
        FindAllPaths test = new FindAllPaths(origin, destination, availableFlights, 10, departureDate);
        test.getAllPaths();
        var result = test.returnAllValidFlights();
        assertEquals(0, result.size());
    }

    @Test
    public void test_returnAllValidFlights_returnEmptyListBecauseDepartureDateIsAfterAnyFlight(){
        LocalDate newDepartureDate = LocalDate.of(2030, 5, 20);
        FindAllPaths test = new FindAllPaths(origin, destination, availableFlights, passengerCount, newDepartureDate);
        test.getAllPaths();
        var result = test.returnAllValidFlights();
        assertEquals(0, result.size());
    }

    @Test
    public void test_returnAllValidFlights_returnEmptyListBecauseDepartureDateIsBeforeAnyFlight(){
        LocalDate newDepartureDate = LocalDate.of(2020, 5, 17);
        FindAllPaths test = new FindAllPaths(origin, destination, availableFlights, 10, newDepartureDate);
        test.getAllPaths();
        var result = test.returnAllValidFlights();
        assertEquals(0, result.size());
    }
}

