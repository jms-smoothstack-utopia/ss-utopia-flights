package com.ss.utopia.flights.util;

import com.ss.utopia.flights.entity.airport.Airport;
import com.ss.utopia.flights.entity.flight.Flight;
import com.ss.utopia.flights.entity.flight.Seat;
import com.ss.utopia.flights.entity.flight.SeatStatus;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FindAllPaths {

  private final List<Airport> origin;
  private final List<Airport> destination;
  private final List<Flight> availableFlights;
  private final Integer passengerCount;
  private final LocalDate departureDate;
  public List<List<Flight>> allCorrespondingFlights;

  public FindAllPaths(List<Airport> origin, List<Airport> destination, List<Flight> listOfFlights, Integer numberOfPassengers, LocalDate departureDate){
    this.origin = origin;
    this.destination = destination;
    this.availableFlights = listOfFlights;
    this.passengerCount = numberOfPassengers;
    this.departureDate = departureDate;
    this.allCorrespondingFlights = new ArrayList<>();
  }

  public void getAllPaths(){
    List<Airport> airportsVisited = new ArrayList<>();
    ArrayList<Flight> currentPath = new ArrayList<>();
    for(Airport i: this.origin){
      List<Flight> flightsThatStartAtOrigin = this.availableFlights.stream()
              .filter(flight -> flight.getOrigin() == i)
              .filter(flight -> flight.getApproximateDateTimeStart().toLocalDate().equals(departureDate))
              .filter(flight -> getAvailableSeats(flight).size() >= this.passengerCount)
              .collect(Collectors.toList());
      airportsVisited.add(i);
      for(Flight j: flightsThatStartAtOrigin){
        currentPath.add(j);
        dfs(currentPath, airportsVisited);
        currentPath.remove(j);
      }
      airportsVisited.remove(i);
    }
  }

  public List<List<Flight>> returnAllValidFlights(){
    return this.allCorrespondingFlights;
  }

  private List<Seat> getAvailableSeats(Flight flight) {
    return flight.getSeats().stream()
            .filter(x -> x.getSeatStatus() == SeatStatus.AVAILABLE)
            .collect(Collectors.toList());
  }

  private void dfs(ArrayList<Flight> currentPath, List<Airport> airportsVisited) {
    Flight lastFlight = currentPath.get(currentPath.size() - 1);

    if (this.destination.contains(lastFlight.getDestination())){
      this.allCorrespondingFlights.add(new ArrayList<>(currentPath));
      return;
    }

    var flightEndingTime = lastFlight.getApproximateDateTimeEnd().toLocalDateTime();

    //Make sure we do not go to the same airport twice
    airportsVisited.add(lastFlight.getDestination());

    List<Flight> validFlightsFromThisAirport = this.availableFlights.stream()
            .filter(flight -> flight.getOrigin() == lastFlight.getDestination())
            .filter(flight -> !airportsVisited.contains(flight.getDestination()))
            .filter(flight -> flight.getApproximateDateTimeStart().toLocalDateTime().isAfter(flightEndingTime) && flight.getApproximateDateTimeStart().toLocalDateTime().isBefore(flightEndingTime.plusHours(8)))
            .filter(flight -> getAvailableSeats(flight).size() >= this.passengerCount)
            .collect(Collectors.toList());

    if (validFlightsFromThisAirport.isEmpty()){
      return;
    }

    for(Flight i: validFlightsFromThisAirport){
      currentPath.add(i);
      dfs(currentPath, airportsVisited);
      currentPath.remove(i);
    }

    airportsVisited.remove(lastFlight.getDestination());
  }
}
