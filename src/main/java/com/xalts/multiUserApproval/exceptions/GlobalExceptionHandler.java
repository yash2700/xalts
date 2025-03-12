package com.xalts.multiUserApproval.exceptions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.xalts.multiUserApproval.constants.ExceptionConstants;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@PropertySource(value = "classpath:messages.properties")
public class GlobalExceptionHandler {
  @Autowired
  private Environment environment;

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<?> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException ex) {
    Map<String, String> errorMap = new HashMap<>();

    // Iterating over each field error
    ex.getBindingResult().getAllErrors().forEach((error) -> {
      String fieldName = ((FieldError) error).getField();
      String message = error.getDefaultMessage();
      errorMap.put(fieldName, message);
    });
    ErrorResponse errorResponse = ErrorResponse.builder()
        .errorCode(ExceptionConstants.INVALID_INPUT)
        .message(errorMap)
        .status(HttpStatus.BAD_REQUEST.value())
        .build();
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(StandardException.class)
  public ResponseEntity<?> handleStandardException(StandardException ex) {
    String message = environment.getProperty(ex.getMessage());
    ErrorResponse errorResponse = ErrorResponse.builder()
        .errorCode(ex.getMessage())
        .message(message)
        .status(HttpStatus.BAD_REQUEST.value())
        .build();
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }



}
