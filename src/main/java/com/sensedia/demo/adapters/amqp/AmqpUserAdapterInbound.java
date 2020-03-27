package com.sensedia.demo.adapters.amqp;

import com.sensedia.commons.errors.resolvers.ExceptionResolver;
import com.sensedia.demo.adapters.amqp.config.BindConfig;
import com.sensedia.demo.adapters.amqp.config.BrokerInput;
import com.sensedia.demo.adapters.dtos.UserCreationDto;
import com.sensedia.demo.adapters.dtos.UserDeletionDto;
import com.sensedia.demo.adapters.mappers.UserMapper;
import com.sensedia.demo.domains.User;
import com.sensedia.demo.ports.AmqpPort;
import com.sensedia.demo.ports.ApplicationPort;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;

@EnableBinding(BrokerInput.class)
public class AmqpUserAdapterInbound {

  private final ApplicationPort applicationPort;
  private final UserMapper userMapper;
  private final AmqpPort amqpPort;
  private final ExceptionResolver exceptionResolver;

  public AmqpUserAdapterInbound(
      ApplicationPort applicationPort,
      UserMapper userMapper,
      AmqpPort amqpPort,
      ExceptionResolver exceptionResolver) {
    this.applicationPort = applicationPort;
    this.userMapper = userMapper;
    this.amqpPort = amqpPort;
    this.exceptionResolver = exceptionResolver;
  }

  @StreamListener(target = BindConfig.SUBSCRIBE_USER_CREATION_REQUESTED)
  public void subscribeExchangeUserCreationRequested(UserCreationDto userCreationDto) {
    try {
      User user = userMapper.toUser(userCreationDto);
      applicationPort.create(user);
    } catch (Exception e) {
      amqpPort.notifyUserOperationError(
          exceptionResolver.solve(e).addOriginalMessage(userCreationDto));
    }
  }

  @StreamListener(target = BindConfig.SUBSCRIBE_USER_DELETION_REQUESTED)
  public void subscribeExchangeUserDeletionRequested(UserDeletionDto userDeletionDto) {
    try {
      applicationPort.delete(userDeletionDto.getId());
    } catch (Exception e) {
      amqpPort.notifyUserOperationError(
          exceptionResolver.solve(e).addOriginalMessage(userDeletionDto));
    }
  }
}
