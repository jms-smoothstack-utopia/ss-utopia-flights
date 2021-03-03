package com.ss.utopia.flights.service;

import com.ss.utopia.flights.dto.airport.ServicingAreaDto;
import com.ss.utopia.flights.entity.airport.ServicingArea;
import java.util.List;

public interface ServicingAreaService {

  List<ServicingArea> getAllAreas();

  ServicingArea getServicingAreaById(Long id);

  ServicingArea createNewServicingArea(ServicingAreaDto servicingAreaDto);
}
