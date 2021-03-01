package com.ss.utopia.flights.exception;

import java.util.NoSuchElementException;

public class NoSuchFlightException extends NoSuchElementException {

  private final Long id;

  public NoSuchFlightException(Long id) {
    super("No Flight found with id=" + id);
    this.id = id;
  }

  public Long getId() {
    return id;
  }
}
