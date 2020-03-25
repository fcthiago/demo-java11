package com.sensedia.demo.domains.search;

import com.sensedia.commons.exceptions.BadRequestException;
import org.apache.commons.lang3.StringUtils;

public enum Sort {
  NAME("name", "name"),
  EMAIL("email", "email"),
  STATUS("status", "status"),
  CREATION_DATE("creation_date", "creationDate");

  private String value;
  private String fieldName;

  Sort(String value, String fieldName) {
    this.value = value;
    this.fieldName = fieldName;
  }

  public static Sort fromValue(String value) {
    if (StringUtils.isBlank(value)) return null;

    for (Sort sort : Sort.values()) {
      if (sort.value.equalsIgnoreCase(value)) {
        return sort;
      }
    }

    throw new BadRequestException(
        "Invalid sort [" + value + "], accepted values: [name, email, status, creation_date]");
  }

  public String getValue() {
    return value;
  }

  public String getFieldName() {
    return fieldName;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }
}
