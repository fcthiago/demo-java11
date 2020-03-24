package com.sensedia.demo.adapters.amqp;

import com.sensedia.demo.adapters.amqp.config.BrokerOutput;
import com.sensedia.demo.adapters.mappers.UserMapper;
import com.sensedia.demo.domains.User;
import com.sensedia.demo.ports.AmqpPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@EnableBinding({BrokerOutput.class})
public class AmqpUserAdapterOutbound implements AmqpPort {

  private final BrokerOutput output;
  private final UserMapper userMapper;

  @Autowired
  public AmqpUserAdapterOutbound(BrokerOutput output, UserMapper userMapper) {
    this.output = output;
    this.userMapper = userMapper;
  }

  @Override
  public void notifyUserCreation(User user) {
    sendMessage(output.publishCreatedUser(), user, "UserCreation");
  }

  @Override
  public void notifyUserDeletion(User user) {
    sendMessage(output.publishDeletedUser(), user, "DeletedUser");
  }

  private void sendMessage(MessageChannel channel, User user, String eventName) {
    channel.send(
        MessageBuilder.withPayload(userMapper.toUserResponseDto(user))
            .setHeader("event_name", eventName)
            .build());
  }
}
