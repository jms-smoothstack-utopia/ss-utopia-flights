package com.ss.utopia.flights.exception;

import java.util.NoSuchElementException;

public class NoSuchAirportException extends NoSuchElementException {

  private final String id;

  public NoSuchAirportException(String id) {
    super("No Airport found with id=" + id);
    this.id = id;
  }

  public String getId() {
    return id;
  }
}
