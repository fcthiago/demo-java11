package com.sensedia.demo.adapters.mappers;

import com.sensedia.demo.adapters.dtos.UserUpdateDto;
import com.sensedia.demo.domains.User;
import com.sensedia.demo.domains.UserStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public abstract class UserDecoratorMapper implements UserMapper {

  @Autowired
  @Qualifier("delegate")
  private UserMapper delegate;

  @Override
  public User toUser(UserUpdateDto userUpdateDto) {
    validateStatus(userUpdateDto);
    return delegate.toUser(userUpdateDto);
  }

  private void validateStatus(UserUpdateDto userUpdateDto) {
    UserStatus.fromValue(userUpdateDto.getStatus());
  }
}
