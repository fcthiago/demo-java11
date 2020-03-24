package com.sensedia.demo.ports;

import com.sensedia.demo.adapters.dtos.UserResponseDto;
import com.sensedia.demo.domains.User;

public interface AmqpPort {

  void notifyUserCreation(User user);

  void notifyUserDeletion(User user);
}
