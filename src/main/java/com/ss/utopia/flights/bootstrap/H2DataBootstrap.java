package com.ss.utopia.flights.bootstrap;


import com.ss.utopia.flights.entity.airplane.Airplane;
import com.ss.utopia.flights.entity.airplane.SeatConfiguration;
import com.ss.utopia.flights.entity.airport.Airport;
import com.ss.utopia.flights.entity.airport.ServicingArea;
import com.ss.utopia.flights.entity.flight.Flight;
import com.ss.utopia.flights.entity.shared.SeatClass;
import com.ss.utopia.flights.repository.AirplaneRepository;
import com.ss.utopia.flights.repository.AirportRepository;
import com.ss.utopia.flights.repository.FlightRepository;
import com.ss.utopia.flights.repository.ServicingAreaRepository;
import com.ss.utopia.flights.service.FlightService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.*;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Component
@Slf4j
@Profile("local-h2")
@RequiredArgsConstructor
public class H2DataBootstrap implements CommandLineRunner {

    private final AirplaneRepository airplaneRepository;
    private final AirportRepository airportRepository;
    private final FlightRepository flightRepository;
    private final FlightService flightService;
    private final ServicingAreaRepository servicingAreaRepository;

    @Override
    public void run(String... args) throws Exception {
        log.info("Running in H2, adding new items");
        if (airplaneRepository.count() == 0 || airportRepository.count() == 0 || flightRepository.count() == 0 || servicingAreaRepository.count() == 0){
            log.info("Now, populating h2");
            loadAllData();
        }
        log.info("Finished populating of h2");
    }

    private void loadAllData(){
        loadServicingAreas();
        loadAirports();
        loadAirplanes();
        loadFlights();
    }

    private void loadFlights() {

        //Going to add static data at first to verify

        LocalDateTime departure = LocalDateTime.of(2021, 5, 17, 12, 10);
        LocalDateTime arrival = LocalDateTime.of(2021, 5, 17, 16,0);
        Flight firstFlight = Flight.builder()
                .possibleLoyaltyPoints(50)
                .flightActive(true)
                .origin(airportRepository.findById("IAD").get())
                .destination(airportRepository.findById("LAX").get())
                .airplane(airplaneRepository.findAll().get(0))
                .approximateDateTimeStart(ZonedDateTime.of(departure, ZoneId.of("EST", ZoneId.SHORT_IDS)))
                .approximateDateTimeEnd(ZonedDateTime.of(arrival, ZoneId.of("EST", ZoneId.SHORT_IDS)))
                .build();
        flightRepository.save(firstFlight);

        LocalDateTime departure1 = LocalDateTime.of(2021, 4, 15, 12, 20);
        LocalDateTime arrival1 = LocalDateTime.of(2021, 4, 15, 16,0);
        Flight secondFlight = Flight.builder()
                .possibleLoyaltyPoints(75)
                .flightActive(true)
                .origin(airportRepository.findById("LAX").get())
                .destination(airportRepository.findById("IAD").get())
                .airplane(airplaneRepository.findAll().get(0))
                .approximateDateTimeStart(ZonedDateTime.of(departure1, ZoneId.of("EST", ZoneId.SHORT_IDS)))
                .approximateDateTimeEnd(ZonedDateTime.of(arrival1, ZoneId.of("EST", ZoneId.SHORT_IDS)))
                .build();
        flightRepository.save(secondFlight);

        LocalDateTime departure2 = LocalDateTime.of(2021, 3, 3, 10, 15);
        LocalDateTime arrival2 = LocalDateTime.of(2021, 3, 3, 14,30);
        Flight thirdFlight = Flight.builder()
                .possibleLoyaltyPoints(15)
                .flightActive(true)
                .origin(airportRepository.findById("IAD").get())
                .destination(airportRepository.findById("JFK").get())
                .airplane(airplaneRepository.findAll().get(1))
                .approximateDateTimeStart(ZonedDateTime.of(departure2, ZoneId.of("EST", ZoneId.SHORT_IDS)))
                .approximateDateTimeEnd(ZonedDateTime.of(arrival2, ZoneId.of("EST", ZoneId.SHORT_IDS)))
                .build();
        flightRepository.save(thirdFlight);

        LocalDateTime departure3 = LocalDateTime.of(2021, 3, 4, 10, 15);
        LocalDateTime arrival3 = LocalDateTime.of(2021, 3, 4, 14,30);
        Flight fourthFlight = Flight.builder()
                .possibleLoyaltyPoints(25)
                .flightActive(true)
                .origin(airportRepository.findById("JFK").get())
                .destination(airportRepository.findById("LAX").get())
                .airplane(airplaneRepository.findAll().get(1))
                .approximateDateTimeStart(ZonedDateTime.of(departure3, ZoneId.of("EST", ZoneId.SHORT_IDS)))
                .approximateDateTimeEnd(ZonedDateTime.of(arrival3, ZoneId.of("EST", ZoneId.SHORT_IDS)))
                .build();
        flightRepository.save(fourthFlight);

        //Then adding random data

        var airplaneList = airplaneRepository.findAll();
        var airportList = airportRepository.findAll();

        var minDate = LocalDate.of(2021, 5, 1);
        var maxDate = LocalDate.of(2021, 7,31);
        var minTime = LocalTime.of(0,0);
        var maxTime = LocalTime.of(23,59);
        Random generator = new Random();
        for (int i = 0; i < 50; i++){
            Airplane randomAirplane = airplaneList.get(generator.nextInt(airplaneList.size()));
            Airport origin = airportList.get(generator.nextInt(airportList.size()));
            Airport destination = airportList.get(generator.nextInt(airportList.size()));
            while (origin.equals(destination)){
                destination = airportList.get(generator.nextInt(airportList.size()));
            }
            Integer randomPoints = generator.nextInt(100);
            var LocalDateDepart = between(minDate, maxDate);
            var LocalDateArrival = between(LocalDateDepart, LocalDateDepart.plusDays(1));
            var LocalTimeDepart = between(minTime, maxTime);
            var LocalTimeArrival = between(LocalTimeDepart, maxTime);
            Flight tempFlight = Flight.builder()
                    .possibleLoyaltyPoints(randomPoints)
                    .flightActive(true)
                    .origin(origin)
                    .destination(destination)
                    .airplane(randomAirplane)
                    .approximateDateTimeStart(ZonedDateTime.of(LocalDateDepart, LocalTimeDepart, ZoneId.of("EST", ZoneId.SHORT_IDS)))
                    .approximateDateTimeEnd(ZonedDateTime.of(LocalDateArrival, LocalTimeArrival, ZoneId.of("EST", ZoneId.SHORT_IDS)))
                    .build();
            flightRepository.save(tempFlight);
        }
    }

    private void loadAirplanes() {
        Airplane Boeing747 = Airplane.builder()
                .name("Boeing 747")
                .seatConfigurations(List.of(
                        SeatConfiguration.builder().numRows(12).numSeatsPerRow(9).seatClass(SeatClass.ECONOMY).build(),
                        SeatConfiguration.builder().numRows(6).numSeatsPerRow(5).seatClass(SeatClass.BUSINESS).build()
                        ))
                .build();
        airplaneRepository.save(Boeing747);
        Airplane Boeing777 = Airplane.builder()
                .name("Boeing 777")
                .seatConfigurations(List.of(
                        SeatConfiguration.builder().numRows(14).numSeatsPerRow(12).seatClass(SeatClass.ECONOMY).build(),
                        SeatConfiguration.builder().numRows(8).numSeatsPerRow(3).seatClass(SeatClass.BUSINESS).build()
                ))
                .build();
        airplaneRepository.save(Boeing777);
        Airplane Boeing737 = Airplane.builder()
                .name("Boeing 737")
                .seatConfigurations(List.of(
                        SeatConfiguration.builder().numRows(6).numSeatsPerRow(6).seatClass(SeatClass.ECONOMY).build(),
                        SeatConfiguration.builder().numRows(3).numSeatsPerRow(2).seatClass(SeatClass.BUSINESS).build()
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
                .servicingArea(servicingAreaRepository.findByServicingArea("D.C").get())
                .zipcode("20166")
                .build();
        airportRepository.save(Dulles);
        Airport Reagan = Airport.builder()
                .iataId("DCA")
                .name("Ronald Reagan Washington National Airport")
                .streetAddress("2401 Smith Blvd")
                .city("Arlington")
                .state("VA")
                .servicingArea(servicingAreaRepository.findByServicingArea("D.C").get())
                .zipcode("22202")
                .build();
        airportRepository.save(Reagan);
        Airport Baltimore = Airport.builder()
                .iataId("BWI")
                .name("Baltimore/Washington International Thurgood Marshall Airport")
                .streetAddress("Baltimore")
                .city("Baltimore")
                .state("MD")
                .servicingArea(servicingAreaRepository.findByServicingArea("D.C").get())
                .zipcode("21240")
                .build();
        airportRepository.save(Baltimore);
        Airport JFK = Airport.builder()
                .iataId("JFK")
                .name("John F. Kennedy International Airport")
                .streetAddress("Queens")
                .city("Queens")
                .state("NY")
                .servicingArea(servicingAreaRepository.findByServicingArea("NYC").get())
                .zipcode("11430")
                .build();
        airportRepository.save(JFK);
        Airport LAX = Airport.builder()
                .iataId("LAX")
                .name("Los Angeles International Airport")
                .streetAddress("1 World Way")
                .city("Los Angeles")
                .state("CA")
                .servicingArea(servicingAreaRepository.findByServicingArea("LA").get())
                .zipcode("90045")
                .build();
        airportRepository.save(LAX);
        Airport SanFran = Airport.builder()
                .iataId("SFO")
                .name("San Francisco International Airport")
                .streetAddress("San Francisco")
                .city("San Francisco")
                .state("CA")
                .servicingArea(servicingAreaRepository.findByServicingArea("SF").get())
                .zipcode("94128")
                .build();
        airportRepository.save(SanFran);
    }

    private void loadServicingAreas() {
        String[] servicingAreas = {"D.C", "NYC", "LA", "SF", "SEA", "ATL", "HOU", "NSH", "CHI"};
        for (String area: servicingAreas){
            var tempArea = ServicingArea.builder()
                    .servicingArea(area)
                    .build();
            servicingAreaRepository.save(tempArea);
        }
    }

    private static LocalDate between(LocalDate startInclusive, LocalDate endExclusive){
        long startEpochDay = startInclusive.toEpochDay();
        long endEpochDay = endExclusive.toEpochDay();
        long randomDay = ThreadLocalRandom.current()
                .nextLong(startEpochDay, endEpochDay);
        return LocalDate.ofEpochDay(randomDay);
    }

    public static LocalTime between(LocalTime startTime, LocalTime endTime) {
        int startSeconds = startTime.toSecondOfDay();
        int endSeconds = endTime.toSecondOfDay();
        int randomTime = ThreadLocalRandom
                .current()
                .nextInt(startSeconds, endSeconds);

        return LocalTime.ofSecondOfDay(randomTime);
    }
}
