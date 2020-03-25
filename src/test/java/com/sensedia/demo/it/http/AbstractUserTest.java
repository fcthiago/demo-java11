package com.sensedia.demo.it.http;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sensedia.demo.adapters.amqp.config.BrokerOutput;
import com.sensedia.demo.domains.User;
import com.sensedia.demo.ports.RepositoryPort;
import io.micrometer.core.instrument.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.cloud.stream.test.binder.MessageCollector;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

public abstract class AbstractUserTest {

  static final String USER_ID_VALID = "620dac34-15e1-4375-8be8-e9a46d1f5a36";

  static final String USER_ID_NOT_FOUND = "not-found";

  @Autowired RepositoryPort repository;

  @Autowired ObjectMapper mapper;

  @Autowired TestRestTemplate request;

  @Autowired MessageCollector collector;

  @Autowired BrokerOutput output;

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
}
