package com.ss.utopia.flights.dto.flight;

import java.math.BigDecimal;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateFlightDto {

  @NotBlank
  private String originId;

  @NotBlank
  private String destinationId;

  @NotNull
  private Long airplaneId;

  @NotNull
  @Min(value = 0)
  private BigDecimal baseSeatPrice;
}
