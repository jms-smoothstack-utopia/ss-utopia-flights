package com.ss.utopia.flights.entity.airplane;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Airplane {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank
  private String name;

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private List<SeatConfiguration> seatConfigurations;

  public int getMaxCapacity() {
    return seatConfigurations.stream()
        .mapToInt(config -> config.getNumRows() * config.getNumSeatsPerRow())
        .sum();
  }
}
