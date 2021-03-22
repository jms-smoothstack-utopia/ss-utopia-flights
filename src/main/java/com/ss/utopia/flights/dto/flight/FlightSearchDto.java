package com.ss.utopia.flights.dto.flight;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.time.LocalDate;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@SuppressFBWarnings(value = {"EI_EXPOSE_REP", "EI_EXPOSE_REP2"},
    justification = "acceptable risk")
public class FlightSearchDto {

  private String[] origins;

  private String[] destinations;

  private LocalDate departureDate;

  private Optional<LocalDate> returnDate;

  private Optional<Integer> numberOfPassengers;

  private boolean multiHop;

  private String sortBy;
}