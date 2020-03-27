package com.sensedia.demo.it.http;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoTimeoutException;
import com.sensedia.demo.adapters.amqp.config.BrokerInput;
import com.sensedia.demo.adapters.amqp.config.BrokerOutput;
import com.sensedia.demo.applications.UserApplication;
import com.sensedia.demo.domains.User;
import com.sensedia.demo.ports.RepositoryPort;
import com.sensedia.demo.commons.MessageCollectorCustom;
import io.micrometer.core.instrument.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.Resource;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public abstract class AbstractUserTest {

  static final String USER_ID_VALID = "620dac34-15e1-4375-8be8-e9a46d1f5a36";

  static final String USER_ID_NOT_FOUND = "not-found";

  @Autowired RepositoryPort repository;

  @Autowired ObjectMapper mapper;

  @Autowired TestRestTemplate request;

  @Autowired MessageCollectorCustom collector;

  @Autowired BrokerOutput brokerOutput;

  @Autowired BrokerInput brokerInput;

  @Autowired private UserApplication userApplication;

  @Value("classpath:users.json")
  private Resource usersJson;

  void loadDatabase() throws IOException {
    String json = loadData(usersJson);
    List<User> users = mapper.readValue(json, new TypeReference<List<User>>() {});
    repository.saveAll(users);
  }

  String loadData(Resource resource) throws IOException {
    return IOUtils.toString(resource.getInputStream(), StandardCharsets.UTF_8);
  }

  boolean isUUID(String uuid) {
    return uuid.matches(
        "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");
  }

  void injectDatabaseError() {
    RepositoryPort failingRepository = mock(RepositoryPort.class);
    ReflectionTestUtils.setField(userApplication, "repository", failingRepository);
    when(failingRepository.save(any())).thenThrow(new MongoTimeoutException("database error"));
    when(failingRepository.findAll(any())).thenThrow(new MongoTimeoutException("database error"));
  }

  void undoDatabaseError() {
    ReflectionTestUtils.setField(userApplication, "repository", repository);
  }
}
