package com.sensedia.demo.adapters.repository;

import com.sensedia.demo.domains.search.UserSearch;
import com.sensedia.demo.domains.search.UserSearchResponse;

public interface AdvancedUserSearch {

  UserSearchResponse findAll(UserSearch userSearch);
}
