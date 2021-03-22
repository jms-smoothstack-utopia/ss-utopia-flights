package com.ss.utopia.flights.dto.flight;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

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
  @FutureOrPresent
  @DateTimeFormat(iso = ISO.DATE_TIME)
  private LocalDateTime approximateDateTimeStart;

  @NotNull
  @FutureOrPresent
  @DateTimeFormat(iso = ISO.DATE_TIME)
  private LocalDateTime approximateDateTimeEnd;

  @NotNull
  @Min(value = 0)
  private BigDecimal baseSeatPrice;

  private Integer loyaltyPoints;
}
