package com.sensedia.demo.domains.search;

import com.sensedia.demo.domains.UserStatus;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Objects;

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

  private Instant createDateStart;
  private Instant createDateEnd;

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

  public Instant getCreateDateStart() {
    return createDateStart;
  }

  void setCreateDateStart(Instant createDateStart) {
    this.createDateStart = createDateStart;
  }

  public Instant getCreateDateEnd() {
    return createDateEnd;
  }

  void setCreateDateEnd(Instant createDateEnd) {
    this.createDateEnd = createDateEnd;
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
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    UserSearch that = (UserSearch) o;
    return Objects.equals(name, that.name)
        && Objects.equals(email, that.email)
        && status == that.status
        && Objects.equals(page, that.page)
        && Objects.equals(limit, that.limit)
        && Objects.equals(createDateStart, that.createDateStart)
        && Objects.equals(createDateEnd, that.createDateEnd)
        && sort == that.sort
        && sortType == that.sortType;
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        name, email, status, page, limit, createDateStart, createDateEnd, sort, sortType);
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("name", name)
        .append("email", email)
        .append("status", status)
        .append("page", page)
        .append("limit", limit)
        .append("createDateStart", createDateStart)
        .append("createDateEnd", createDateEnd)
        .append("sort", sort)
        .append("sortType", sortType)
        .toString();
  }
}
