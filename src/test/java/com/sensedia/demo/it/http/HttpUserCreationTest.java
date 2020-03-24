package com.sensedia.demo.it.http;

import com.sensedia.commons.exceptions.DefaultErrorResponse;
import com.sensedia.demo.adapters.dtos.UserCreationDto;
import com.sensedia.demo.adapters.dtos.UserResponseDto;
import com.sensedia.demo.domains.User;
import com.sensedia.demo.domains.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
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

    ResponseEntity<UserResponseDto> response =
        request.exchange(
            "/users", HttpMethod.POST, new HttpEntity<>(userCreation), UserResponseDto.class);

    // RESPONSE VALIDATION
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    UserResponseDto userResponse = response.getBody();

    assertThat(isUUID(userResponse.getId())).isTrue();
    assertThat(userResponse.getEmail()).isEqualTo("thiago.costa@sensedia.com");
    assertThat(userResponse.getName()).isEqualTo("Thiago Costa");
    assertThat(userResponse.getStatus()).isEqualTo(UserStatus.ACTIVE.toString());
    assertThat(userResponse.getCreationDate()).isNotNull();

    // DATABASE VALIDATION
    User user = repository.findAll().iterator().next();

    assertThat(isUUID(user.getId())).isTrue();
    assertThat(user.getEmail()).isEqualTo("thiago.costa@sensedia.com");
    assertThat(user.getName()).isEqualTo("Thiago Costa");
    assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
    assertThat(user.getCreationDate()).isNotNull();

    // NOTIFICATION VALIDATION
    Message<?> poll = collector.forChannel(output.publishCreatedUser()).poll();
    Object payload = poll.getPayload();
    MessageHeaders headers = poll.getHeaders();

    userResponse = mapper.readValue(payload.toString(), UserResponseDto.class);

    assertThat(isUUID(userResponse.getId())).isTrue();
    assertThat(userResponse.getEmail()).isEqualTo("thiago.costa@sensedia.com");
    assertThat(userResponse.getName()).isEqualTo("Thiago Costa");
    assertThat(userResponse.getStatus()).isEqualTo(UserStatus.ACTIVE.toString());
    assertThat(userResponse.getCreationDate()).isNotNull();

    assertThat(headers.get("event_name")).isEqualTo("UserCreation");
  }

  @Test
  @DisplayName("I want to create a user without email")
  public void createUserWithoutEmail() throws IOException {
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
    assertThat(collector.forChannel(output.publishCreatedUser()).poll()).isNull();
  }

  @Test
  @DisplayName("I want to create a user without name")
  public void createUserWithoutName() throws IOException {
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
    assertThat(collector.forChannel(output.publishCreatedUser()).poll()).isNull();
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
    assertThat(response.getBody().getDetail()).isEqualTo("email não é um endereço de e-mail");
    assertThat(response.getBody().getType()).isNull();

    assertThat(repository.findAll()).hasSize(0);
    assertThat(collector.forChannel(output.publishCreatedUser()).poll()).isNull();
  }

}
