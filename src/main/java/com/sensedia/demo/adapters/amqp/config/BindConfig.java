package com.sensedia.demo.adapters.amqp.config;

public class BindConfig {

  public static final String SUBSCRIBE_USER_CREATION_REQUESTED = "subscribeUserCreationRequested";
  public static final String SUBSCRIBE_USER_UPDATE_REQUESTED = "subscribeUserUpdateRequested";
  public static final String SUBSCRIBE_USER_DELETION_REQUESTED = "subscribeUserDeletionRequested";
  public static final String PUBLISH_USER_DELETED = "publishUserDeleted";
  public static final String PUBLISH_USER_CREATED = "publishUserCreated";
  public static final String PUBLISH_USER_OPERATION_ERROR = "publishUserOperationError";

  private BindConfig() {}
}
