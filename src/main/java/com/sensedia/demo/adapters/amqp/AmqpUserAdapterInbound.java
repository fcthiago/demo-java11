package com.sensedia.demo.adapters.amqp;

import com.sensedia.demo.ports.ApplicationPort;
import com.sensedia.demo.domains.User;

public class AmqpUserAdapterInbound {

  private ApplicationPort userApplication;

  public int test() {
    userApplication.create(new User());
    return 0;
  }
}
