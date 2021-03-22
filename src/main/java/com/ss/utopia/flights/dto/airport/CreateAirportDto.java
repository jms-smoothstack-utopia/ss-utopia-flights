package com.ss.utopia.flights.dto.airport;

import com.ss.utopia.flights.entity.airport.Airport;
import com.ss.utopia.flights.entity.airport.ServicingArea;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateAirportDto {

  @NotBlank(message = "IATA ID is mandatory.")
  private String iataId;

  @NotBlank(message = "Name is mandatory")
  private String name;

  @NotBlank(message = "Street address is mandatory")
  private String streetAddress;

  @NotBlank(message = "City is mandatory")
  private String city;

  @NotBlank(message = "State is mandatory")
  private String state;

  @NotNull
  private long servicingAreaId;

  @NotBlank
  @Pattern(regexp = "^\\d{5}(?:[-\\s]\\d{4})?$",
      message = "Zipcode does not meet expected format: '#####-####' or '#####'")
  private String zipcode;

  public Airport mapToEntity(ServicingArea servicingArea) {
    return Airport.builder()
        .iataId(iataId)
        .name(name)
        .streetAddress(streetAddress)
        .city(city)
        .state(state)
        .zipcode(zipcode)
        .servicingArea(servicingArea)
        .build();
  }
}
