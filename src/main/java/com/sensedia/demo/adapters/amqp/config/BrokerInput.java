package com.sensedia.demo.adapters.amqp.config;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface BrokerInput {

  @Input(BindConfig.SUBSCRIBE_USER_CREATION_REQUESTED)
  SubscribableChannel subscribeUserCreationRequested();
}
