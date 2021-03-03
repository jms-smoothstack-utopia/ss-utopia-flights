package com.ss.utopia.flights.service;

import com.ss.utopia.flights.dto.flight.CreateFlightDto;
import com.ss.utopia.flights.dto.flight.FlightSearchDto;
import com.ss.utopia.flights.dto.flight.UpdateFlightDto;
import com.ss.utopia.flights.dto.flight.UpdateSeatDto;
import com.ss.utopia.flights.entity.airport.Airport;
import com.ss.utopia.flights.entity.flight.Flight;
import com.ss.utopia.flights.entity.flight.Seat;
import com.ss.utopia.flights.entity.flight.SeatStatus;
import com.ss.utopia.flights.exception.NoSuchFlightException;
import com.ss.utopia.flights.repository.FlightRepository;
import com.ss.utopia.flights.util.FindAllPaths;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class FlightServiceImpl implements FlightService {

  private final FlightRepository repository;
  private final AirportService airportService;
  private final AirplaneService airplaneService;

  public FlightServiceImpl(FlightRepository repository,
                           AirportService airportService,
                           AirplaneService airplaneService) {
    this.repository = repository;
    this.airportService = airportService;
    this.airplaneService = airplaneService;
  }

  @Override
  public List<Flight> getAllFlights() {
    return repository.findAll();
  }

  private List<Flight> getAllActiveFlights(){
    ZonedDateTime machineTime = ZonedDateTime.now();
    return repository.findAll().stream().filter(flight -> machineTime.compareTo(flight.getApproximateDateTimeStart()) < 0 && flight
        .isFlightActive()).collect(Collectors.toList());
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

    var flight = Flight.builder()
        .origin(origin)
        .destination(destination)
        .airplane(airplane)
        .build();

    flight = repository.save(flight);

    // add seats to the flight
    var flightId = flight.getId();
    var seats = new ArrayList<Seat>();

    airplane.getSeatConfigurations()
        .forEach(config -> {
          for (int row = 0; row < config.getNumRows(); row++) {
            for (char col = 'A'; col < config.getNumSeatsPerRow(); col++) {
              var seat = Seat.builder()
                  .id(String.format("%d-%d%c", flightId, row, col))
                  .seatRow(row + 1)
                  .seatColumn(col)
                  .seatClass(config.getSeatClass())
                  .seatStatus(SeatStatus.AVAILABLE)
                  .price(createFlightDto.getBaseSeatPrice())
                  .build();
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

  private Map<String, List<Flight>> getNonStopsFlightByCriteria(FlightSearchDto flightSearchDto) {
    List<String> startingAirports = returnListOfAirportsInRegardsToSearchCriteria(
        flightSearchDto.getStartingServicingArea(), flightSearchDto.getStartingServicingAirports());
    List<String> destinationAirports = returnListOfAirportsInRegardsToSearchCriteria(
        flightSearchDto.getDestinationServicingArea(), flightSearchDto.getDestinationServicingAirports());
    List<Flight> nonStopFlights = findNonStopFlightsBetweenLists(startingAirports, destinationAirports, flightSearchDto.getDepartureDate());
    var setOfFlights = new java.util.HashMap<>(Collections.<String, List<Flight>>emptyMap());
    setOfFlights.put("Origin to Destination", Objects.requireNonNullElse(nonStopFlights, Collections.emptyList()));

    //Check if the flightSearchDto is round trip
    if (flightSearchDto.isRoundTrip()){
      var tempAirports = flightSearchDto.getDestinationServicingAirports();
      var tempArea = flightSearchDto.getDestinationServicingArea();
      var tempDate = flightSearchDto.getReturnDate();
      FlightSearchDto newFlightSearchDto = FlightSearchDto.builder()
          .numberOfPassengers(flightSearchDto.getNumberOfPassengers())
          .destinationServicingAirports(flightSearchDto.getStartingServicingAirports())
          .destinationServicingArea(flightSearchDto.getStartingServicingArea())
          .startingServicingAirports(tempAirports)
          .startingServicingArea(tempArea)
          .DepartureDate(flightSearchDto.getReturnDate())
          .returnDate(tempDate)
          .roundTrip(false)
          .multiHop(false)
          .build();
      Map<String, List<Flight>> returnTrips = getNonStopsFlightByCriteria(newFlightSearchDto);
      List<Flight> values = returnTrips.get("Origin to Destination");
      setOfFlights.put("Destination to Origin", values);
    }

    //Return the flights
    return setOfFlights;
  }


  //I will need to implement this later for multihop
  private Map<String, List<List<Flight>>> getMultiHopFlightByCriteria(FlightSearchDto flightSearchDto) {
    List<String> startingAirports = returnListOfAirportsInRegardsToSearchCriteria(
        flightSearchDto.getStartingServicingArea(), flightSearchDto.getStartingServicingAirports());
    List<String> destinationAirports = returnListOfAirportsInRegardsToSearchCriteria(
        flightSearchDto.getDestinationServicingArea(), flightSearchDto.getDestinationServicingAirports());
    List<List<Flight>> multiHop = findMultiHopFlightsBetweenLists(startingAirports, destinationAirports);
    assert multiHop != null;
    return Map.of("Origin to destination", multiHop);
  }

  private List<List<Flight>> findMultiHopFlightsBetweenLists(List<String> starts, List<String> destinations) {
    if (starts.isEmpty() || destinations.isEmpty())
    {
      return null;
    }
    List<List<Flight>> emptyList = new ArrayList<>() {};
    for(String i: starts){
      for(String j: destinations){
        Airport origin = airportService.getAirportById(i);
        Airport destination = airportService.getAirportById(j);
        var values = addMultiHopFlights(origin, destination);
        System.out.println(values);
      }
    }
    return emptyList;
  }

  private List<Flight> addMultiHopFlights(Airport origin, Airport destination){
    List<Flight> availableFlights = getAllActiveFlights().stream().filter(e -> e.getOrigin() == origin || e.getDestination() == destination).collect(Collectors.toList());
    if (availableFlights.isEmpty()){
      return Collections.emptyList();
    }
    FindAllPaths findAllPaths = new FindAllPaths(origin, destination, availableFlights);
    List<List<Airport>> paths = findAllPaths.getAllPaths();
    System.out.println(paths);
    return availableFlights;
  }

  @Override
  public Map<String, ?> getFlightByCriteria(FlightSearchDto flightSearchDto) {
    if (flightSearchDto.isMultiHop()){
      return getMultiHopFlightByCriteria(flightSearchDto);
    }
    return getNonStopsFlightByCriteria(flightSearchDto);
  }

  private List<String> returnListOfAirportsInRegardsToSearchCriteria(String ServicingArea, List<String> ServicingAirports){
    List<String> airportCodes;
    if (ServicingArea.isEmpty())
    {
       airportCodes = ServicingAirports;
    }
    else{
      airportCodes = airportService.getAirportsByServicingCity(ServicingArea);
    }
    return airportCodes;
  }

  private List<Flight> findNonStopFlightsBetweenLists(List<String> starts, List<String> destinations, LocalDate departureDate){
    if (starts.isEmpty() || destinations.isEmpty()){
      return null;
    }
    return getAllActiveFlights().stream().filter(e -> starts.contains(e.getOrigin().getIataId()) && destinations.contains(e.getDestination().getIataId()))
        .filter(x -> x.getApproximateDateTimeStart().toLocalDate() == departureDate)
        .collect(Collectors.toList());
  }
}
