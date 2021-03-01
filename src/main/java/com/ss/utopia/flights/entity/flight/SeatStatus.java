package com.ss.utopia.flights.entity.flight;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.ss.utopia.flights.exception.InvalidEnumValue;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum SeatStatus {
  AVAILABLE,
  SOLD,
  HELD,
  BLOCKED;

  private static final List<String> stringValues = Arrays.stream(SeatStatus.values())
      .map(Enum::toString)
      .collect(Collectors.toList());

  @JsonCreator
  public static SeatStatus fromString(String text) {
    try {
      return SeatStatus.valueOf(text.toUpperCase());
    } catch (IllegalArgumentException ex) {
      throw new InvalidEnumValue(text, stringValues);
    }
  }
}
