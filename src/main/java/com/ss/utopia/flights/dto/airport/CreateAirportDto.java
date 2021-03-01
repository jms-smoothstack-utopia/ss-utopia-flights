package com.ss.utopia.flights.dto.airport;

import com.ss.utopia.flights.dto.Mappable;
import com.ss.utopia.flights.entity.airport.Airport;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateAirportDto implements Mappable<Airport> {

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

  @NotBlank
  @Pattern(regexp = "^\\d{5}(?:[-\\s]\\d{4})?$",
      message = "Zipcode does not meet expected format: '#####-####' or '#####'")
  private String zipcode;

  public Airport mapToEntity() {
    return Airport.builder()
        .iataId(iataId)
        .name(name)
        .streetAddress(streetAddress)
        .city(city)
        .state(state)
        .zipcode(zipcode)
        .build();
  }
}
