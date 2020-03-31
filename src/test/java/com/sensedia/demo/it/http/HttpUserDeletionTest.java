package com.sensedia.demo.it.http;

import com.sensedia.commons.errors.domains.DefaultErrorResponse;
import com.sensedia.demo.adapters.dtos.UserDto;
import com.sensedia.demo.domains.User;
import com.sensedia.demo.domains.UserStatus;
import com.sensedia.demo.commons.BrokerResponse;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
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
            "/users/{id}", HttpMethod.DELETE, HttpEntity.EMPTY, Object.class, USER_ID_VALID);

    // RESPONSE VALIDATION
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

    // DATABASE VALIDATION
    assertThat(repository.findAll()).hasSize(4);
    Optional<User> user = repository.findById(USER_ID_VALID);

    assertThat(user.isEmpty()).isTrue();

    // NOTIFICATION VALIDATION
    BrokerResponse brokerResponse = collector.forChannel(brokerOutput.publishUserDeleted());

    UserDto userResponse = brokerResponse.getPayload(UserDto.class);

    assertThat(isUUID(userResponse.getId())).isTrue();
    assertThat(userResponse.getEmail()).isEqualTo("usuario03@sensedia.com");
    assertThat(userResponse.getName()).isEqualTo("Usuário 03");
    assertThat(userResponse.getStatus()).isEqualTo(UserStatus.ACTIVE.toString());
    assertThat(userResponse.getCreatedAt()).isNotNull();

    assertThat(brokerResponse.getHeaders().get("event_name")).isEqualTo("UserDeletion");
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
            USER_ID_NOT_FOUND);

    // RESPONSE VALIDATION
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(response.getBody().getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    assertThat(response.getBody().getTitle()).isEqualTo(HttpStatus.NOT_FOUND.getReasonPhrase());
    assertThat(response.getBody().getDetail()).isEqualTo("User not found");
    assertThat(response.getBody().getType()).isNull();

    // DATABASE VALIDATION
    assertThat(repository.findAll()).hasSize(5);

    // NOTIFICATION VALIDATION
    assertThat(collector.forChannel(brokerOutput.publishUserDeleted())).isNull();
  }
}
