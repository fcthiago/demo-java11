package com.sensedia.demo.it.http;

import com.fasterxml.jackson.core.type.TypeReference;
import com.sensedia.commons.errors.domains.DefaultErrorResponse;
import com.sensedia.demo.adapters.dtos.UserCreationDto;
import com.sensedia.demo.adapters.dtos.UserResponseDto;
import com.sensedia.demo.domains.User;
import com.sensedia.demo.domains.UserStatus;
import com.sensedia.demo.commons.BrokerResponse;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;

import java.io.IOException;

import static com.sensedia.commons.headers.DefaultHeader.APP_ID_HEADER_NAME;
import static com.sensedia.commons.headers.DefaultHeader.EVENT_NAME_HEADER_HEADER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
public class AmqpUserCreationTest extends AbstractUserTest {

  @BeforeEach
  public void setup() {
    repository.deleteAll();
  }

  @Test
  @DisplayName("I want to create a user with success")
  public void createUserSuccessfully() throws IOException {
    UserCreationDto userCreation = new UserCreationDto();

    userCreation.setEmail("thiago.costa@sensedia.com");
    userCreation.setName("Thiago Costa");

    Message<UserCreationDto> message =
        MessageBuilder.withPayload(userCreation).setHeader(APP_ID_HEADER_NAME, "app-test").build();

    brokerInput.subscribeUserCreationRequested().send(message);

    // DATABASE VALIDATION
    User user = repository.findAll().iterator().next();

    assertThat(isUUID(user.getId())).isTrue();
    assertThat(user.getEmail()).isEqualTo("thiago.costa@sensedia.com");
    assertThat(user.getName()).isEqualTo("Thiago Costa");
    assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
    assertThat(user.getCreationDate()).isNotNull();

    // NOTIFICATION VALIDATION
    BrokerResponse brokerResponse = collector.forChannel(brokerOutput.publishUserCreated());

    UserResponseDto userResponse = brokerResponse.getPayload(UserResponseDto.class);

    assertThat(isUUID(userResponse.getId())).isTrue();
    assertThat(userResponse.getEmail()).isEqualTo("thiago.costa@sensedia.com");
    assertThat(userResponse.getName()).isEqualTo("Thiago Costa");
    assertThat(userResponse.getStatus()).isEqualTo(UserStatus.ACTIVE.toString());
    assertThat(userResponse.getCreationDate()).isNotNull();

    MessageHeaders headers = brokerResponse.getHeaders();

    assertThat(headers.get(EVENT_NAME_HEADER_HEADER)).isEqualTo("UserCreation");
    assertThat(headers.get(APP_ID_HEADER_NAME)).isEqualTo("demo");
  }

  @Test
  @DisplayName("I want to create a user without email")
  public void createUserWithoutEmail() throws IOException {
    UserCreationDto userCreation = new UserCreationDto();
    userCreation.setName("Thiago Costa");

    Message<UserCreationDto> message =
        MessageBuilder.withPayload(userCreation).setHeader(APP_ID_HEADER_NAME, "app-test").build();

    brokerInput.subscribeUserCreationRequested().send(message);

    // RESPONSE VALIDATION
    BrokerResponse brokerResponse = collector.forChannel(brokerOutput.publishUserOperationError());

    DefaultErrorResponse<UserCreationDto> response =
        brokerResponse.getPayload(new TypeReference<DefaultErrorResponse<UserCreationDto>>() {});

    assertThat((UserCreationDto) response.getOriginalMessage()).isEqualTo(userCreation);
    assertThat(response.getStatus()).isEqualTo(BAD_REQUEST.value());
    assertThat(response.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
    assertThat(response.getDetail()).isEqualTo("email é obrigatório ou está no formato inválido.");
    assertThat(response.getType()).isNull();

    MessageHeaders headers = brokerResponse.getHeaders();

    assertThat(headers.get(EVENT_NAME_HEADER_HEADER)).isEqualTo("UserOperationError");
    assertThat(headers.get(APP_ID_HEADER_NAME)).isEqualTo("demo");

    // REPOSITORY VALIDATION
    assertThat(repository.findAll()).hasSize(0);
  }

  @Test
  @DisplayName("I want to create a user without name")
  public void createUserWithoutName() throws IOException {
    UserCreationDto userCreation = new UserCreationDto();
    userCreation.setEmail("thiago.costa@sensedia.com");

    Message<UserCreationDto> message =
        MessageBuilder.withPayload(userCreation).setHeader(APP_ID_HEADER_NAME, "app-test").build();

    brokerInput.subscribeUserCreationRequested().send(message);

    // RESPONSE VALIDATION
    BrokerResponse brokerResponse = collector.forChannel(brokerOutput.publishUserOperationError());

    DefaultErrorResponse<UserCreationDto> response =
        brokerResponse.getPayload(new TypeReference<DefaultErrorResponse<UserCreationDto>>() {});

    assertThat((UserCreationDto) response.getOriginalMessage()).isEqualTo(userCreation);
    assertThat(response.getStatus()).isEqualTo(BAD_REQUEST.value());
    assertThat(response.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
    assertThat(response.getDetail()).isEqualTo("name é obrigatório ou está no formato inválido.");
    assertThat(response.getType()).isNull();

    MessageHeaders headers = brokerResponse.getHeaders();

    assertThat(headers.get(EVENT_NAME_HEADER_HEADER)).isEqualTo("UserOperationError");
    assertThat(headers.get(APP_ID_HEADER_NAME)).isEqualTo("demo");

    // REPOSITORY VALIDATION
    assertThat(repository.findAll()).hasSize(0);
  }

  @Test
  @DisplayName("I want to create a user with invalid email")
  public void createUserWithInvalidEmail() throws IOException {
    UserCreationDto userCreation = new UserCreationDto();
    userCreation.setEmail("thiago.costa.sensedia.com");
    userCreation.setName("Thiago Costa");

    Message<UserCreationDto> message =
        MessageBuilder.withPayload(userCreation).setHeader(APP_ID_HEADER_NAME, "app-test").build();

    brokerInput.subscribeUserCreationRequested().send(message);

    // RESPONSE VALIDATION
    BrokerResponse brokerResponse = collector.forChannel(brokerOutput.publishUserOperationError());

    DefaultErrorResponse<UserCreationDto> response =
        brokerResponse.getPayload(new TypeReference<DefaultErrorResponse<UserCreationDto>>() {});

    assertThat(response.getOriginalMessage()).isEqualTo(userCreation);
    assertThat(response.getStatus()).isEqualTo(BAD_REQUEST.value());
    assertThat(response.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
    assertThat(response.getDetail()).isEqualTo("email não é um endereço de e-mail");
    assertThat(response.getType()).isNull();

    MessageHeaders headers = brokerResponse.getHeaders();

    assertThat(headers.get(EVENT_NAME_HEADER_HEADER)).isEqualTo("UserOperationError");
    assertThat(headers.get(APP_ID_HEADER_NAME)).isEqualTo("demo");

    // REPOSITORY VALIDATION
    assertThat(repository.findAll()).hasSize(0);
  }
}
