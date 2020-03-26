package com.sensedia.demo.domains.search;

import com.sensedia.demo.domains.User;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

public class UserSearchResponse {

  private List<User> users;
  private int total;
  private int maximumLimitPerPage;

  public UserSearchResponse(List<User> users, int total, int maximumLimitPerPage) {
    this.users = users;
    this.total = total;
    this.maximumLimitPerPage = maximumLimitPerPage;
  }

  public List<User> getUsers() {
    return users;
  }

  public int getTotal() {
    return total;
  }

  public int getMaximumLimitPerPage() {
    return maximumLimitPerPage;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("users", users)
        .append("total", total)
        .append("maximumLimitPerPage", maximumLimitPerPage)
        .toString();
  }
}
