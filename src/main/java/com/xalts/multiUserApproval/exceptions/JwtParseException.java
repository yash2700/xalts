package com.xalts.multiUserApproval.exceptions;

public class JwtParseException extends RuntimeException {
  public JwtParseException(String message, Throwable cause) {
    super(message, cause);
  }
}
