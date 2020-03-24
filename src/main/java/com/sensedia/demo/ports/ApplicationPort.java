package com.sensedia.demo.ports;

import com.sensedia.demo.domains.User;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public interface ApplicationPort {
  User create(@Valid @NotNull User user);

  void delete(String id);
}
