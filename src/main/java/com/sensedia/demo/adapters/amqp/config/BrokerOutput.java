package com.sensedia.demo.adapters.amqp.config;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Component;

@Component
public interface BrokerOutput {

  @Output("publishCreatedUser")
  MessageChannel publishCreatedUser();

  @Output("publishDeletedUser")
  MessageChannel publishDeletedUser();
}
