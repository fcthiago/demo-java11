package com.sensedia.demo.it.http;

import com.sensedia.commons.errors.domains.DefaultErrorResponse;
import com.sensedia.demo.adapters.dtos.UserDto;
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

import static com.sensedia.commons.headers.DefaultHeader.HEADER_ACCEPT_RANGE;
import static com.sensedia.commons.headers.DefaultHeader.HEADER_CONTENT_RANGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.*;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
public class HttpUserSearchTest extends AbstractUserTest {

  @BeforeEach
  public void setup() throws IOException {
    repository.deleteAll();
    loadDatabase();
  }

  @Test
  @DisplayName("I want to search for a user with an empty result list")
  public void searchUserWithEmptyResultList() {
    ResponseEntity<UserDto[]> response =
        request.exchange(
            "/users?page=1&limit=3&name=asdf", HttpMethod.GET, HttpEntity.EMPTY, UserDto[].class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    assertThat(response.getBody()).hasSize(0);

    assertThat(response.getHeaders().get(HEADER_CONTENT_RANGE).get(0)).isEqualTo("0");
    assertThat(response.getHeaders().get(HEADER_ACCEPT_RANGE).get(0)).isEqualTo("100");
  }

  @Test
  @DisplayName("I want to search for a user by name")
  public void searchUserByName() {
    ResponseEntity<UserDto[]> response =
        request.exchange(
            "/users?page=1&limit=3&name=Usuário 01",
            HttpMethod.GET,
            HttpEntity.EMPTY,
            UserDto[].class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    assertThat(response.getBody()).hasSize(1);

    UserDto userDto = response.getBody()[0];

    assertThat(userDto.getId()).isEqualTo("887816e0-59fc-4dd3-a1dc-40f70fe1c650");
    assertThat(userDto.getName()).isEqualTo("Usuário 01");
    assertThat(userDto.getEmail()).isEqualTo("usuario01@sensedia.com");
    assertThat(userDto.getStatus()).isEqualTo(UserStatus.ACTIVE.name());
    assertThat(userDto.getCreatedAt()).isEqualTo(Instant.parse("2020-03-21T16:07:44.260Z"));
    assertThat(userDto.getUpdatedAt()).isEqualTo(Instant.parse("2020-03-22T16:07:44.260Z"));

    assertThat(response.getHeaders().get(HEADER_CONTENT_RANGE).get(0)).isEqualTo("1");
    assertThat(response.getHeaders().get(HEADER_ACCEPT_RANGE).get(0)).isEqualTo("100");
  }

  @Test
  @DisplayName("I want to search for a user by the first letters of the name")
  public void searchUserByFirstLettersOfTheName() {
    ResponseEntity<UserDto[]> response =
        request.exchange(
            "/users?page=1&limit=3&name=Usuário",
            HttpMethod.GET,
            HttpEntity.EMPTY,
            UserDto[].class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    assertThat(response.getBody()).hasSize(3);

    assertThat(response.getBody()[0].getName()).isEqualTo("Usuário 01");
    assertThat(response.getBody()[1].getName()).isEqualTo("Usuário 02");
    assertThat(response.getBody()[2].getName()).isEqualTo("Usuário 03");

    assertThat(response.getHeaders().get(HEADER_CONTENT_RANGE).get(0)).isEqualTo("5");
    assertThat(response.getHeaders().get(HEADER_ACCEPT_RANGE).get(0)).isEqualTo("100");
  }

  @Test
  @DisplayName("I want to search for a user by email")
  public void searchUserByEmail() {
    ResponseEntity<UserDto[]> response =
        request.exchange(
            "/users?page=1&limit=3&email=usuario01@sensedia.com",
            HttpMethod.GET,
            HttpEntity.EMPTY,
            UserDto[].class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    assertThat(response.getBody()).hasSize(1);

    assertThat(response.getBody()[0].getName()).isEqualTo("Usuário 01");

    assertThat(response.getHeaders().get(HEADER_CONTENT_RANGE).get(0)).isEqualTo("1");
    assertThat(response.getHeaders().get(HEADER_ACCEPT_RANGE).get(0)).isEqualTo("100");
  }

  @Test
  @DisplayName("I want to search for a user by the first letters of the email")
  public void searchUserByFirstLettersOfTheEmail() {
    ResponseEntity<UserDto[]> response =
        request.exchange(
            "/users?page=1&limit=3&email=usuario",
            HttpMethod.GET,
            HttpEntity.EMPTY,
            UserDto[].class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    assertThat(response.getBody()).hasSize(3);

    assertThat(response.getBody()[0].getName()).isEqualTo("Usuário 01");
    assertThat(response.getBody()[1].getName()).isEqualTo("Usuário 02");
    assertThat(response.getBody()[2].getName()).isEqualTo("Usuário 03");

    assertThat(response.getHeaders().get(HEADER_CONTENT_RANGE).get(0)).isEqualTo("5");
    assertThat(response.getHeaders().get(HEADER_ACCEPT_RANGE).get(0)).isEqualTo("100");
  }

  @Test
  @DisplayName("I want to search for a user by status")
  public void searchUserByStatus() {
    ResponseEntity<UserDto[]> response =
        request.exchange(
            "/users?page=1&limit=3&status=ACTIVE",
            HttpMethod.GET,
            HttpEntity.EMPTY,
            UserDto[].class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    assertThat(response.getBody()).hasSize(3);

    assertThat(response.getBody()[0].getName()).isEqualTo("Usuário 01");
    assertThat(response.getBody()[1].getName()).isEqualTo("Usuário 02");
    assertThat(response.getBody()[2].getName()).isEqualTo("Usuário 03");

    assertThat(response.getHeaders().get(HEADER_CONTENT_RANGE).get(0)).isEqualTo("4");
    assertThat(response.getHeaders().get(HEADER_ACCEPT_RANGE).get(0)).isEqualTo("100");
  }

  @Test
  @DisplayName("I want to search for a user by status with invalid status name")
  public void searchUserByStatusWithInvalidStatusName() {
    ResponseEntity<DefaultErrorResponse> response =
        request.exchange(
            "/users?page=1&limit=3&status=ERROR",
            HttpMethod.GET,
            HttpEntity.EMPTY,
            DefaultErrorResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);

    assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    assertThat(response.getBody().getStatus()).isEqualTo(BAD_REQUEST.value());
    assertThat(response.getBody().getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
    assertThat(response.getBody().getDetail())
        .isEqualTo("Invalid status [ERROR], accepted values: [active, disable]");
    assertThat(response.getBody().getType()).isNull();
  }

  @Test
  @DisplayName("I want to search for a user by creation date")
  public void searchUserByCreationDate() {
    ResponseEntity<UserDto[]> response =
        request.exchange(
            "/users?page=1&limit=3&created_at_start=2020-03-21&created_at_end=2020-03-23",
            HttpMethod.GET,
            HttpEntity.EMPTY,
            UserDto[].class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    assertThat(response.getBody()).hasSize(2);

    assertThat(response.getBody()[0].getName()).isEqualTo("Usuário 01");
    assertThat(response.getBody()[1].getName()).isEqualTo("Usuário 02");

    assertThat(response.getHeaders().get(HEADER_CONTENT_RANGE).get(0)).isEqualTo("2");
    assertThat(response.getHeaders().get(HEADER_ACCEPT_RANGE).get(0)).isEqualTo("100");
  }

  @Test
  @DisplayName("I want to search for a user by creation date with an invalid date")
  public void searchUserByCreationDateWithInvalidDate() {
    ResponseEntity<DefaultErrorResponse> response =
        request.exchange(
            "/users?page=1&limit=3&created_at_start=2020-03-21&created_at_end=2020",
            HttpMethod.GET,
            HttpEntity.EMPTY,
            DefaultErrorResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);

    assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    assertThat(response.getBody().getStatus()).isEqualTo(BAD_REQUEST.value());
    assertThat(response.getBody().getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
    assertThat(response.getBody().getDetail())
        .isEqualTo("Invalid date format for the value [2020]. Use the date in ISO 8601 format");
    assertThat(response.getBody().getType()).isNull();
  }

  @Test
  @DisplayName("I want to search for a user sorting by name")
  public void searchUserSortingByName() {
    ResponseEntity<UserDto[]> response =
        request.exchange(
            "/users?page=1&limit=3&sort=name", HttpMethod.GET, HttpEntity.EMPTY, UserDto[].class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    UserDto[] usersResponse = response.getBody();

    assertThat(usersResponse).hasSize(3);

    assertThat(response.getBody()[0].getName()).isEqualTo("Usuário 01");
    assertThat(response.getBody()[1].getName()).isEqualTo("Usuário 02");
    assertThat(response.getBody()[2].getName()).isEqualTo("Usuário 03");

    assertThat(response.getHeaders().get(HEADER_CONTENT_RANGE).get(0)).isEqualTo("5");
    assertThat(response.getHeaders().get(HEADER_ACCEPT_RANGE).get(0)).isEqualTo("100");

    assertThat(usersResponse[0].getName()).isEqualTo("Usuário 01");
    assertThat(usersResponse[1].getName()).isEqualTo("Usuário 02");
    assertThat(usersResponse[2].getName()).isEqualTo("Usuário 03");
  }

  @Test
  @DisplayName("I want to search for a user sorting by email")
  public void searchUserSortingByEmail() {
    ResponseEntity<UserDto[]> response =
        request.exchange(
            "/users?page=1&limit=3&sort=email", HttpMethod.GET, HttpEntity.EMPTY, UserDto[].class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    UserDto[] usersResponse = response.getBody();

    assertThat(usersResponse).hasSize(3);

    assertThat(response.getHeaders().get(HEADER_CONTENT_RANGE).get(0)).isEqualTo("5");
    assertThat(response.getHeaders().get(HEADER_ACCEPT_RANGE).get(0)).isEqualTo("100");

    assertThat(usersResponse[0].getEmail()).isEqualTo("usuario01@sensedia.com");
    assertThat(usersResponse[1].getEmail()).isEqualTo("usuario02@sensedia.com");
    assertThat(usersResponse[2].getEmail()).isEqualTo("usuario03@sensedia.com");
  }

  @Test
  @DisplayName("I want to search for a user sorting by status")
  public void searchUserSortingByStatus() {
    ResponseEntity<UserDto[]> response =
        request.exchange(
            "/users?page=1&limit=3&sort=status", HttpMethod.GET, HttpEntity.EMPTY, UserDto[].class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    UserDto[] usersResponse = response.getBody();

    assertThat(usersResponse).hasSize(3);

    assertThat(response.getHeaders().get(HEADER_CONTENT_RANGE).get(0)).isEqualTo("5");
    assertThat(response.getHeaders().get(HEADER_ACCEPT_RANGE).get(0)).isEqualTo("100");

    assertThat(usersResponse[0].getName()).isEqualTo("Usuário 01");
    assertThat(usersResponse[1].getName()).isEqualTo("Usuário 03");
    assertThat(usersResponse[2].getName()).isEqualTo("Usuário 05");
  }

  @Test
  @DisplayName("I want to search for a user sorting by creation date")
  public void searchUserSortingByCreationDate() {
    ResponseEntity<UserDto[]> response =
        request.exchange(
            "/users?page=1&limit=3&sort=created_at",
            HttpMethod.GET,
            HttpEntity.EMPTY,
            UserDto[].class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    UserDto[] usersResponse = response.getBody();

    assertThat(usersResponse).hasSize(3);

    assertThat(response.getHeaders().get(HEADER_CONTENT_RANGE).get(0)).isEqualTo("5");
    assertThat(response.getHeaders().get(HEADER_ACCEPT_RANGE).get(0)).isEqualTo("100");

    assertThat(usersResponse[0].getName()).isEqualTo("Usuário 01");
    assertThat(usersResponse[1].getName()).isEqualTo("Usuário 02");
    assertThat(usersResponse[2].getName()).isEqualTo("Usuário 03");
  }

  @Test
  @DisplayName("I want to search for a user with an invalid sort")
  public void searchUserWithInvalidSort() {
    ResponseEntity<DefaultErrorResponse> response =
        request.exchange(
            "/users?page=1&limit=3&sort=invalid",
            HttpMethod.GET,
            HttpEntity.EMPTY,
            DefaultErrorResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);

    assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    assertThat(response.getBody().getStatus()).isEqualTo(BAD_REQUEST.value());
    assertThat(response.getBody().getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
    assertThat(response.getBody().getDetail())
        .isEqualTo("Invalid sort [invalid], accepted values: [name, email, status, created_at]");
    assertThat(response.getBody().getType()).isNull();
  }

  @Test
  @DisplayName("I want to search for a user in descending order")
  public void searchUserDescendingOrder() {
    ResponseEntity<UserDto[]> response =
        request.exchange(
            "/users?page=1&limit=3&sort=name&sort_type=desc",
            HttpMethod.GET,
            HttpEntity.EMPTY,
            UserDto[].class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    UserDto[] usersResponse = response.getBody();

    assertThat(usersResponse).hasSize(3);

    assertThat(response.getHeaders().get(HEADER_CONTENT_RANGE).get(0)).isEqualTo("5");
    assertThat(response.getHeaders().get(HEADER_ACCEPT_RANGE).get(0)).isEqualTo("100");

    assertThat(usersResponse[0].getName()).isEqualTo("Usuário 05");
    assertThat(usersResponse[1].getName()).isEqualTo("Usuário 04");
    assertThat(usersResponse[2].getName()).isEqualTo("Usuário 03");
  }

  @Test
  @DisplayName("I want to search for a user in ascending order")
  public void searchUserAscendingOrder() {
    ResponseEntity<UserDto[]> response =
        request.exchange(
            "/users?page=1&limit=3&sort=name&order_type=asc",
            HttpMethod.GET,
            HttpEntity.EMPTY,
            UserDto[].class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    UserDto[] usersResponse = response.getBody();

    assertThat(usersResponse).hasSize(3);

    assertThat(response.getHeaders().get(HEADER_CONTENT_RANGE).get(0)).isEqualTo("5");
    assertThat(response.getHeaders().get(HEADER_ACCEPT_RANGE).get(0)).isEqualTo("100");

    assertThat(usersResponse[0].getName()).isEqualTo("Usuário 01");
    assertThat(usersResponse[1].getName()).isEqualTo("Usuário 02");
    assertThat(usersResponse[2].getName()).isEqualTo("Usuário 03");
  }

  @Test
  @DisplayName("I want to search for a user in ascending order")
  public void searchUserWithInvalidOrder() {
    ResponseEntity<DefaultErrorResponse> response =
        request.exchange(
            "/users?page=1&limit=3&sort_type=invalid",
            HttpMethod.GET,
            HttpEntity.EMPTY,
            DefaultErrorResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);

    assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    assertThat(response.getBody().getStatus()).isEqualTo(BAD_REQUEST.value());
    assertThat(response.getBody().getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
    assertThat(response.getBody().getDetail())
        .isEqualTo("Invalid sort type [invalid], accepted values: [asc, desc]");
    assertThat(response.getBody().getType()).isNull();
  }

  @Test
  @DisplayName("I want to search for a user on page two")
  public void searchUserOnPageTwo() {
    ResponseEntity<UserDto[]> response =
        request.exchange(
            "/users?page=2&limit=3", HttpMethod.GET, HttpEntity.EMPTY, UserDto[].class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    UserDto[] usersResponse = response.getBody();

    assertThat(usersResponse).hasSize(2);

    assertThat(response.getHeaders().get(HEADER_CONTENT_RANGE).get(0)).isEqualTo("5");
    assertThat(response.getHeaders().get(HEADER_ACCEPT_RANGE).get(0)).isEqualTo("100");

    assertThat(usersResponse[0].getName()).isEqualTo("Usuário 04");
    assertThat(usersResponse[1].getName()).isEqualTo("Usuário 05");
  }

  @Test
  @DisplayName("I want to search for a user with zero limit")
  public void searchUserWithZeroLimit() {
    ResponseEntity<DefaultErrorResponse> response =
        request.exchange(
            "/users?page=1&limit=0", HttpMethod.GET, HttpEntity.EMPTY, DefaultErrorResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);

    assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    assertThat(response.getBody().getStatus()).isEqualTo(BAD_REQUEST.value());
    assertThat(response.getBody().getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
    assertThat(response.getBody().getDetail()).isEqualTo("limit deve ser maior ou igual a 1.");
    assertThat(response.getBody().getType()).isNull();
  }

  @Test
  @DisplayName("I want to search for a user on page zero")
  public void searchUserWithOnPageZero() {
    ResponseEntity<DefaultErrorResponse> response =
        request.exchange(
            "/users?page=0&limit=3", HttpMethod.GET, HttpEntity.EMPTY, DefaultErrorResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);

    assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
    assertThat(response.getBody().getStatus()).isEqualTo(BAD_REQUEST.value());
    assertThat(response.getBody().getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
    assertThat(response.getBody().getDetail()).isEqualTo("page deve ser maior ou igual a 1.");
    assertThat(response.getBody().getType()).isNull();
  }

  @Test
  @DisplayName("I want to search for a user with a higher limit than the maximum limit")
  public void searchUserWithHigherLimitThanMaximumLimit() {
    ResponseEntity<DefaultErrorResponse> response =
        request.exchange(
            "/users?page=1&limit=1000",
            HttpMethod.GET,
            HttpEntity.EMPTY,
            DefaultErrorResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(PRECONDITION_FAILED);

    assertThat(response.getStatusCode()).isEqualTo(PRECONDITION_FAILED);
    assertThat(response.getBody().getStatus()).isEqualTo(PRECONDITION_FAILED.value());
    assertThat(response.getBody().getTitle()).isEqualTo(PRECONDITION_FAILED.getReasonPhrase());
    assertThat(response.getBody().getDetail())
        .isEqualTo("The 'limit' field is greater than the configured maximum limit [100]");
    assertThat(response.getBody().getType()).isNull();
  }

  @Test
  @DisplayName("I want to search for a user and receive a database error")
  public void searchUserAndReceiveDatabaseError() {
    try {
      injectDatabaseError();

      ResponseEntity<DefaultErrorResponse> response =
          request.exchange("/users", HttpMethod.GET, HttpEntity.EMPTY, DefaultErrorResponse.class);

      assertThat(response.getStatusCode()).isEqualTo(INTERNAL_SERVER_ERROR);

      assertThat(response.getStatusCode()).isEqualTo(INTERNAL_SERVER_ERROR);
      assertThat(response.getBody().getStatus()).isEqualTo(INTERNAL_SERVER_ERROR.value());
      assertThat(response.getBody().getTitle()).isEqualTo(INTERNAL_SERVER_ERROR.getReasonPhrase());
      assertThat(response.getBody().getDetail()).isEqualTo("database error");
      assertThat(response.getBody().getType()).isNull();

    } finally {
      undoDatabaseError();
    }
  }
}
