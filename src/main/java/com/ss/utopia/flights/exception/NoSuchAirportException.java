package com.ss.utopia.flights.exception;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

public class NoSuchAirportException extends NoSuchElementException {

  private final String id;
  private final String[] idList;

  public NoSuchAirportException(String id) {
    super("No Airport found with id=" + id);
    this.id = id;
    this.idList = null;
  }

  public NoSuchAirportException(String[] idList){
    super("No airports found with ids" + String.join(", ", idList));
    this.idList = idList;
    this.id = null;
  }

  public String getId() {
    return id;
  }

  public Optional<String[]> getIdList(){
    return Optional.ofNullable(this.idList);
  }
}
