package com.ss.utopia.flights.dto.airplane;

import com.ss.utopia.flights.dto.Mappable;
import com.ss.utopia.flights.dto.Updatable;
import com.ss.utopia.flights.entity.airplane.Airplane;
import com.ss.utopia.flights.entity.airplane.SeatConfiguration;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AirplaneDto implements Mappable<Airplane>, Updatable<Airplane> {

  @NotBlank
  private String name;

  @NotNull
  private List<SeatConfiguration> seatConfigurations;

  @Override
  public Airplane mapToEntity() {
    return Airplane.builder()
        .name(name)
        .seatConfigurations(seatConfigurations)
        .build();
  }

  @Override
  public void update(Airplane airplane) {
    if (name != null && !name.isBlank()) {
      airplane.setName(name);
    }
    if (seatConfigurations != null && !seatConfigurations.isEmpty()) {
      airplane.setSeatConfigurations(seatConfigurations);
    }
  }
}
