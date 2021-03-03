package com.ss.utopia.flights.entity.flight;

import com.ss.utopia.flights.entity.airplane.Airplane;
import com.ss.utopia.flights.entity.airport.Airport;
import java.time.ZonedDateTime;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
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
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Flight {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Integer possibleLoyaltyPoints;

  @ManyToOne
  private Airport origin;

  @ManyToOne
  private Airport destination;

  @OneToOne
  private Airplane airplane;

  @OneToMany(cascade = CascadeType.REMOVE)
  private List<Seat> seats;

  @Column(updatable = false)
  @CreationTimestamp
  private ZonedDateTime creationDateTime;

  private ZonedDateTime approximateDateTimeStart;

  private ZonedDateTime approximateDateTimeEnd;

  private boolean flightActive;
}
