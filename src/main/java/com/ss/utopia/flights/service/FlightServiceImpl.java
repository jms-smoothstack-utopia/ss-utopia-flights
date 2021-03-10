package com.ss.utopia.flights.service;

import com.ss.utopia.flights.dto.flight.CreateFlightDto;
import com.ss.utopia.flights.dto.flight.FlightSearchDto;
import com.ss.utopia.flights.dto.flight.UpdateFlightDto;
import com.ss.utopia.flights.dto.flight.UpdateSeatDto;
import com.ss.utopia.flights.entity.airport.Airport;
import com.ss.utopia.flights.entity.flight.Flight;
import com.ss.utopia.flights.entity.flight.Seat;
import com.ss.utopia.flights.entity.flight.SeatStatus;
import com.ss.utopia.flights.exception.NoSuchAirportException;
import com.ss.utopia.flights.exception.NoSuchFlightException;
import com.ss.utopia.flights.repository.AirportRepository;
import com.ss.utopia.flights.repository.FlightRepository;
import com.ss.utopia.flights.repository.SeatRepository;
import com.ss.utopia.flights.repository.ServicingAreaRepository;
import com.ss.utopia.flights.util.FindAllPaths;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FlightServiceImpl implements FlightService {

  private final FlightRepository repository;
  private final AirportService airportService;
  private final AirplaneService airplaneService;
  private final ServicingAreaRepository servicingAreaRepository;
  private final AirportRepository airportRepository;
  private final SeatRepository seatRepository;

  @Override
  public List<Flight> getAllFlights() {
    return repository.findAll();
  }

  private List<Flight> getAllActiveFlights() {
    ZonedDateTime machineTime = ZonedDateTime.now();
    return repository.findAll().stream().parallel()
        .filter(flight -> flight.getApproximateDateTimeStart().isAfter(machineTime) &&
            flight.isFlightActive())
        .collect(Collectors.toList());
  }

  @Override
  public Flight getFlightById(Long id) {
    return repository.findById(id)
        .orElseThrow(() -> new NoSuchFlightException(id));
  }

  @Override
  public Flight createNewFlight(CreateFlightDto createFlightDto) {
    var origin = airportService.getAirportById(createFlightDto.getOriginId());
    var destination = airportService.getAirportById(createFlightDto.getDestinationId());
    var airplane = airplaneService.getAirplaneById(createFlightDto.getAirplaneId());

    Integer loyaltyPoints;
    if (createFlightDto.getLoyaltyPoints() == null) {
      loyaltyPoints = 50;
    } else {
      loyaltyPoints = createFlightDto.getLoyaltyPoints();
    }

    BigDecimal basePrice;
    if (createFlightDto.getBaseSeatPrice() == null) {
      basePrice = BigDecimal.valueOf(50.00);
    } else {
      basePrice = createFlightDto.getBaseSeatPrice();
    }

    var flight = Flight.builder()
        .origin(origin)
        .destination(destination)
        .airplane(airplane)
        .possibleLoyaltyPoints(loyaltyPoints)
        .flightActive(true)
        .approximateDateTimeStart(createFlightDto.getApproximateDateTimeStart().atZone(ZoneId.of("UTC")))
        .approximateDateTimeEnd(createFlightDto.getApproximateDateTimeEnd().atZone(ZoneId.of("UTC")))
        .build();

    flight = repository.save(flight);

    // add seats to the flight
    var flightId = flight.getId();
    var seats = new ArrayList<Seat>();

    airplane.getSeatConfigurations()
        .forEach(config -> {
          for (int row = 0; row < config.getNumRows(); row++) {
            for (int col = 0; col < config.getNumSeatsPerRow(); col++) {
              char baseLetter = 'A';
              char CorrectLetter = (char) (baseLetter + col);
              var seat = Seat.builder()
                  .id(String.format("FLIGHT %d-%d%c %s",
                                    flightId,
                                    row + 1,
                                    CorrectLetter,
                                    config.getSeatClass()))
                  .seatRow(row + 1)
                  .seatColumn(CorrectLetter)
                  .seatClass(config.getSeatClass())
                  .seatStatus(SeatStatus.AVAILABLE)
                  .price(basePrice)
                  .build();
              seatRepository.save(seat);
              seats.add(seat);
            }
          }
        });

    flight.setSeats(seats);
    return repository.save(flight);
  }

  @Override
  public void updateFlight(Long id, UpdateFlightDto updateFlightDto) {
    //todo this is a complicated use case
    throw new IllegalStateException("NOT IMPLEMENTED");
  /*
    var toUpdate = getFlightById(id);
    updateFlightDto.update(toUpdate);
    repository.save(toUpdate);
  */
  }

  @Override
  public void deleteFlight(Long id) {
    repository.findById(id)
        .ifPresent(repository::delete);
  }

  public List<Seat> getFlightSeats(Long flightId) {
    var flight = getFlightById(flightId);
    return flight.getSeats();
  }

  public List<Seat> getAvailableSeats(Long flightId) {
    return getFlightSeats(flightId).stream().parallel()
        .filter(seats -> seats.getSeatStatus() == SeatStatus.AVAILABLE)
        .collect(Collectors.toList());
  }

  @Override
  public void updateFlightSeats(Long flightId, Map<String, UpdateSeatDto> seatDtoMap) {
    var flight = getFlightById(flightId);

    flight.getSeats()
        .stream()
        .filter(seat -> seatDtoMap.containsKey(seat.getId()))
        .forEach(seat -> {
          var dto = seatDtoMap.get(seat.getId());
          if (dto.getPrice() != null && dto.getPrice().compareTo(BigDecimal.ZERO) > 0) {
            seat.setPrice(dto.getPrice());
          }
          if (dto.getSeatStatus() != null) {
            seat.setSeatStatus(dto.getSeatStatus());
          }
        });

    repository.save(flight);
  }

  @Override
  public Map<String, ?> getFlightByCriteria(FlightSearchDto flightSearchDto) {

    //Make sure we have available flights that exist in the database
    List<Flight> availableFlights = getAllFlights();
    if (availableFlights.isEmpty()) {
      return null;
    }

    //Process and differentiate between airports and areas - final list will just have airports
    var originAirports = validateAreasAndReturnAirports(flightSearchDto.getOrigins());
    var destinationAirports = validateAreasAndReturnAirports(flightSearchDto.getDestinations());

    Integer numberOfPassengers;
    if (flightSearchDto.getNumberOfPassengers().isPresent()) {
      numberOfPassengers = flightSearchDto.getNumberOfPassengers().get();
    } else {
      numberOfPassengers = 1;
    }

    if (!flightSearchDto.isMultiHop()) {
      return findNonStopFlightsBasedOnCriteria(originAirports,
                                               destinationAirports,
                                               flightSearchDto.getDepartureDate(),
                                               flightSearchDto.getReturnDate(),
                                               numberOfPassengers);
    } else {
      return findMultiHopFlightsBasedOnCriteria(originAirports,
                                                destinationAirports,
                                                flightSearchDto.getDepartureDate(),
                                                flightSearchDto.getReturnDate(),
                                                numberOfPassengers);
    }
  }

  private Map<String, ?> findMultiHopFlightsBasedOnCriteria(List<Airport> originAirports,
                                                            List<Airport> destinationAirports,
                                                            LocalDate departureDate,
                                                            Optional<LocalDate> returnDate,
                                                            Integer numberOfPassengers) {
    List<Flight> availableFlights = getAllActiveFlights();
    List<List<Flight>> departFlights = getMultiHopFlightsBetweenAirports(originAirports,
                                                                         destinationAirports,
                                                                         numberOfPassengers,
                                                                         availableFlights,
                                                                         departureDate);
    if (returnDate.isPresent()) {
      List<List<Flight>> returnFlights = getMultiHopFlightsBetweenAirports(destinationAirports,
                                                                           originAirports,
                                                                           numberOfPassengers,
                                                                           availableFlights,
                                                                           returnDate.get());
      return Map.of("Origin to destination", departFlights, "Destination to origin", returnFlights);
    }
    return Map.of("Origin to destination", departFlights);
  }

  private List<List<Flight>> getMultiHopFlightsBetweenAirports(List<Airport> origin,
                                                               List<Airport> destination,
                                                               Integer numberOfPassengers,
                                                               List<Flight> availableFlights,
                                                               LocalDate departureDate) {
    FindAllPaths findAllPaths = new FindAllPaths(origin,
                                                 destination,
                                                 availableFlights,
                                                 numberOfPassengers,
                                                 departureDate);
    findAllPaths.getAllPaths();
    return findAllPaths.returnAllValidFlights();
  }

  private Map<String, List<Flight>> findNonStopFlightsBasedOnCriteria(List<Airport> originAirports,
                                                                      List<Airport> destinationAirports,
                                                                      LocalDate departureDate,
                                                                      Optional<LocalDate> returnDate,
                                                                      Integer passengerCount) {
    List<Flight> flightsWithCriteria = findNonStopFlightsBetweenLists(originAirports,
                                                                      destinationAirports,
                                                                      departureDate,
                                                                      passengerCount);
    var setOfFlights = new java.util.HashMap<>(Collections.<String, List<Flight>>emptyMap());
    setOfFlights.put("Origin to Destination", flightsWithCriteria);

    if (returnDate.isPresent()) {
      List<Flight> returnFlightsWithCriteria = findNonStopFlightsBetweenLists(destinationAirports,
                                                                              originAirports,
                                                                              returnDate.get(),
                                                                              passengerCount);
      setOfFlights.put("Destination to Origin", returnFlightsWithCriteria);
    }

    return setOfFlights;
  }

  private List<Airport> validateAreasAndReturnAirports(String[] ListOfLocations) {

    List<Airport> processedList = new ArrayList<>();
    for (String area : ListOfLocations) {
      var tempArea = servicingAreaRepository.findByServicingArea(area);
      var tempAirPort = airportRepository.findById(area);
      if (tempArea.isEmpty() && tempAirPort.isEmpty()) {
        throw new NoSuchAirportException(area);
      }
      if (tempAirPort.isPresent()) {
        processedList.add(tempAirPort.get());
      } else {
        processedList.addAll(airportRepository.findByServicingArea(tempArea.get()));
      }
    }
    if (processedList.isEmpty()) {
      throw new NoSuchAirportException(ListOfLocations);
    }
    return processedList;
  }

  private List<Flight> findNonStopFlightsBetweenLists(List<Airport> origins,
                                                      List<Airport> destinations,
                                                      LocalDate departureDate,
                                                      Integer passengerCount) {
    return getAllActiveFlights().stream().parallel()
        .filter(flight -> origins.contains(flight.getOrigin())
            && destinations.contains(flight.getDestination()))
        .filter(flight -> flight.getApproximateDateTimeStart().toLocalDate().equals(departureDate))
        .filter(flight -> getAvailableSeats(flight.getId()).size() >= passengerCount)
        .collect(Collectors.toList());
  }
}
