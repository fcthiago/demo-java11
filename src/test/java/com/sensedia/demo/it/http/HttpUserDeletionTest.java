package com.sensedia.demo.it.http;

import com.sensedia.commons.exceptions.DefaultErrorResponse;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class HttpUserDeletionTest extends AbstractUserTest {

  @BeforeEach
  public void setup() throws IOException {
    repository.deleteAll();
    loadDatabase();
  }

  @Test
  @DisplayName("I want to delete a user with success")
  public void deleteUserSuccessfully() throws IOException {
    ResponseEntity<?> response =
            request.exchange(
                    "/users/{id}",
                    HttpMethod.DELETE,
                    HttpEntity.EMPTY,
                    Object.class,
                    "620dac34-15e1-4375-8be8-e9a46d1f5a36");

    // RESPONSE VALIDATION
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

    // DATABASE VALIDATION
    assertThat(repository.findAll()).hasSize(4);
    Optional<User> user = repository.findById("620dac34-15e1-4375-8be8-e9a46d1f5a36");

    assertThat(user.isEmpty()).isTrue();

    // NOTIFICATION VALIDATION
    Message<?> poll = collector.forChannel(output.publishDeletedUser()).poll();
    Object payload = poll.getPayload();
    MessageHeaders headers = poll.getHeaders();

    UserResponseDto userResponse = mapper.readValue(payload.toString(), UserResponseDto.class);

    assertThat(isUUID(userResponse.getId())).isTrue();
    assertThat(userResponse.getEmail()).isEqualTo("usuario03@sensedia.com");
    assertThat(userResponse.getName()).isEqualTo("Usuário 03");
    assertThat(userResponse.getStatus()).isEqualTo(UserStatus.ACTIVE.toString());
    assertThat(userResponse.getCreationDate()).isNotNull();

    assertThat(headers.get("event_name")).isEqualTo("UserDeletion");
  }

  @Test
  @DisplayName("I want to delete a user that does not exist")
  public void deleteUserThatDoesNotExist() {
    ResponseEntity<DefaultErrorResponse> response =
        request.exchange(
                "/users/{id}",
                HttpMethod.DELETE,
            HttpEntity.EMPTY,
            DefaultErrorResponse.class,
            "not-found");

    // RESPONSE VALIDATION
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(response.getBody().getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    assertThat(response.getBody().getTitle()).isEqualTo(HttpStatus.NOT_FOUND.getReasonPhrase());
    assertThat(response.getBody().getDetail()).isEqualTo("User not found");
    assertThat(response.getBody().getType()).isNull();

    // DATABASE VALIDATION
    assertThat(repository.findAll()).hasSize(5);

    // NOTIFICATION VALIDATION
    assertThat(collector.forChannel(output.publishCreatedUser()).poll()).isNull();
  }
}
