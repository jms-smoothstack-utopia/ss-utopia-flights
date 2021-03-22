package com.ss.utopia.flights.entity.airport;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Entity
@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class Airport {

  @Id
  @NotNull
  private String iataId;

  @NotBlank
  private String name;

  @NotBlank
  private String streetAddress;

  @NotBlank
  private String city;

  @NotBlank
  private String state;

  @NotBlank
  private String zipcode;

  //Which metropolitan area does this airport service
  @ManyToOne
  private ServicingArea servicingArea;

}
