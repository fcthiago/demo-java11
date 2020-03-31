package com.sensedia.demo.domains.search;

import com.sensedia.demo.domains.UserStatus;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.Instant;

@Valid
public class UserSearch {

  private String name;
  private String email;
  private UserStatus status;

  @NotNull
  @Min(1)
  private Integer page;

  @Min(1)
  private Integer limit;

  private Instant createdAtStart;
  private Instant createdAtEnd;

  private Sort sort;
  private SortType sortType;

  public String getName() {
    return name;
  }

  void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  void setEmail(String email) {
    this.email = email;
  }

  public UserStatus getStatus() {
    return status;
  }

  void setStatus(UserStatus status) {
    this.status = status;
  }

  public Integer getPage() {
    return page;
  }

  void setPage(Integer page) {
    this.page = page;
  }

  public Integer getLimit() {
    return limit;
  }

  void setLimit(Integer limit) {
    this.limit = limit;
  }

  public Instant getCreatedAtStart() {
    return createdAtStart;
  }

  void setCreatedAtStart(Instant createdAtStart) {
    this.createdAtStart = createdAtStart;
  }

  public Instant getCreatedAtEnd() {
    return createdAtEnd;
  }

  void setCreatedAtEnd(Instant createdAtEnd) {
    this.createdAtEnd = createdAtEnd;
  }

  public Sort getSort() {
    return sort;
  }

  void setSort(Sort sort) {
    this.sort = sort;
  }

  public SortType getSortType() {
    return sortType;
  }

  void setSortType(SortType sortType) {
    this.sortType = sortType;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("name", name)
        .append("email", email)
        .append("status", status)
        .append("page", page)
        .append("limit", limit)
        .append("createDateStart", createdAtStart)
        .append("createDateEnd", createdAtEnd)
        .append("sort", sort)
        .append("sortType", sortType)
        .toString();
  }
}
