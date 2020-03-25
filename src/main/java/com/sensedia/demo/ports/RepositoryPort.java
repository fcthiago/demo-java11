package com.sensedia.demo.ports;

import com.sensedia.demo.adapters.repository.AdvancedUserSearch;
import com.sensedia.demo.domains.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositoryPort extends CrudRepository<User, String>, AdvancedUserSearch {}
