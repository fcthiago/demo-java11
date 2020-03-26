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
import org.springframework.validation.FieldError;
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

import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
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
      HttpServletResponse res, ApplicationException e) {
    log.error("", e);
    res.setStatus(e.getDefaultErrorResponse().getStatus());
    return e.getDefaultErrorResponse();
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler({MethodArgumentNotValidException.class})
  @ResponseBody
  public DefaultErrorResponse handleMethodArgumentNotValidException(
      MethodArgumentNotValidException e) {
    log.error("", e);
    String errorMessage = buildItems(e.getBindingResult());
    return new DefaultErrorResponse(HttpStatus.BAD_REQUEST, errorMessage);
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler({MissingServletRequestParameterException.class})
  @ResponseBody
  public DefaultErrorResponse handleBadRequestMissingServletRequestParameterException(
      MissingServletRequestParameterException e) {
    log.error("", e);
    String errorMessage = buildErrorMessage(e);
    return new DefaultErrorResponse(HttpStatus.BAD_REQUEST, errorMessage);
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler({JsonMappingException.class})
  @ResponseBody
  public DefaultErrorResponse handleJsonMappingException(JsonMappingException e) {
    log.error("", e);
    String errorMessage = buildErrorMessage(e);
    return new DefaultErrorResponse(HttpStatus.BAD_REQUEST, errorMessage);
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler({DateTimeParseException.class})
  @ResponseBody
  public DefaultErrorResponse handleDateTimeParseException(DateTimeParseException e) {
    log.error("", e);
    String errorMessage = buildErrorMessage(e);
    return new DefaultErrorResponse(HttpStatus.BAD_REQUEST, errorMessage);
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler({ConstraintViolationException.class})
  @ResponseBody
  public DefaultErrorResponse handleConstraintViolationException(ConstraintViolationException e) {
    log.error("", e);
    String message = errorFields(e.getConstraintViolations());
    return new DefaultErrorResponse(HttpStatus.BAD_REQUEST, message);
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler({HttpMessageNotReadableException.class})
  @ResponseBody
  public DefaultErrorResponse handleHttpMessageNotReadableException(
      HttpMessageNotReadableException e) {
    log.error("", e);
    String message = handleNotReadableMessage(e);
    return new DefaultErrorResponse(HttpStatus.BAD_REQUEST, message);
  }

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler({Exception.class})
  @ResponseBody
  public DefaultErrorResponse handleInternalError(Exception e) {
    log.error("", e);
    return new DefaultErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
  }

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler({InternalError.class})
  @ResponseBody
  public DefaultErrorResponse handleInternalErrorImpl(InternalError e) {
    log.error("", e);
    return new DefaultErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
  }

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler({NullPointerException.class})
  @ResponseBody
  public DefaultErrorResponse handleNullPointer(NullPointerException e) {
    log.error("", e);
    return new DefaultErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler({IllegalArgumentException.class})
  @ResponseBody
  public DefaultErrorResponse handleIllegalArgument(Exception e) {
    log.error("", e);
    return new DefaultErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler({MissingRequestHeaderException.class})
  @ResponseBody
  public DefaultErrorResponse handleBadRequestMissingRequestHeaderException(
      MissingRequestHeaderException e) {
    log.error("", e);
    String errorMessage = buildErrorMessage(e);
    return new DefaultErrorResponse(HttpStatus.BAD_REQUEST, errorMessage);
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler({MethodArgumentTypeMismatchException.class})
  @ResponseBody
  public DefaultErrorResponse handleMethodArgumentTypeMismatchException(
      MethodArgumentTypeMismatchException e) {
    log.error("", e);
    String errorMessage = buildErrorMessage(e);
    return new DefaultErrorResponse(HttpStatus.BAD_REQUEST, errorMessage);
  }

  @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
  @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
  @ResponseBody
  public DefaultErrorResponse handleHttpRequestMethodNotSupportedException(Exception e) {
    log.error("", e);
    return new DefaultErrorResponse(HttpStatus.METHOD_NOT_ALLOWED);
  }

  @ExceptionHandler(NoHandlerFoundException.class)
  @ResponseStatus(value = HttpStatus.NOT_FOUND)
  @ResponseBody
  public DefaultErrorResponse requestHandlingNoHandlerFound() {
    return new DefaultErrorResponse(HttpStatus.NOT_FOUND);
  }

  private String errorFields(Set<ConstraintViolation<?>> constraintsViolation) {
    if (constraintsViolation == null || constraintsViolation.isEmpty()) return StringUtils.EMPTY;

    Optional<ConstraintViolation<?>> optional = constraintsViolation.stream().findFirst();

    if (optional.isEmpty()) return StringUtils.EMPTY;

    ConstraintViolation<?> constraintViolation = optional.get();
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

  private String handleNotReadableMessage(HttpMessageNotReadableException e) {
    if (e.getCause() instanceof JsonMappingException) {
      return buildErrorMessage((JsonMappingException) e.getCause());
    }

    return e.getMessage();
  }

  private String convertToSnakeCase(String value) {
    return CaseFormat.LOWER_CAMEL.converterTo(CaseFormat.LOWER_UNDERSCORE).convert(value);
  }

  private String buildItems(BindingResult bindingResult) {
    List<FieldError> fieldErrors = bindingResult.getFieldErrors();

    if (fieldErrors.isEmpty()) return StringUtils.EMPTY;

    Optional<FieldError> optional = fieldErrors.stream().findFirst();

    if (optional.isEmpty()) return StringUtils.EMPTY;

    return convertToSnakeCase(fieldErrors.get(0).getField())
        + " "
        + optional.get().getDefaultMessage();
  }
}
