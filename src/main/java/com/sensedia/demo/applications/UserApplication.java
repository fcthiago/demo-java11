package com.sensedia.demo.applications;

import com.sensedia.commons.errors.exceptions.NotFoundException;
import com.sensedia.demo.domains.User;
import com.sensedia.demo.domains.UserStatus;
import com.sensedia.demo.domains.search.UserSearch;
import com.sensedia.demo.domains.search.UserSearchResponse;
import com.sensedia.demo.ports.AmqpPort;
import com.sensedia.demo.ports.ApplicationPort;
import com.sensedia.demo.ports.RepositoryPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.Instant;

@Service
@Transactional
@Validated
public class UserApplication implements ApplicationPort {

  private final AmqpPort amqpPort;
  private final RepositoryPort repository;

  @Autowired
  public UserApplication(AmqpPort amqpPort, RepositoryPort repository) {
    this.amqpPort = amqpPort;
    this.repository = repository;
  }

  @Override
  public User create(@Valid @NotNull User user) {
    user.setCreationDate(Instant.now());
    user.setStatus(UserStatus.ACTIVE);

    repository.save(user);
    amqpPort.notifyUserCreation(user);

    return user;
  }

  @Override
  public void delete(@NotNull String id) {
    User user = findById(id);

    repository.delete(user);
    amqpPort.notifyUserDeletion(user);
  }

  @Override
  public User update(@Valid @NotNull User userForUpdate, String id) {
    User user = findById(id);

    user.setName(userForUpdate.getName());
    user.setEmail(userForUpdate.getEmail());

    if (userForUpdate.getStatus() != null) user.setStatus(userForUpdate.getStatus());

    repository.save(user);
    amqpPort.notifyUserUpdate(user);

    return user;
  }

  @Override
  public User findById(@NotNull String id) {
    return repository
        .findById(id)
        .orElseGet(
            () -> {
              throw new NotFoundException("User not found");
            });
  }

  @Override
  public UserSearchResponse findAll(@Valid @NotNull UserSearch userSearch) {
    return repository.findAll(userSearch);
  }
}
