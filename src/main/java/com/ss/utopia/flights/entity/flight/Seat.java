package com.ss.utopia.flights.entity.flight;

import com.ss.utopia.flights.entity.shared.SeatClass;
import java.math.BigDecimal;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Seat {

  @Id
  private String id; // Flight ID, Seat Row/Col, and Class, ie "1234-2A"

  @NotNull
  private Integer seatRow;

  @NotNull
  private Character seatColumn;

  @Enumerated(EnumType.STRING)
  private SeatClass seatClass;

  @Enumerated(EnumType.STRING)
  private SeatStatus seatStatus;

  @NotNull
  private BigDecimal price;
}
