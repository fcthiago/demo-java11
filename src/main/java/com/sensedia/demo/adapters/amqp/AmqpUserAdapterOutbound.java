package com.sensedia.demo.adapters.amqp;

import com.sensedia.commons.errors.domains.DefaultErrorResponse;
import com.sensedia.demo.adapters.amqp.config.BrokerOutput;
import com.sensedia.demo.adapters.mappers.UserMapper;
import com.sensedia.demo.domains.User;
import com.sensedia.demo.ports.AmqpPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import static com.sensedia.commons.headers.DefaultHeader.APP_ID_HEADER_NAME;
import static com.sensedia.commons.headers.DefaultHeader.EVENT_NAME_HEADER_HEADER;
import static com.sensedia.demo.adapters.amqp.config.EventConfig.*;

@Service
@EnableBinding({BrokerOutput.class})
public class AmqpUserAdapterOutbound implements AmqpPort {

  private final BrokerOutput output;
  private final UserMapper userMapper;

  @Value("${spring.application.name}")
  protected String appId;

  @Autowired
  public AmqpUserAdapterOutbound(BrokerOutput output, UserMapper userMapper) {
    this.output = output;
    this.userMapper = userMapper;
  }

  @Override
  public void notifyUserCreation(User user) {
    sendMessage(output.publishUserCreated(), user, USER_CREATION_EVENT_NAME);
  }

  @Override
  public void notifyUserDeletion(User user) {
    sendMessage(output.publishUserDeleted(), user, USER_DELETION_EVENT_NAME);
  }

  @Override
  public void notifyUserOperationError(DefaultErrorResponse errorResponse) {
    sendMessage(output.publishUserOperationError(), errorResponse, USER_OPERATION_ERROR_EVENT_NAME);
  }

  private void sendMessage(MessageChannel channel, User user, String eventName) {
    sendMessage(channel, userMapper.toUserResponseDto(user), eventName);
  }

  private void sendMessage(MessageChannel channel, Object object, String eventName) {
    channel.send(
        MessageBuilder.withPayload(object)
            .setHeader(EVENT_NAME_HEADER_HEADER, eventName)
            .setHeader(APP_ID_HEADER_NAME, appId)
            .build());
  }
}
