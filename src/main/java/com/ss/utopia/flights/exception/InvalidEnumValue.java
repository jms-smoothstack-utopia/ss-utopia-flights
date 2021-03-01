package com.ss.utopia.flights.exception;

import java.util.List;

public class InvalidEnumValue extends IllegalArgumentException {

  public InvalidEnumValue(String invalidValue, List<String> acceptableValues) {
    super("Unrecognized value '" + invalidValue + "'."
              + " Expecting one of: " + String.join(", ", acceptableValues));
  }
}
