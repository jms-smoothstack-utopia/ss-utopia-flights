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
import com.ss.utopia.flights.repository.AirplaneRepository;
import com.ss.utopia.flights.repository.AirportRepository;
import com.ss.utopia.flights.repository.FlightRepository;
import com.ss.utopia.flights.repository.ServicingAreaRepository;
import com.ss.utopia.flights.util.FindAllPaths;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@Service
@RequiredArgsConstructor
public class FlightServiceImpl implements FlightService {

    private final FlightRepository repository;
    private final AirportService airportService;
    private final AirplaneService airplaneService;
    private final ServicingAreaRepository servicingAreaRepository;
    private final AirportRepository airportRepository;

    @Override
    public List<Flight> getAllFlights() {
        return repository.findAll();
    }

    private List<Flight> getAllActiveFlights() {
        ZonedDateTime machineTime = ZonedDateTime.now();
        return repository.findAll().stream()
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

        var flight = Flight.builder()
                .origin(origin)
                .destination(destination)
                .airplane(airplane)
                .approximateDateTimeStart(createFlightDto.getApproximateDateTimeStart())
                .approximateDateTimeEnd(createFlightDto.getApproximateDateTimeEnd())
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

    public List<Seat> getAvailableSeats(Long flightId){
        return getFlightSeats(flightId).stream()
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

//
//
//  //I will need to implement this later for multihop
//  private Map<String, List<List<Flight>>> getMultiHopFlightByCriteria(FlightSearchDto flightSearchDto) {
//    List<String> startingAirports = returnListOfAirportsInRegardsToSearchCriteria(
//        flightSearchDto.getStartingServicingArea(), flightSearchDto.getStartingServicingAirports());
//    List<String> destinationAirports = returnListOfAirportsInRegardsToSearchCriteria(
//        flightSearchDto.getDestinationServicingArea(), flightSearchDto.getDestinationServicingAirports());
//    List<List<Flight>> multiHop = findMultiHopFlightsBetweenLists(startingAirports, destinationAirports);
//    assert multiHop != null;
//    return Map.of("Origin to destination", multiHop);
//  }

//  private List<List<Flight>> findMultiHopFlightsBetweenLists(List<String> starts, List<String> destinations) {
//    if (starts.isEmpty() || destinations.isEmpty())
//    {
//      return null;
//    }
//    List<List<Flight>> emptyList = new ArrayList<>() {};
//    for(String i: starts){
//      for(String j: destinations){
//        Airport origin = airportService.getAirportById(i);
//        Airport destination = airportService.getAirportById(j);
//        var values = addMultiHopFlights(origin, destination);
//        System.out.println(values);
//      }
//    }
//    return emptyList;
//  }

//  private List<Flight> addMultiHopFlights(Airport origin, Airport destination){
//    List<Flight> availableFlights = getAllActiveFlights().stream().filter(e -> e.getOrigin() == origin || e.getDestination() == destination).collect(Collectors.toList());
//    if (availableFlights.isEmpty()){
//      return Collections.emptyList();
//    }
//    FindAllPaths findAllPaths = new FindAllPaths(origin, destination, availableFlights);
//    List<List<Airport>> paths = findAllPaths.getAllPaths();
//    System.out.println(paths);
//    return availableFlights;
//  }

    @Override
    public Map<String, ?> getFlightByCriteria(FlightSearchDto flightSearchDto) {

        //Make sure we have available flights that exist in the database
        List<Flight> availableFlights = getAllFlights();
        if (availableFlights.isEmpty()){
            return null;
        }

        //Process and differentiate between airports and areas - final list will just have airports
        var originAirports = validateAreasAndReturnAirports(flightSearchDto.getOrigins());
        var destinationAirports = validateAreasAndReturnAirports(flightSearchDto.getDestinations());

        Integer numberOfPassengers;
        if (flightSearchDto.getNumberOfPassengers().isPresent()){
            numberOfPassengers = flightSearchDto.getNumberOfPassengers().get();
        }
        else{
            numberOfPassengers = 1;
        }

        if (!flightSearchDto.isMultiHop()) {
            return findNonStopFlightsBasedOnCriteria(originAirports, destinationAirports, flightSearchDto.getDepartureDate(), flightSearchDto.getReturnDate(), numberOfPassengers);
        } else {
            return findMultiHopFlightsBasedOnCriteria(originAirports, destinationAirports, flightSearchDto.getDepartureDate(), flightSearchDto.getReturnDate(), numberOfPassengers);
        }
    }

    private Map<String,?> findMultiHopFlightsBasedOnCriteria(List<Airport> originAirports, List<Airport> destinationAirports, LocalDate departureDate, Optional<LocalDate> returnDate, Integer numberOfPassengers) {
        List<Flight> availableFlights = getAllActiveFlights();
        var flightTracker = new ArrayList<>(Collections.emptyList());
        for(Airport i: originAirports){
            for(Airport j: destinationAirports){
                List<List<Flight>> tempList = getMultiHopFlightsBetweenAirports(i, j, numberOfPassengers, availableFlights, departureDate);
                if (tempList != null){
                    flightTracker.add(tempList);
                }
            }
        }

        if (flightTracker.isEmpty()){
            return null;
        }
        return Map.of("Origin to Destination", flightTracker);
    }

    private List<List<Flight>> getMultiHopFlightsBetweenAirports(Airport origin, Airport destination, Integer numberOfPassengers, List<Flight> availableFlights, LocalDate departureDate) {
        FindAllPaths findAllPaths = new FindAllPaths(origin, destination, availableFlights);
        List<List<Airport>> pathsFromAirports = findAllPaths.getAllPaths();

        if (pathsFromAirports.isEmpty() || pathsFromAirports.size() == 1){
            return null;
        }

        List<List<Flight>> returnList = new ArrayList<>(Collections.emptyList());
        //Now I need to map between airports and flights
        for(int i = 0; i < pathsFromAirports.size(); i++){
            var tempPlan = pathsFromAirports.get(i);
            var airportLeave = departureDate;
            for(int j = 0; j < tempPlan.size(); j++){

                //We do not need to check the last element
                //We are at the destination
                if (j == tempPlan.size() - 1){
                    break;
                }

                var tempFlightPlan = findNonStopFlightsBetweenLists(List.of(tempPlan.get(j)), List.of(tempPlan.get(j + 1)), airportLeave, numberOfPassengers);

                if (tempFlightPlan.isEmpty()){
                    break;
                }

                var sortListFlights = tempFlightPlan.stream()
                        .sorted(Comparator.comparing(Flight::getApproximateDateTimeStart))
                        .collect(Collectors.toList());

                airportLeave = sortListFlights.get(0).getApproximateDateTimeEnd().toLocalDate();

                returnList.add(sortListFlights);
            }
        }

        return returnList;
    }

    private Map<String, List<Flight>> findNonStopFlightsBasedOnCriteria(List<Airport> originAirports, List<Airport> destinationAirports, LocalDate departureDate, Optional<LocalDate> returnDate, Integer passengerCount) {
        List<Flight> flightsWithCriteria = findNonStopFlightsBetweenLists(originAirports, destinationAirports, departureDate, passengerCount);
        var setOfFlights = new java.util.HashMap<>(Collections.<String, List<Flight>>emptyMap());
        setOfFlights.put("Origin to Destination", flightsWithCriteria);

        //If its present, then user wants round trip
        if (returnDate.isPresent()) {
            List<Flight> returnFlightsWithCriteria = findNonStopFlightsBetweenLists(destinationAirports, originAirports, returnDate.get(), passengerCount);
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

    private List<Flight> findNonStopFlightsBetweenLists(List<Airport> origins, List<Airport> destinations, LocalDate departureDate, Integer passengerCount) {

        System.out.println("This is the passenger count: " + passengerCount);

        System.out.println("These are available seats for the first flight in database: " + getAvailableSeats(getAllActiveFlights().get(0).getId()));

        var firstFilter = getAllActiveFlights().stream()
                .filter(flight -> origins.contains(flight.getOrigin()) && destinations.contains(flight.getDestination()))
                .collect(Collectors.toList());

        System.out.println(firstFilter);

        var secondFilter = firstFilter.stream()
                .filter(flight -> flight.getApproximateDateTimeStart().toLocalDate().equals(departureDate))
                .collect(Collectors.toList());

        System.out.println(secondFilter);

        return getAllActiveFlights().stream()
                .filter(flight -> origins.contains(flight.getOrigin()) && destinations.contains(flight.getDestination()))
                .filter(flight -> flight.getApproximateDateTimeStart().toLocalDate().equals(departureDate))
                .filter(flight -> getAvailableSeats(flight.getId()).size() >= passengerCount)
                .collect(Collectors.toList());
    }
}
