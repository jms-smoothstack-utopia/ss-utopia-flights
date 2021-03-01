package com.ss.utopia.flights.exception;

import java.util.NoSuchElementException;

public class NoSuchAirplaneException extends NoSuchElementException {

  private final Long id;

  public NoSuchAirplaneException(Long id) {
    super("No Airplane found with id=" + id);
    this.id =  id;
  }

  public Long getId() {
    return id;
  }
}
