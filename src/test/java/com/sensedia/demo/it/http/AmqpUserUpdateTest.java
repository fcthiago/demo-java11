package com.sensedia.demo.it.http;

import com.fasterxml.jackson.core.type.TypeReference;
import com.sensedia.commons.errors.domains.DefaultErrorResponse;
import com.sensedia.demo.adapters.dtos.UserDto;
import com.sensedia.demo.adapters.dtos.UserUpdateDto;
import com.sensedia.demo.commons.BrokerResponse;
import com.sensedia.demo.domains.User;
import com.sensedia.demo.domains.UserStatus;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;

import java.io.IOException;
import java.time.Instant;

import static com.sensedia.commons.headers.DefaultHeader.APP_ID_HEADER_NAME;
import static com.sensedia.commons.headers.DefaultHeader.EVENT_NAME_HEADER_HEADER;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
public class AmqpUserUpdateTest extends AbstractUserTest {

  @BeforeEach
  public void setup() throws IOException {
    repository.deleteAll();
    loadDatabase();
  }

  @Test
  @DisplayName("I want to update a user with success")
  public void updateUserSuccessfully() throws IOException {
    UserUpdateDto userUpdateDto = new UserUpdateDto();

    userUpdateDto.setId(USER_ID_VALID);
    userUpdateDto.setEmail("thiago.costa@sensedia.com");
    userUpdateDto.setName("Thiago Costa");
    userUpdateDto.setStatus(UserStatus.DISABLE.name());

    Message<UserUpdateDto> message =
        MessageBuilder.withPayload(userUpdateDto).setHeader(APP_ID_HEADER_NAME, "app-test").build();

    brokerInput.subscribeUserUpdateRequested().send(message);

    // DATABASE VALIDATION
    User user = repository.findById(USER_ID_VALID).get();

    assertThat(user.getId()).isEqualTo(USER_ID_VALID);
    assertThat(user.getEmail()).isEqualTo("thiago.costa@sensedia.com");
    assertThat(user.getName()).isEqualTo("Thiago Costa");
    assertThat(user.getStatus()).isEqualTo(UserStatus.DISABLE);
    assertThat(user.getCreatedAt()).isEqualTo(Instant.parse("2020-03-23T16:09:01.035Z"));
    assertThat(user.getUpdatedAt()).isNotNull();


    // NOTIFICATION VALIDATION
    BrokerResponse brokerResponse = collector.forChannel(brokerOutput.publishUserUpdated());

    UserDto userResponse = brokerResponse.getPayload(UserDto.class);

    assertThat(userResponse.getId()).isEqualTo(USER_ID_VALID);
    assertThat(userResponse.getEmail()).isEqualTo("thiago.costa@sensedia.com");
    assertThat(userResponse.getName()).isEqualTo("Thiago Costa");
    assertThat(userResponse.getStatus()).isEqualTo(UserStatus.DISABLE.toString());
    assertThat(userResponse.getCreatedAt()).isEqualTo(Instant.parse("2020-03-23T16:09:01.035Z"));
    assertThat(userResponse.getUpdatedAt()).isNotNull();

    MessageHeaders headers = brokerResponse.getHeaders();

    assertThat(headers.get(EVENT_NAME_HEADER_HEADER)).isEqualTo("UserUpdate");
    assertThat(headers.get(APP_ID_HEADER_NAME)).isEqualTo("demo");
  }

  @Test
  @DisplayName("I want to update a user without email")
  public void updateUserWithoutEmail() throws IOException {
    UserUpdateDto userUpdateDto = new UserUpdateDto();

    userUpdateDto.setId(USER_ID_VALID);
    userUpdateDto.setName("Thiago Costa");
    userUpdateDto.setStatus(UserStatus.DISABLE.name());

    Message<UserUpdateDto> message =
        MessageBuilder.withPayload(userUpdateDto).setHeader(APP_ID_HEADER_NAME, "app-test").build();

    brokerInput.subscribeUserUpdateRequested().send(message);

    // RESPONSE VALIDATION
    BrokerResponse brokerResponse = collector.forChannel(brokerOutput.publishUserOperationError());

    DefaultErrorResponse<UserUpdateDto> response =
        brokerResponse.getPayload(new TypeReference<>() {});

    assertThat(response.getOriginalMessage()).isEqualTo(userUpdateDto);
    assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    assertThat(response.getTitle()).isEqualTo(HttpStatus.BAD_REQUEST.getReasonPhrase());
    assertThat(response.getDetail()).isEqualTo("email é obrigatório ou está no formato inválido.");
    assertThat(response.getType()).isNull();

    MessageHeaders headers = brokerResponse.getHeaders();

    assertThat(headers.get(EVENT_NAME_HEADER_HEADER)).isEqualTo("UserOperationError");
    assertThat(headers.get(APP_ID_HEADER_NAME)).isEqualTo("demo");

    // REPOSITORY VALIDATION
    assertThat(repository.findById(USER_ID_VALID).get().getStatus()).isEqualTo(UserStatus.ACTIVE);

    // NOTIFICATION VALIDATION
    assertThat(collector.forChannel(brokerOutput.publishUserUpdated())).isNull();
  }

  @Test
  @DisplayName("I want to update a user without name")
  public void updateUserWithoutName() throws IOException {
    UserUpdateDto userUpdateDto = new UserUpdateDto();

    userUpdateDto.setId(USER_ID_VALID);
    userUpdateDto.setEmail("thiago.costa@sensedia.com");
    userUpdateDto.setStatus(UserStatus.DISABLE.name());

    Message<UserUpdateDto> message =
        MessageBuilder.withPayload(userUpdateDto).setHeader(APP_ID_HEADER_NAME, "app-test").build();

    brokerInput.subscribeUserUpdateRequested().send(message);

    // RESPONSE VALIDATION
    BrokerResponse brokerResponse = collector.forChannel(brokerOutput.publishUserOperationError());

    DefaultErrorResponse<UserUpdateDto> response =
        brokerResponse.getPayload(new TypeReference<>() {});

    assertThat(response.getOriginalMessage()).isEqualTo(userUpdateDto);
    assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    assertThat(response.getTitle()).isEqualTo(HttpStatus.BAD_REQUEST.getReasonPhrase());
    assertThat(response.getDetail()).isEqualTo("name é obrigatório ou está no formato inválido.");
    assertThat(response.getType()).isNull();

    MessageHeaders headers = brokerResponse.getHeaders();

    assertThat(headers.get(EVENT_NAME_HEADER_HEADER)).isEqualTo("UserOperationError");
    assertThat(headers.get(APP_ID_HEADER_NAME)).isEqualTo("demo");

    // REPOSITORY VALIDATION
    assertThat(repository.findById(USER_ID_VALID).get().getStatus()).isEqualTo(UserStatus.ACTIVE);

    // NOTIFICATION VALIDATION
    assertThat(collector.forChannel(brokerOutput.publishUserUpdated())).isNull();
  }

  @Test
  @DisplayName("I want to update a user without status")
  public void updateUserWithoutStatus() throws IOException {
    UserUpdateDto userUpdateDto = new UserUpdateDto();

    userUpdateDto.setId(USER_ID_VALID);
    userUpdateDto.setName("Thiago Costa");
    userUpdateDto.setEmail("thiago.costa@sensedia.com");

    Message<UserUpdateDto> message =
        MessageBuilder.withPayload(userUpdateDto).setHeader(APP_ID_HEADER_NAME, "app-test").build();

    brokerInput.subscribeUserUpdateRequested().send(message);

    // RESPONSE VALIDATION
    BrokerResponse brokerResponse = collector.forChannel(brokerOutput.publishUserOperationError());

    DefaultErrorResponse<UserUpdateDto> response =
        brokerResponse.getPayload(new TypeReference<>() {});

    assertThat(response.getOriginalMessage()).isEqualTo(userUpdateDto);
    assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    assertThat(response.getTitle()).isEqualTo(HttpStatus.BAD_REQUEST.getReasonPhrase());
    assertThat(response.getDetail()).isEqualTo("status é obrigatório ou está no formato inválido.");
    assertThat(response.getType()).isNull();

    MessageHeaders headers = brokerResponse.getHeaders();

    assertThat(headers.get(EVENT_NAME_HEADER_HEADER)).isEqualTo("UserOperationError");
    assertThat(headers.get(APP_ID_HEADER_NAME)).isEqualTo("demo");

    // REPOSITORY VALIDATION
    assertThat(repository.findById(USER_ID_VALID).get().getStatus()).isEqualTo(UserStatus.ACTIVE);

    // NOTIFICATION VALIDATION
    assertThat(collector.forChannel(brokerOutput.publishUserUpdated())).isNull();
  }

  @Test
  @DisplayName("I want to update a user without id")
  public void updateUserWithoutId() throws IOException {
    UserUpdateDto userUpdateDto = new UserUpdateDto();

    userUpdateDto.setName("Thiago Costa");
    userUpdateDto.setEmail("thiago.costa@sensedia.com");
    userUpdateDto.setStatus(UserStatus.DISABLE.name());

    Message<UserUpdateDto> message =
        MessageBuilder.withPayload(userUpdateDto).setHeader(APP_ID_HEADER_NAME, "app-test").build();

    brokerInput.subscribeUserUpdateRequested().send(message);

    // RESPONSE VALIDATION
    BrokerResponse brokerResponse = collector.forChannel(brokerOutput.publishUserOperationError());

    DefaultErrorResponse<UserUpdateDto> response =
        brokerResponse.getPayload(new TypeReference<>() {});

    assertThat(response.getOriginalMessage()).isEqualTo(userUpdateDto);
    assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    assertThat(response.getTitle()).isEqualTo(HttpStatus.BAD_REQUEST.getReasonPhrase());
    assertThat(response.getDetail()).isEqualTo("id é obrigatório ou está no formato inválido.");
    assertThat(response.getType()).isNull();

    MessageHeaders headers = brokerResponse.getHeaders();

    assertThat(headers.get(EVENT_NAME_HEADER_HEADER)).isEqualTo("UserOperationError");
    assertThat(headers.get(APP_ID_HEADER_NAME)).isEqualTo("demo");

    // REPOSITORY VALIDATION
    assertThat(repository.findById(USER_ID_VALID).get().getStatus()).isEqualTo(UserStatus.ACTIVE);

    // NOTIFICATION VALIDATION
    assertThat(collector.forChannel(brokerOutput.publishUserUpdated())).isNull();
  }

  @Test
  @DisplayName("I want to update a user with invalid status")
  public void updateUserWithInvalidStatus() throws IOException {
    UserUpdateDto userUpdateDto = new UserUpdateDto();

    userUpdateDto.setId(USER_ID_VALID);
    userUpdateDto.setName("Thiago Costa");
    userUpdateDto.setEmail("thiago.costa@sensedia.com");
    userUpdateDto.setStatus("ERROR");

    Message<UserUpdateDto> message =
        MessageBuilder.withPayload(userUpdateDto).setHeader(APP_ID_HEADER_NAME, "app-test").build();

    brokerInput.subscribeUserUpdateRequested().send(message);

    // RESPONSE VALIDATION
    BrokerResponse brokerResponse = collector.forChannel(brokerOutput.publishUserOperationError());

    DefaultErrorResponse<UserUpdateDto> response =
        brokerResponse.getPayload(new TypeReference<>() {});

    assertThat(response.getOriginalMessage()).isEqualTo(userUpdateDto);
    assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    assertThat(response.getTitle()).isEqualTo(HttpStatus.BAD_REQUEST.getReasonPhrase());
    assertThat(response.getDetail())
        .isEqualTo("Invalid status [ERROR], accepted values: [active, disable]");
    assertThat(response.getType()).isNull();

    MessageHeaders headers = brokerResponse.getHeaders();

    assertThat(headers.get(EVENT_NAME_HEADER_HEADER)).isEqualTo("UserOperationError");
    assertThat(headers.get(APP_ID_HEADER_NAME)).isEqualTo("demo");

    // REPOSITORY VALIDATION
    assertThat(repository.findById(USER_ID_VALID).get().getStatus()).isEqualTo(UserStatus.ACTIVE);

    // NOTIFICATION VALIDATION
    assertThat(collector.forChannel(brokerOutput.publishUserUpdated())).isNull();
  }

  @Test
  @DisplayName("I want to update a user with invalid email")
  public void updateUserWithInvalidEmail() throws IOException {
    UserUpdateDto userUpdateDto = new UserUpdateDto();

    userUpdateDto.setId(USER_ID_VALID);
    userUpdateDto.setName("Thiago Costa");
    userUpdateDto.setEmail("thiago.costa.sensedia.com");
    userUpdateDto.setStatus(UserStatus.DISABLE.name());

    Message<UserUpdateDto> message =
        MessageBuilder.withPayload(userUpdateDto).setHeader(APP_ID_HEADER_NAME, "app-test").build();

    brokerInput.subscribeUserUpdateRequested().send(message);

    // RESPONSE VALIDATION
    BrokerResponse brokerResponse = collector.forChannel(brokerOutput.publishUserOperationError());

    DefaultErrorResponse<UserUpdateDto> response =
        brokerResponse.getPayload(new TypeReference<>() {});

    assertThat(response.getOriginalMessage()).isEqualTo(userUpdateDto);
    assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    assertThat(response.getTitle()).isEqualTo(HttpStatus.BAD_REQUEST.getReasonPhrase());
    assertThat(response.getDetail()).isEqualTo("email deve ser um endereço de e-mail bem formado");
    assertThat(response.getType()).isNull();

    MessageHeaders headers = brokerResponse.getHeaders();

    assertThat(headers.get(EVENT_NAME_HEADER_HEADER)).isEqualTo("UserOperationError");
    assertThat(headers.get(APP_ID_HEADER_NAME)).isEqualTo("demo");

    // REPOSITORY VALIDATION
    assertThat(repository.findById(USER_ID_VALID).get().getStatus()).isEqualTo(UserStatus.ACTIVE);

    // NOTIFICATION VALIDATION
    assertThat(collector.forChannel(brokerOutput.publishUserUpdated())).isNull();
  }

  @Test
  @DisplayName("I want to update a user that does not exist")
  public void updateUserThatDoesNotExist() throws IOException {
    UserUpdateDto userUpdateDto = new UserUpdateDto();

    userUpdateDto.setId(USER_ID_NOT_FOUND);
    userUpdateDto.setName("Thiago Costa");
    userUpdateDto.setEmail("thiago.costa@sensedia.com");
    userUpdateDto.setStatus(UserStatus.DISABLE.name());

    Message<UserUpdateDto> message =
        MessageBuilder.withPayload(userUpdateDto).setHeader(APP_ID_HEADER_NAME, "app-test").build();

    brokerInput.subscribeUserUpdateRequested().send(message);

    // RESPONSE VALIDATION
    BrokerResponse brokerResponse = collector.forChannel(brokerOutput.publishUserOperationError());

    DefaultErrorResponse<UserUpdateDto> response =
        brokerResponse.getPayload(new TypeReference<>() {});

    assertThat(response.getOriginalMessage()).isEqualTo(userUpdateDto);
    assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    assertThat(response.getTitle()).isEqualTo(HttpStatus.NOT_FOUND.getReasonPhrase());
    assertThat(response.getDetail()).isEqualTo("User not found");
    assertThat(response.getType()).isNull();

    MessageHeaders headers = brokerResponse.getHeaders();

    assertThat(headers.get(EVENT_NAME_HEADER_HEADER)).isEqualTo("UserOperationError");
    assertThat(headers.get(APP_ID_HEADER_NAME)).isEqualTo("demo");

    // NOTIFICATION VALIDATION
    assertThat(collector.forChannel(brokerOutput.publishUserUpdated())).isNull();
  }
}
