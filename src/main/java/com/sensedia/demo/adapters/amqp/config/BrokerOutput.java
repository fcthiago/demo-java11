package com.sensedia.demo.adapters.amqp.config;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Component;

@Component
public interface BrokerOutput {

  @Output(BindConfig.PUBLISH_USER_CREATED)
  MessageChannel publishUserCreated();

  @Output(BindConfig.PUBLISH_USER_DELETED)
  MessageChannel publishUserDeleted();

  @Output(BindConfig.PUBLISH_USER_OPERATION_ERROR)
  MessageChannel publishUserOperationError();
}
