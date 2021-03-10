package com.ss.utopia.flights.dto.airport;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServicingAreaDto {

  @NotBlank(message = "We need a servicing area")
  private String servicingArea;

}
