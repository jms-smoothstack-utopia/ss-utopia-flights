package com.ss.utopia.flights.util;

import com.ss.utopia.flights.entity.airport.Airport;
import com.ss.utopia.flights.entity.flight.Flight;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FindAllPaths {

  private final Airport origin;
  private final Airport destination;
  private Map<Airport, List<Airport>> adjList;
  private final List<List<Airport>> airportPaths;

  public FindAllPaths(Airport origin, Airport destination, List<Flight> listOfFlights){
    this.origin = origin;
    this.destination = destination;
    this.airportPaths = new ArrayList<>();
    buildGraph(listOfFlights);
  }

  private void buildGraph(List<Flight> flights){
    this.adjList = new HashMap<>();
    for(Flight i: flights){
      //The key is a
      Airport a = i.getOrigin();

      //This will be included in List<Airport>
      Airport b = i.getDestination();

      var tempVal = this.adjList.get(a);
      if (tempVal == null){
        adjList.put(a, List.of(b));
      }
      else {
        if (!tempVal.contains(b)){
          tempVal.add(b);
          adjList.replace(a, tempVal);
        }
      }
    }
  }

  public List<List<Airport>> getAllPaths(){
    Map<Airport, Boolean> beingVisited = new HashMap<>();
    ArrayList<Airport> currentPath = new ArrayList<>();
    currentPath.add(this.origin);
    dfs(this.origin, this.destination, beingVisited, currentPath);
    return this.airportPaths;
  }

  private void dfs(Airport origin, Airport destination, Map<Airport, Boolean> beingVisited, ArrayList<Airport> currentPath) {

    if (beingVisited.get(origin) == null){
      beingVisited.put(origin, true);
    }
    else{
      beingVisited.replace(origin, true);
    }

    if (origin.equals(destination)){
      this.airportPaths.add(currentPath);
      return;
    }

    var tempVal = this.adjList.get(origin);
    for(Airport i: tempVal)
    {
      if(beingVisited.get(i) == null || !beingVisited.get(i))
      {
        currentPath.add(i);
        dfs(i, destination, beingVisited, currentPath);
        currentPath.remove(i);
      }
    }
    beingVisited.replace(origin, false);
  }
}
