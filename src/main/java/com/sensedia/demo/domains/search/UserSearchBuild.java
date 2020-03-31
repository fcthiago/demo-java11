package com.sensedia.demo.domains.search;

import com.sensedia.demo.domains.UserStatus;

import java.time.Instant;

public class UserSearchBuild {

  private UserSearchBuild() {}

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private UserSearch userSearch;

    private Builder() {
      userSearch = new UserSearch();
    }

    public Builder page(Integer page) {
      userSearch.setPage(page);
      return this;
    }

    public Builder limit(Integer limit) {
      userSearch.setLimit(limit);
      return this;
    }

    public Builder name(String name) {
      userSearch.setName(name);
      return this;
    }

    public Builder email(String email) {
      userSearch.setEmail(email);
      return this;
    }

    public Builder createdAtStart(Instant createDateStart) {
      userSearch.setCreatedAtStart(createDateStart);
      return this;
    }

    public Builder createdAtEnd(Instant createDateEnd) {
      userSearch.setCreatedAtEnd(createDateEnd);
      return this;
    }

    public Builder sort(String sort) {
      userSearch.setSort(Sort.fromValue(sort));
      return this;
    }

    public Builder sortType(String sortType) {
      userSearch.setSortType(SortType.fromValue(sortType));
      return this;
    }

    public Builder status(String status) {
      userSearch.setStatus(UserStatus.fromValue(status));
      return this;
    }

    public UserSearch build() {
      return userSearch;
    }
  }
}
