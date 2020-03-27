package com.sensedia.demo.adapters.amqp.config;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface BrokerInput {

  @Input(BindConfig.SUBSCRIBE_USER_CREATION_REQUESTED)
  SubscribableChannel subscribeUserCreationRequested();

  @Input(BindConfig.SUBSCRIBE_USER_DELETION_REQUESTED)
  SubscribableChannel subscribeUserDeletionRequested();

  @Input(BindConfig.SUBSCRIBE_USER_UPDATE_REQUESTED)
  SubscribableChannel subscribeUserUpdateRequested();
}
