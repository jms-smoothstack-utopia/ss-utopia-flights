package com.ss.utopia.flights.exception;

import org.springframework.dao.DuplicateKeyException;

public class DuplicateAirportException extends DuplicateKeyException {

  private final String id;

  public DuplicateAirportException(String id) {
    super("Airport with id=" + id + " already exists.");
    this.id = id;
  }

  public String getId() {
    return id;
  }
}
