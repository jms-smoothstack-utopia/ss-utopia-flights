package com.ss.utopia.flights.dto.flight;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FlightSearchDto {

  //Check seat availability
  @Min(value = 1)
  private Integer numberOfPassengers;

  //Check if user checks for Roundtrip
  @NotBlank
  private boolean roundTrip;

  @NotBlank
  private boolean multiHop;

  private LocalDate DepartureDate;

  private LocalDate returnDate;

  //Allow the user to choose an area rather than individual airports
  //That support a metropolitan area
  //For example,  if I want all Washington D.C areas
  private String startingServicingArea;

  //They can also choose individual cities pooled from a single servicing area
  //For example, if I want to only fly at IAD or DCA
  private List<String> startingServicingAirports;

  private String destinationServicingArea;

  private List<String> destinationServicingAirports;
}