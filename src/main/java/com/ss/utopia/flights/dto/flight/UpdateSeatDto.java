package com.ss.utopia.flights.dto.flight;

import com.ss.utopia.flights.entity.flight.SeatStatus;
import java.math.BigDecimal;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateSeatDto {

  @NotNull
  private SeatStatus seatStatus;

  @NotNull
  @Min(value = 0)
  private BigDecimal price;
}
