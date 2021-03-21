package com.ss.utopia.flights.exception;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ExceptionControllerAdvisor {

  private static final String ERROR_KEY = "error";
  private static final String STATUS_KEY = "status";

  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ExceptionHandler(NoSuchElementException.class)
  public Map<String, Object> noSuchAirportException(NoSuchElementException ex) {
    log.error(ex.getMessage());

    return baseResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
  }

  @ResponseStatus(HttpStatus.CONFLICT)
  @ExceptionHandler(DuplicateKeyException.class)
  public Map<String, Object> duplicateAirportException(DuplicateKeyException ex) {
    log.error(ex.getMessage());

    return baseResponse(ex.getMessage(), HttpStatus.CONFLICT);
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(InvalidEnumValue.class)
  public Map<String, Object> invalidEnumValue(InvalidEnumValue ex) {
    log.error(ex.getMessage());
    return baseResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
  }

  private Map<String, Object> baseResponse(String msg, HttpStatus httpStatus) {
    var response = new HashMap<String, Object>();
    response.put(ERROR_KEY, msg);
    response.put(STATUS_KEY, httpStatus.value());
    return response;
  }
}
