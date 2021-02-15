package com.sensedia.demo.it.http;

import com.sensedia.commons.errors.domains.DefaultErrorResponse;
import com.sensedia.demo.adapters.dtos.UserCreationDto;
import com.sensedia.demo.adapters.dtos.UserDto;
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

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
public class HttpUserCreationTest extends AbstractUserTest {

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

    ResponseEntity<UserDto> response =
        request.exchange("/users", HttpMethod.POST, new HttpEntity<>(userCreation), UserDto.class);

    // RESPONSE VALIDATION
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    UserDto userResponse = response.getBody();

    assertThat(isUUID(userResponse.getId())).isTrue();
    assertThat(userResponse.getEmail()).isEqualTo("thiago.costa@sensedia.com");
    assertThat(userResponse.getName()).isEqualTo("Thiago Costa");
    assertThat(userResponse.getStatus()).isEqualTo(UserStatus.ACTIVE.toString());
    assertThat(userResponse.getCreatedAt()).isNotNull();
    assertThat(userResponse.getUpdatedAt()).isNull();

    // DATABASE VALIDATION
    User user = repository.findAll().iterator().next();

    assertThat(isUUID(user.getId())).isTrue();
    assertThat(user.getEmail()).isEqualTo("thiago.costa@sensedia.com");
    assertThat(user.getName()).isEqualTo("Thiago Costa");
    assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
    assertThat(user.getCreatedAt()).isNotNull();
    assertThat(user.getUpdatedAt()).isNull();

    // NOTIFICATION VALIDATION
    BrokerResponse brokerResponse = collector.forChannel(brokerOutput.publishUserCreated());

    userResponse = brokerResponse.getPayload(UserDto.class);

    assertThat(isUUID(userResponse.getId())).isTrue();
    assertThat(userResponse.getEmail()).isEqualTo("thiago.costa@sensedia.com");
    assertThat(userResponse.getName()).isEqualTo("Thiago Costa");
    assertThat(userResponse.getStatus()).isEqualTo(UserStatus.ACTIVE.toString());
    assertThat(userResponse.getCreatedAt()).isNotNull();
    assertThat(userResponse.getUpdatedAt()).isNull();

    assertThat(brokerResponse.getHeaders().get("event_name")).isEqualTo("UserCreation");
  }

  @Test
  @DisplayName("I want to create a user without email")
  public void createUserWithoutEmail() {
    UserCreationDto userCreation = new UserCreationDto();
    userCreation.setName("Thiago Costa");

    ResponseEntity<DefaultErrorResponse> response =
        request.exchange(
            "/users", HttpMethod.POST, new HttpEntity<>(userCreation), DefaultErrorResponse.class);

    // RESPONSE VALIDATION
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    assertThat(response.getBody().getTitle()).isEqualTo(HttpStatus.BAD_REQUEST.getReasonPhrase());
    assertThat(response.getBody().getDetail())
        .isEqualTo("email é obrigatório ou está no formato inválido.");
    assertThat(response.getBody().getType()).isNull();

    assertThat(repository.findAll()).hasSize(0);
    assertThat(collector.forChannel(brokerOutput.publishUserCreated())).isNull();
  }

  @Test
  @DisplayName("I want to create a user without name")
  public void createUserWithoutName() {
    UserCreationDto userCreation = new UserCreationDto();
    userCreation.setEmail("thiago.costa@sensedia.com");

    ResponseEntity<DefaultErrorResponse> response =
        request.exchange(
            "/users", HttpMethod.POST, new HttpEntity<>(userCreation), DefaultErrorResponse.class);

    // RESPONSE VALIDATION
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    assertThat(response.getBody().getTitle()).isEqualTo(HttpStatus.BAD_REQUEST.getReasonPhrase());
    assertThat(response.getBody().getDetail())
        .isEqualTo("name é obrigatório ou está no formato inválido.");
    assertThat(response.getBody().getType()).isNull();

    assertThat(repository.findAll()).hasSize(0);
    assertThat(collector.forChannel(brokerOutput.publishUserCreated())).isNull();
  }

  @Test
  @DisplayName("I want to create a user with invalid email")
  public void createUserWithInvalidEmail() throws IOException {
    UserCreationDto userCreation = new UserCreationDto();
    userCreation.setEmail("thiago.costa.sensedia.com");
    userCreation.setName("Thiago Costa");

    ResponseEntity<DefaultErrorResponse> response =
        request.exchange(
            "/users", HttpMethod.POST, new HttpEntity<>(userCreation), DefaultErrorResponse.class);

    // RESPONSE VALIDATION
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    assertThat(response.getBody().getTitle()).isEqualTo(HttpStatus.BAD_REQUEST.getReasonPhrase());
    assertThat(response.getBody().getDetail()).isEqualTo("email deve ser um endereço de e-mail bem formado");
    assertThat(response.getBody().getType()).isNull();

    assertThat(repository.findAll()).hasSize(0);
    assertThat(collector.forChannel(brokerOutput.publishUserCreated())).isNull();
  }
}
