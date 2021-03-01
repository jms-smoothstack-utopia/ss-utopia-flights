package com.ss.utopia.flights.entity.flight;

import com.ss.utopia.flights.entity.airplane.Airplane;
import com.ss.utopia.flights.entity.airport.Airport;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Flight {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  private Airport origin;

  @ManyToOne
  private Airport destination;

  @OneToOne
  private Airplane airplane;

  @OneToMany(cascade = CascadeType.REMOVE)
  private List<Seat> seats;
}
