package com.sensedia.demo.domains.search;

import com.sensedia.commons.errors.exceptions.BadRequestException;
import org.apache.commons.lang3.StringUtils;

public enum Sort {
  NAME("name"),
  EMAIL("email"),
  STATUS("status"),
  CREATION_DATE("creationDate");

  private String fieldName;

  Sort(String fieldName) {
    this.fieldName = fieldName;
  }

  public static Sort fromValue(String value) {
    if (StringUtils.isBlank(value)) return null;

    for (Sort sort : Sort.values()) {
      if (sort.name().equalsIgnoreCase(value)) {
        return sort;
      }
    }

    throw new BadRequestException(
        "Invalid sort [" + value + "], accepted values: [name, email, status, creation_date]");
  }

  public String getFieldName() {
    return fieldName;
  }

  @Override
  public String toString() {
    return this.name();
  }
}
