package com.ss.utopia.flights.exception;

public class DuplicateAirportException extends IllegalStateException {

  private final String id;

  public DuplicateAirportException(String id) {
    super("Airport with id=" + id + " already exists.");
    this.id = id;
  }

  public String getId() {
    return id;
  }
}
