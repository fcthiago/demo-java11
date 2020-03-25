package com.sensedia.demo.domains;

import com.sensedia.commons.exceptions.BadRequestException;
import org.apache.commons.lang3.StringUtils;

public enum UserStatus {
  ACTIVE,
  DISABLE;

  public static UserStatus fromValue(String value) {
    if (StringUtils.isBlank(value)) return null;

    for (UserStatus status : UserStatus.values()) {
      if (status.name().equalsIgnoreCase(value)) {
        return status;
      }
    }

    throw new BadRequestException(
        "Invalid status [" + value + "], accepted values: [active, disable]");
  }
}
