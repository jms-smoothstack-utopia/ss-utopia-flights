package com.ss.utopia.flights.dto.flight;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

import com.ss.utopia.flights.entity.airport.Airport;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FlightSearchDto {

  private String[] origins;

  private String[] destinations;

  private LocalDate departureDate;

  private Optional<LocalDate> returnDate;

  private Optional<Integer> numberOfPassengers;

  private boolean multiHop;

  private String sortBy;
}