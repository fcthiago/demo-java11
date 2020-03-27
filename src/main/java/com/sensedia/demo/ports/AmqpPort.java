package com.sensedia.demo.ports;

import com.sensedia.commons.errors.domains.DefaultErrorResponse;
import com.sensedia.demo.domains.User;

public interface AmqpPort {

  void notifyUserCreation(User user);

  void notifyUserDeletion(User user);

  void notifyUserOperationError(DefaultErrorResponse errorResponse);
}
