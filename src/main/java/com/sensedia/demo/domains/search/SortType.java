package com.sensedia.demo.domains.search;

import com.sensedia.commons.errors.exceptions.BadRequestException;
import org.apache.commons.lang3.StringUtils;

public enum SortType {
  ASC("asc"),
  DESC("desc");

  private String value;

  SortType(String value) {
    this.value = value;
  }

  public static SortType fromValue(String value) {
    if (StringUtils.isBlank(value)) return null;

    for (SortType sortType : SortType.values()) {
      if (sortType.name().equalsIgnoreCase(value)) {
        return sortType;
      }
    }

    throw new BadRequestException(
        "Invalid sort type [" + value + "], accepted values: [asc, desc]");
  }

  public String getValue() {
    return value;
  }
}
