package com.sensedia.commons.exceptions;

import org.springframework.http.HttpStatus;

public abstract class ApplicationException extends RuntimeException {

  private DefaultErrorResponse defaultErrorResponse;

  public ApplicationException(
      HttpStatus status, String detail, String type, String title, Throwable cause) {
    this(new DefaultErrorResponse(status, detail, type, title), cause);
  }

  private ApplicationException(DefaultErrorResponse defaultErrorResponse, Throwable cause) {
    super(defaultErrorResponse.toString(), cause);
    this.defaultErrorResponse = defaultErrorResponse;
  }

  public DefaultErrorResponse getDefaultErrorResponse() {
    return defaultErrorResponse;
  }
}
