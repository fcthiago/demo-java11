package com.sensedia.commons.handles;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.common.base.CaseFormat;
import com.sensedia.commons.exceptions.ApplicationException;
import com.sensedia.commons.exceptions.DefaultErrorResponse;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.internal.engine.path.NodeImpl;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.time.format.DateTimeParseException;
import java.util.Set;

@PropertySource(value = {"classpath:ValidationMessages.properties"})
@ControllerAdvice
public class ErrorHandler {
  private static final Logger log = LoggerFactory.getLogger(ErrorHandler.class);

  @Value("${com.sensedia.InvalidField.message}")
  private String invalidFieldMessage;

  @Value("${com.sensedia.MissingField.message}")
  private String missingFieldMessage;

  @ExceptionHandler({ApplicationException.class})
  @ResponseBody
  public DefaultErrorResponse handleApplicationException(
      HttpServletRequest req, HttpServletResponse res, ApplicationException e) {
    log.error("", e);
    res.setStatus(e.getDefaultErrorResponse().getStatus());
    return e.getDefaultErrorResponse();
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler({MethodArgumentNotValidException.class})
  @ResponseBody
  public DefaultErrorResponse handleMethodArgumentNotValidException(
      HttpServletRequest req, Exception e) {
    log.error("", e);
    String errorMessage = buildItems(((MethodArgumentNotValidException) e).getBindingResult());
    return new DefaultErrorResponse(HttpStatus.BAD_REQUEST, errorMessage);
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler({MissingServletRequestParameterException.class})
  @ResponseBody
  public DefaultErrorResponse handleBadRequestMissingServletRequestParameterException(
      HttpServletRequest req, Exception e) {
    log.error("", e);
    String errorMessage = buildErrorMessage((MissingServletRequestParameterException) e);
    return new DefaultErrorResponse(HttpStatus.BAD_REQUEST, errorMessage);
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler({JsonMappingException.class})
  @ResponseBody
  public DefaultErrorResponse handleJsonMappingException(HttpServletRequest req, Exception e) {
    log.error("", e);
    String errorMessage = buildErrorMessage((JsonMappingException) e.getCause());
    return new DefaultErrorResponse(HttpStatus.BAD_REQUEST, errorMessage);
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler({DateTimeParseException.class})
  @ResponseBody
  public DefaultErrorResponse handleDateTimeParseException(HttpServletRequest req, Exception e) {
    log.error("", e);
    String errorMessage = buildErrorMessage((DateTimeParseException) e);
    return new DefaultErrorResponse(HttpStatus.BAD_REQUEST, errorMessage);
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler({ConstraintViolationException.class})
  @ResponseBody
  public DefaultErrorResponse handleConstraintViolationException(
      HttpServletRequest req, Exception e) {
    log.error("", e);
    String message = errorFields(((ConstraintViolationException) e).getConstraintViolations());
    return new DefaultErrorResponse(HttpStatus.BAD_REQUEST, message);
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler({HttpMessageNotReadableException.class})
  @ResponseBody
  public DefaultErrorResponse handleHttpMessageNotReadableException(
      HttpServletRequest req, Exception e) {
    log.error("", e);
    String message = handleNotReadableMessage(req, (HttpMessageNotReadableException) e);
    return new DefaultErrorResponse(HttpStatus.BAD_REQUEST, message);
  }

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler({Exception.class})
  @ResponseBody
  public DefaultErrorResponse handleInternalError(HttpServletRequest req, Exception e) {
    log.error("", e);
    return new DefaultErrorResponse(
        HttpStatus.INTERNAL_SERVER_ERROR.value(),
        HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
        e.getMessage());
  }

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler({InternalError.class})
  @ResponseBody
  public DefaultErrorResponse handleInternalErrorImpl(HttpServletRequest req, Exception e) {
    log.error("", e);
    return new DefaultErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
  }

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler({NullPointerException.class})
  @ResponseBody
  public DefaultErrorResponse handleNullPointer(HttpServletRequest req, Exception e) {
    log.error("", e);
    return new DefaultErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler({IllegalArgumentException.class})
  @ResponseBody
  public DefaultErrorResponse handleIllegalArgument(HttpServletRequest req, Exception e) {
    log.error("", e);
    return new DefaultErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler({MissingRequestHeaderException.class})
  @ResponseBody
  public DefaultErrorResponse handleBadRequestMissingRequestHeaderException(
      HttpServletRequest req, Exception e) throws IOException {
    log.error("", e);
    String errorMessage = buildErrorMessage((MissingRequestHeaderException) e);
    return new DefaultErrorResponse(HttpStatus.BAD_REQUEST, errorMessage);
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler({MethodArgumentTypeMismatchException.class})
  @ResponseBody
  public DefaultErrorResponse handleMethodArgumentTypeMismatchException(
      HttpServletRequest req, Exception e) {
    log.error("", e);
    String errorMessage = buildErrorMessage((MethodArgumentTypeMismatchException) e);
    return new DefaultErrorResponse(HttpStatus.BAD_REQUEST, errorMessage);
  }

  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
  @ResponseBody
  public DefaultErrorResponse handleHttpRequestMethodNotSupportedException(
      HttpServletRequest req, Exception e) {
    log.error("", e);
    return new DefaultErrorResponse(HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(NoHandlerFoundException.class)
  @ResponseStatus(value = HttpStatus.NOT_FOUND)
  @ResponseBody
  public DefaultErrorResponse requestHandlingNoHandlerFound() {
    return new DefaultErrorResponse(HttpStatus.NOT_FOUND);
  }

  private String errorFields(Set<ConstraintViolation<?>> constraintsViolation) {
    if (constraintsViolation == null || constraintsViolation.isEmpty()) {
      return "";
    }
    ConstraintViolation<?> constraintViolation = constraintsViolation.stream().findFirst().get();
    NodeImpl node = ((PathImpl) constraintViolation.getPropertyPath()).getLeafNode();
    String message = constraintViolation.getMessage();
    return convertToSnakeCase(node.getName()) + " " + message;
  }

  private String buildErrorMessage(MissingRequestHeaderException e) {
    return StringUtils.join(convertToSnakeCase(e.getHeaderName()), " ", missingFieldMessage);
  }

  private String buildErrorMessage(MissingServletRequestParameterException e) {
    return StringUtils.join(convertToSnakeCase(e.getParameterName()), " ", missingFieldMessage);
  }

  private String buildErrorMessage(MethodArgumentTypeMismatchException e) {
    return StringUtils.join(convertToSnakeCase(e.getName()), " ", invalidFieldMessage);
  }

  private String buildErrorMessage(JsonMappingException e) {
    String fieldName = e.getPath().get(0).getFieldName();
    return StringUtils.join(convertToSnakeCase(fieldName), " ", invalidFieldMessage);
  }

  private String buildErrorMessage(DateTimeParseException e) {
    return StringUtils.join(convertToSnakeCase(e.getMessage()), " ", invalidFieldMessage);
  }

  private String handleNotReadableMessage(
      HttpServletRequest req, HttpMessageNotReadableException e) {
    if (e.getCause() instanceof JsonMappingException) {
      return buildErrorMessage((JsonMappingException) e.getCause());
    }

    return e.getMessage();
  }

  private String convertToSnakeCase(String value) {
    return CaseFormat.LOWER_CAMEL.converterTo(CaseFormat.LOWER_UNDERSCORE).convert(value);
  }

  private String buildItems(BindingResult bindingResult) {
    if (bindingResult.getFieldErrors().isEmpty()) {
      return "";
    }
    String errorMessage =
        convertToSnakeCase(bindingResult.getFieldErrors().get(0).getField())
            + " "
            + bindingResult.getFieldErrors().stream().findFirst().get().getDefaultMessage();

    return errorMessage;
  }
}
