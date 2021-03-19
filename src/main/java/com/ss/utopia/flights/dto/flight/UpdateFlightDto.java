package com.ss.utopia.flights.dto.flight;

import com.ss.utopia.flights.dto.Updatable;
import com.ss.utopia.flights.entity.flight.Flight;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateFlightDto implements Updatable<Flight> {

  private String originId;
  private String destinationId;
  private Long airplaneId;

  @Override
  public void update(Flight flight) {
    throw new UnsupportedOperationException();
  }
}
