package com.ss.utopia.flights.exception;

import java.util.List;
import java.util.NoSuchElementException;
import lombok.Getter;

public class NoSuchAirportException extends NoSuchElementException {

  @Getter
  private final List<String> idList;

  public NoSuchAirportException(String id) {
    this(List.of(id));
  }

  public NoSuchAirportException(List<String> idList) {
    super("No airports found with ids: " + String.join(", ", idList));
    this.idList = idList;
  }
}
