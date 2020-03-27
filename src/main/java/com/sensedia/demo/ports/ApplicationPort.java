package com.sensedia.demo.ports;

import com.sensedia.demo.domains.User;
import com.sensedia.demo.domains.search.UserSearch;
import com.sensedia.demo.domains.search.UserSearchResponse;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public interface ApplicationPort {
  User create(@Valid @NotNull User user);

  void delete(@NotNull String id);

  User update(@Valid @NotNull User user, String id);

  User findById(@NotNull String id);

  UserSearchResponse findAll(@Valid @NotNull UserSearch userSearch);
}
