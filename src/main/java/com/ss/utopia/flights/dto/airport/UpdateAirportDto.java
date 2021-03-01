package com.ss.utopia.flights.dto.airport;

import com.ss.utopia.flights.dto.Updatable;
import com.ss.utopia.flights.entity.airport.Airport;
import javax.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateAirportDto implements Updatable<Airport> {

  private String name;
  private String streetAddress;
  private String city;
  private String state;

  @Pattern(regexp = "^\\d{5}(?:[-\\s]\\d{4})?$",
      message = "Zipcode does not meet expected format: '#####-####' or '#####'")
  private String zipcode;

  public void update(Airport airport) {
    if (name != null && !name.isBlank()) {
      airport.setName(name);
    }

    if (streetAddress != null && !streetAddress.isBlank()) {
      airport.setStreetAddress(streetAddress);
    }

    if (city != null && !city.isBlank()) {
      airport.setCity(city);
    }

    if (state != null && !state.isBlank()) {
      airport.setState(state);
    }

    if (zipcode != null && !zipcode.isBlank()) {
      airport.setZipcode(zipcode);
    }
  }
}
