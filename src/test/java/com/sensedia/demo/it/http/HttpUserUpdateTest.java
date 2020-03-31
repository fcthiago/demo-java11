package com.sensedia.demo.it.http;

import com.sensedia.commons.errors.domains.DefaultErrorResponse;
import com.sensedia.demo.adapters.dtos.UserDto;
import com.sensedia.demo.adapters.dtos.UserUpdateDto;
import com.sensedia.demo.commons.BrokerResponse;
import com.sensedia.demo.domains.User;
import com.sensedia.demo.domains.UserStatus;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
public class HttpUserUpdateTest extends AbstractUserTest {

  @BeforeEach
  public void setup() throws IOException {
    repository.deleteAll();
    loadDatabase();
  }

  @Test
  @DisplayName("I want to update a user with success")
  public void updateUserSuccessfully() throws IOException {
    UserUpdateDto userUpdateDto = new UserUpdateDto();

    userUpdateDto.setEmail("thiago.costa@sensedia.com");
    userUpdateDto.setName("Thiago Costa");
    userUpdateDto.setStatus(UserStatus.DISABLE.name());

    ResponseEntity<UserDto> response =
        request.exchange(
            "/users/{id}",
            HttpMethod.PUT,
            new HttpEntity<>(userUpdateDto),
            UserDto.class,
            USER_ID_VALID);

    // RESPONSE VALIDATION
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    UserDto userResponse = response.getBody();

    assertThat(userResponse.getId()).isEqualTo(USER_ID_VALID);
    assertThat(userResponse.getEmail()).isEqualTo("thiago.costa@sensedia.com");
    assertThat(userResponse.getName()).isEqualTo("Thiago Costa");
    assertThat(userResponse.getStatus()).isEqualTo(UserStatus.DISABLE.toString());
    assertThat(userResponse.getCreatedAt()).isEqualTo(Instant.parse("2020-03-23T16:09:01.035Z"));
    assertThat(userResponse.getUpdatedAt()).isNotNull();

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

    userResponse = brokerResponse.getPayload(UserDto.class);

    assertThat(userResponse.getId()).isEqualTo(USER_ID_VALID);
    assertThat(userResponse.getEmail()).isEqualTo("thiago.costa@sensedia.com");
    assertThat(userResponse.getName()).isEqualTo("Thiago Costa");
    assertThat(userResponse.getStatus()).isEqualTo(UserStatus.DISABLE.toString());
    assertThat(userResponse.getCreatedAt()).isEqualTo(Instant.parse("2020-03-23T16:09:01.035Z"));
    assertThat(userResponse.getUpdatedAt()).isNotNull();

    assertThat(brokerResponse.getHeaders().get("event_name")).isEqualTo("UserUpdate");
  }

  @Test
  @DisplayName("I want to update a user without email")
  public void updateUserWithoutEmail() {
    UserUpdateDto userUpdateDto = new UserUpdateDto();

    userUpdateDto.setName("Thiago Costa");
    userUpdateDto.setStatus(UserStatus.DISABLE.name());

    ResponseEntity<DefaultErrorResponse> response =
        request.exchange(
            "/users/{id}",
            HttpMethod.PUT,
            new HttpEntity<>(userUpdateDto),
            DefaultErrorResponse.class,
            USER_ID_VALID);

    // RESPONSE VALIDATION
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    assertThat(response.getBody().getTitle()).isEqualTo(HttpStatus.BAD_REQUEST.getReasonPhrase());
    assertThat(response.getBody().getDetail())
        .isEqualTo("email é obrigatório ou está no formato inválido.");
    assertThat(response.getBody().getType()).isNull();

    assertThat(repository.findById(USER_ID_VALID).get().getStatus()).isEqualTo(UserStatus.ACTIVE);
    assertThat(collector.forChannel(brokerOutput.publishUserUpdated())).isNull();
  }

  @Test
  @DisplayName("I want to update a user without name")
  public void updateUserWithoutName() {
    UserUpdateDto userUpdateDto = new UserUpdateDto();

    userUpdateDto.setEmail("thiago.costa@sensedia.com");
    userUpdateDto.setStatus(UserStatus.DISABLE.name());

    ResponseEntity<DefaultErrorResponse> response =
        request.exchange(
            "/users/{id}",
            HttpMethod.PUT,
            new HttpEntity<>(userUpdateDto),
            DefaultErrorResponse.class,
            USER_ID_VALID);

    // RESPONSE VALIDATION
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    assertThat(response.getBody().getTitle()).isEqualTo(HttpStatus.BAD_REQUEST.getReasonPhrase());
    assertThat(response.getBody().getDetail())
        .isEqualTo("name é obrigatório ou está no formato inválido.");
    assertThat(response.getBody().getType()).isNull();

    assertThat(repository.findById(USER_ID_VALID).get().getStatus()).isEqualTo(UserStatus.ACTIVE);
    assertThat(collector.forChannel(brokerOutput.publishUserUpdated())).isNull();
  }

  @Test
  @DisplayName("I want to update a user without status")
  public void updateUserWithoutStatus() {
    UserUpdateDto userUpdateDto = new UserUpdateDto();

    userUpdateDto.setName("Thiago Costa");
    userUpdateDto.setEmail("thiago.costa@sensedia.com");

    ResponseEntity<DefaultErrorResponse> response =
        request.exchange(
            "/users/{id}",
            HttpMethod.PUT,
            new HttpEntity<>(userUpdateDto),
            DefaultErrorResponse.class,
            USER_ID_VALID);

    // RESPONSE VALIDATION
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    assertThat(response.getBody().getTitle()).isEqualTo(HttpStatus.BAD_REQUEST.getReasonPhrase());
    assertThat(response.getBody().getDetail())
        .isEqualTo("status é obrigatório ou está no formato inválido.");
    assertThat(response.getBody().getType()).isNull();

    assertThat(repository.findById(USER_ID_VALID).get().getStatus()).isEqualTo(UserStatus.ACTIVE);
    assertThat(collector.forChannel(brokerOutput.publishUserUpdated())).isNull();
  }

  @Test
  @DisplayName("I want to update a user with invalid status")
  public void updateUserWithInvalidStatus() {
    UserUpdateDto userUpdateDto = new UserUpdateDto();

    userUpdateDto.setName("Thiago Costa");
    userUpdateDto.setEmail("thiago.costa@sensedia.com");
    userUpdateDto.setStatus("ERROR");

    ResponseEntity<DefaultErrorResponse> response =
        request.exchange(
            "/users/{id}",
            HttpMethod.PUT,
            new HttpEntity<>(userUpdateDto),
            DefaultErrorResponse.class,
            USER_ID_VALID);

    // RESPONSE VALIDATION
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    assertThat(response.getBody().getTitle()).isEqualTo(HttpStatus.BAD_REQUEST.getReasonPhrase());
    assertThat(response.getBody().getDetail())
        .isEqualTo("Invalid status [ERROR], accepted values: [active, disable]");
    assertThat(response.getBody().getType()).isNull();

    assertThat(repository.findById(USER_ID_VALID).get().getStatus()).isEqualTo(UserStatus.ACTIVE);
    assertThat(collector.forChannel(brokerOutput.publishUserUpdated())).isNull();
  }

  @Test
  @DisplayName("I want to update a user with invalid email")
  public void updateUserWithInvalidEmail() {
    UserUpdateDto userUpdateDto = new UserUpdateDto();

    userUpdateDto.setName("Thiago Costa");
    userUpdateDto.setEmail("thiago.costa.sensedia.com");
    userUpdateDto.setStatus(UserStatus.DISABLE.name());

    ResponseEntity<DefaultErrorResponse> response =
        request.exchange(
            "/users/{id}",
            HttpMethod.PUT,
            new HttpEntity<>(userUpdateDto),
            DefaultErrorResponse.class,
            USER_ID_VALID);

    // RESPONSE VALIDATION
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    assertThat(response.getBody().getTitle()).isEqualTo(HttpStatus.BAD_REQUEST.getReasonPhrase());
    assertThat(response.getBody().getDetail()).isEqualTo("email não é um endereço de e-mail");
    assertThat(response.getBody().getType()).isNull();

    assertThat(repository.findById(USER_ID_VALID).get().getStatus()).isEqualTo(UserStatus.ACTIVE);
    assertThat(collector.forChannel(brokerOutput.publishUserUpdated())).isNull();
  }

  @Test
  @DisplayName("I want to update a user that does not exist")
  public void updateUserThatDoesNotExist() {
    UserUpdateDto userUpdateDto = new UserUpdateDto();

    userUpdateDto.setName("Thiago Costa");
    userUpdateDto.setEmail("thiago.costa@sensedia.com");
    userUpdateDto.setStatus(UserStatus.DISABLE.name());

    ResponseEntity<DefaultErrorResponse> response =
        request.exchange(
            "/users/{id}",
            HttpMethod.PUT,
            new HttpEntity<>(userUpdateDto),
            DefaultErrorResponse.class,
            USER_ID_NOT_FOUND);

    // RESPONSE VALIDATION
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(response.getBody().getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    assertThat(response.getBody().getTitle()).isEqualTo(HttpStatus.NOT_FOUND.getReasonPhrase());
    assertThat(response.getBody().getDetail()).isEqualTo("User not found");
    assertThat(response.getBody().getType()).isNull();

    assertThat(collector.forChannel(brokerOutput.publishUserUpdated())).isNull();
  }
}
