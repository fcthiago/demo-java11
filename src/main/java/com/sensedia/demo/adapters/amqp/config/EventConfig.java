package com.sensedia.demo.adapters.amqp.config;

public class EventConfig {

  public static final String USER_CREATION_EVENT_NAME = "UserCreation";
  public static final String USER_DELETION_EVENT_NAME = "UserDeletion";
  public static final String USER_OPERATION_ERROR_EVENT_NAME = "UserOperationError";

  private EventConfig() {}
}
