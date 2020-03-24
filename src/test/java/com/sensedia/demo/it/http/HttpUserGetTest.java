package com.sensedia.demo.it.http;

import com.sensedia.commons.exceptions.DefaultErrorResponse;
import com.sensedia.demo.adapters.dtos.UserResponseDto;
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

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class HttpUserGetTest extends AbstractUserTest {

  @BeforeEach
  public void setup() throws IOException {
    repository.deleteAll();
    loadDatabase();
  }

  @Test
  @DisplayName("I want to get a user with success")
  public void getUserSuccessfully() throws IOException {
    ResponseEntity<UserResponseDto> response =
        request.exchange(
            "/users/{id}", HttpMethod.GET, HttpEntity.EMPTY, UserResponseDto.class, USER_ID_VALID);

    // RESPONSE VALIDATION
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    UserResponseDto userResponse = response.getBody();

    assertThat(userResponse.getId()).isEqualTo(USER_ID_VALID);
    assertThat(userResponse.getEmail()).isEqualTo("usuario03@sensedia.com");
    assertThat(userResponse.getName()).isEqualTo("Usuário 03");
    assertThat(userResponse.getStatus()).isEqualTo(UserStatus.ACTIVE.toString());
    assertThat(userResponse.getCreationDate()).isNotNull();
  }

  @Test
  @DisplayName("I want to get a user that does not exist")
  public void getUserThatDoesNotExist() {
    ResponseEntity<DefaultErrorResponse> response =
        request.exchange(
            "/users/{id}",
            HttpMethod.GET,
            HttpEntity.EMPTY,
            DefaultErrorResponse.class,
            USER_ID_NOT_FOUND);

    // RESPONSE VALIDATION
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(response.getBody().getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    assertThat(response.getBody().getTitle()).isEqualTo(HttpStatus.NOT_FOUND.getReasonPhrase());
    assertThat(response.getBody().getDetail()).isEqualTo("User not found");
    assertThat(response.getBody().getType()).isNull();
  }
}