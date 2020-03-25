package com.sensedia.demo.it.http;

import com.sensedia.commons.exceptions.DefaultErrorResponse;
import com.sensedia.commons.headers.DefaultHeader;
import com.sensedia.demo.adapters.dtos.UserResponseDto;
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
public class HttpUserSearchTest extends AbstractUserTest {

  @BeforeEach
  public void setup() throws IOException {
    repository.deleteAll();
    loadDatabase();
  }

  @Test
  @DisplayName("I want to search for a user with an empty result list")
  public void searchUserWithEmptyResultList() throws IOException {
    ResponseEntity<UserResponseDto[]> response =
        request.exchange(
            "/users?page=1&limit=3&name=asdf",
            HttpMethod.GET,
            HttpEntity.EMPTY,
            UserResponseDto[].class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    assertThat(response.getBody()).hasSize(0);

    assertThat(response.getHeaders().get(DefaultHeader.HEADER_CONTENT_RANGE).get(0)).isEqualTo("0");
    assertThat(response.getHeaders().get(DefaultHeader.HEADER_ACCEPT_RANGE).get(0)).isEqualTo("10");
  }

  @Test
  @DisplayName("I want to search for a user by name")
  public void searchUserByName() throws IOException {
    ResponseEntity<UserResponseDto[]> response =
        request.exchange(
            "/users?page=1&limit=3&name=usuario01",
            HttpMethod.GET,
            HttpEntity.EMPTY,
            UserResponseDto[].class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    assertThat(response.getBody()).hasSize(1);

    assertThat(response.getHeaders().get(DefaultHeader.HEADER_CONTENT_RANGE).get(0)).isEqualTo("1");
    assertThat(response.getHeaders().get(DefaultHeader.HEADER_ACCEPT_RANGE).get(0)).isEqualTo("10");
  }

  @Test
  @DisplayName("I want to search for a user by the first letters of the name")
  public void searchUserByFirstLettersOfTheName() throws IOException {
    ResponseEntity<UserResponseDto[]> response =
        request.exchange(
            "/users?page=1&limit=3&name=usuar",
            HttpMethod.GET,
            HttpEntity.EMPTY,
            UserResponseDto[].class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    assertThat(response.getBody()).hasSize(3);

    assertThat(response.getHeaders().get(DefaultHeader.HEADER_CONTENT_RANGE).get(0)).isEqualTo("5");
    assertThat(response.getHeaders().get(DefaultHeader.HEADER_ACCEPT_RANGE).get(0)).isEqualTo("10");
  }

  @Test
  @DisplayName("I want to search for a user by email")
  public void searchUserByEmail() throws IOException {
    ResponseEntity<UserResponseDto[]> response =
        request.exchange(
            "/users?page=1&limit=3&email=usuario01@sensedia.com",
            HttpMethod.GET,
            HttpEntity.EMPTY,
            UserResponseDto[].class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    assertThat(response.getBody()).hasSize(1);

    assertThat(response.getHeaders().get(DefaultHeader.HEADER_CONTENT_RANGE).get(0)).isEqualTo("1");
    assertThat(response.getHeaders().get(DefaultHeader.HEADER_ACCEPT_RANGE).get(0)).isEqualTo("10");
  }

  @Test
  @DisplayName("I want to search for a user by the first letters of the email")
  public void searchUserByFirstLettersOfTheEmail() throws IOException {
    ResponseEntity<UserResponseDto[]> response =
        request.exchange(
            "/users?page=1&limit=3&email=usuario",
            HttpMethod.GET,
            HttpEntity.EMPTY,
            UserResponseDto[].class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    assertThat(response.getBody()).hasSize(3);

    assertThat(response.getHeaders().get(DefaultHeader.HEADER_CONTENT_RANGE).get(0)).isEqualTo("5");
    assertThat(response.getHeaders().get(DefaultHeader.HEADER_ACCEPT_RANGE).get(0)).isEqualTo("10");
  }

  @Test
  @DisplayName("I want to search for a user by status")
  public void searchUserByStatus() throws IOException {
    ResponseEntity<UserResponseDto[]> response =
        request.exchange(
            "/users?page=1&limit=3&status=ACTIVE",
            HttpMethod.GET,
            HttpEntity.EMPTY,
            UserResponseDto[].class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    assertThat(response.getBody()).hasSize(3);

    assertThat(response.getHeaders().get(DefaultHeader.HEADER_CONTENT_RANGE).get(0)).isEqualTo("4");
    assertThat(response.getHeaders().get(DefaultHeader.HEADER_ACCEPT_RANGE).get(0)).isEqualTo("10");
  }

  @Test
  @DisplayName("I want to search for a user by status with invalid status name")
  public void searchUserByStatusWithInvalidStatusName() throws IOException {
    ResponseEntity<DefaultErrorResponse> response =
        request.exchange(
            "/users?page=1&limit=3&status=ERROR",
            HttpMethod.GET,
            HttpEntity.EMPTY,
            DefaultErrorResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    assertThat(response.getBody().getTitle()).isEqualTo(HttpStatus.BAD_REQUEST.getReasonPhrase());
    assertThat(response.getBody().getDetail()).isEqualTo("error.");
    assertThat(response.getBody().getType()).isNull();
  }

  @Test
  @DisplayName("I want to search for a user by creation date")
  public void searchUserByCreationDate() throws IOException {
    ResponseEntity<UserResponseDto[]> response =
        request.exchange(
            "/users?page=1&limit=3&creation_date_start=2020-03-21&creation_date_end=2020-03-23",
            HttpMethod.GET,
            HttpEntity.EMPTY,
            UserResponseDto[].class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    assertThat(response.getBody()).hasSize(3);

    assertThat(response.getHeaders().get(DefaultHeader.HEADER_CONTENT_RANGE).get(0)).isEqualTo("3");
    assertThat(response.getHeaders().get(DefaultHeader.HEADER_ACCEPT_RANGE).get(0)).isEqualTo("10");
  }

  @Test
  @DisplayName("I want to search for a user by creation date with an invalid date")
  public void searchUserByCreationDateWithInvalidDate() throws IOException {
    ResponseEntity<DefaultErrorResponse> response =
        request.exchange(
            "/users?page=1&limit=3&creation_date_start=2020-03-21&creation_date_end=2020",
            HttpMethod.GET,
            HttpEntity.EMPTY,
            DefaultErrorResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    assertThat(response.getBody().getTitle()).isEqualTo(HttpStatus.BAD_REQUEST.getReasonPhrase());
    assertThat(response.getBody().getDetail()).isEqualTo("error.");
    assertThat(response.getBody().getType()).isNull();
  }

  @Test
  @DisplayName("I want to search for a user sorting by name")
  public void searchUserSortingByName() throws IOException {
    ResponseEntity<UserResponseDto[]> response =
        request.exchange(
            "/users?page=1&limit=3sort=name",
            HttpMethod.GET,
            HttpEntity.EMPTY,
            UserResponseDto[].class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    UserResponseDto[] usersResponse = response.getBody();

    assertThat(usersResponse).hasSize(3);

    assertThat(response.getHeaders().get(DefaultHeader.HEADER_CONTENT_RANGE).get(0)).isEqualTo("5");
    assertThat(response.getHeaders().get(DefaultHeader.HEADER_ACCEPT_RANGE).get(0)).isEqualTo("10");

    assertThat(usersResponse[0].getName()).isEqualTo("usuário01");
    assertThat(usersResponse[1].getName()).isEqualTo("usuário02");
    assertThat(usersResponse[2].getName()).isEqualTo("usuário03");
  }

  @Test
  @DisplayName("I want to search for a user sorting by email")
  public void searchUserSortingByEmail() throws IOException {
    ResponseEntity<UserResponseDto[]> response =
        request.exchange(
            "/users?page=1&limit=3sort=email",
            HttpMethod.GET,
            HttpEntity.EMPTY,
            UserResponseDto[].class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    UserResponseDto[] usersResponse = response.getBody();

    assertThat(usersResponse).hasSize(3);

    assertThat(response.getHeaders().get(DefaultHeader.HEADER_CONTENT_RANGE).get(0)).isEqualTo("5");
    assertThat(response.getHeaders().get(DefaultHeader.HEADER_ACCEPT_RANGE).get(0)).isEqualTo("10");

    assertThat(usersResponse[0].getEmail()).isEqualTo("usuario01@sensedia.com");
    assertThat(usersResponse[1].getEmail()).isEqualTo("usuario02@sensedia.com");
    assertThat(usersResponse[2].getEmail()).isEqualTo("usuario03@sensedia.com");
  }

  @Test
  @DisplayName("I want to search for a user sorting by status")
  public void searchUserSortingByStatus() throws IOException {
    ResponseEntity<UserResponseDto[]> response =
        request.exchange(
            "/users?page=1&limit=3sort=status",
            HttpMethod.GET,
            HttpEntity.EMPTY,
            UserResponseDto[].class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    UserResponseDto[] usersResponse = response.getBody();

    assertThat(usersResponse).hasSize(3);

    assertThat(response.getHeaders().get(DefaultHeader.HEADER_CONTENT_RANGE).get(0)).isEqualTo("5");
    assertThat(response.getHeaders().get(DefaultHeader.HEADER_ACCEPT_RANGE).get(0)).isEqualTo("10");

    assertThat(usersResponse[0].getName()).isEqualTo("usuário01");
    assertThat(usersResponse[1].getName()).isEqualTo("usuário02");
    assertThat(usersResponse[2].getName()).isEqualTo("usuário03");
  }

  @Test
  @DisplayName("I want to search for a user sorting by creation date")
  public void searchUserSortingByCreationDate() throws IOException {
    ResponseEntity<UserResponseDto[]> response =
        request.exchange(
            "/users?page=1&limit=3sort=creation_date",
            HttpMethod.GET,
            HttpEntity.EMPTY,
            UserResponseDto[].class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    UserResponseDto[] usersResponse = response.getBody();

    assertThat(usersResponse).hasSize(3);

    assertThat(response.getHeaders().get(DefaultHeader.HEADER_CONTENT_RANGE).get(0)).isEqualTo("5");
    assertThat(response.getHeaders().get(DefaultHeader.HEADER_ACCEPT_RANGE).get(0)).isEqualTo("10");

    assertThat(usersResponse[0].getName()).isEqualTo("usuário01");
    assertThat(usersResponse[1].getName()).isEqualTo("usuário02");
    assertThat(usersResponse[2].getName()).isEqualTo("usuário03");
  }

  @Test
  @DisplayName("I want to search for a user with an invalid sort")
  public void searchUserWithInvalidSort() throws IOException {
    ResponseEntity<DefaultErrorResponse> response =
            request.exchange(
                    "/users?page=1&limit=3&sort=invalid",
                    HttpMethod.GET,
                    HttpEntity.EMPTY,
                    DefaultErrorResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    assertThat(response.getBody().getTitle()).isEqualTo(HttpStatus.BAD_REQUEST.getReasonPhrase());
    assertThat(response.getBody().getDetail()).isEqualTo("error.");
    assertThat(response.getBody().getType()).isNull();
  }

  @Test
  @DisplayName("I want to search for a user in descending order")
  public void searchUserDescendingOrder() throws IOException {
    ResponseEntity<UserResponseDto[]> response =
        request.exchange(
            "/users?page=1&limit=3sort=name&order=desc",
            HttpMethod.GET,
            HttpEntity.EMPTY,
            UserResponseDto[].class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    UserResponseDto[] usersResponse = response.getBody();

    assertThat(usersResponse).hasSize(3);

    assertThat(response.getHeaders().get(DefaultHeader.HEADER_CONTENT_RANGE).get(0)).isEqualTo("5");
    assertThat(response.getHeaders().get(DefaultHeader.HEADER_ACCEPT_RANGE).get(0)).isEqualTo("10");

    assertThat(usersResponse[0].getName()).isEqualTo("usuário05");
    assertThat(usersResponse[1].getName()).isEqualTo("usuário04");
    assertThat(usersResponse[2].getName()).isEqualTo("usuário03");
  }

  @Test
  @DisplayName("I want to search for a user in ascending order")
  public void searchUserAscendingOrder() throws IOException {
    ResponseEntity<UserResponseDto[]> response =
        request.exchange(
            "/users?page=1&limit=3sort=name&order=asc",
            HttpMethod.GET,
            HttpEntity.EMPTY,
            UserResponseDto[].class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    UserResponseDto[] usersResponse = response.getBody();

    assertThat(usersResponse).hasSize(3);

    assertThat(response.getHeaders().get(DefaultHeader.HEADER_CONTENT_RANGE).get(0)).isEqualTo("5");
    assertThat(response.getHeaders().get(DefaultHeader.HEADER_ACCEPT_RANGE).get(0)).isEqualTo("10");

    assertThat(usersResponse[0].getName()).isEqualTo("usuário01");
    assertThat(usersResponse[1].getName()).isEqualTo("usuário02");
    assertThat(usersResponse[2].getName()).isEqualTo("usuário03");
  }

  @Test
  @DisplayName("I want to search for a user in ascending order")
  public void searchUserWithInvalidOrder() throws IOException {
    ResponseEntity<DefaultErrorResponse> response =
            request.exchange(
                    "/users?page=1&limit=3&order=invalid",
                    HttpMethod.GET,
                    HttpEntity.EMPTY,
                    DefaultErrorResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    assertThat(response.getBody().getTitle()).isEqualTo(HttpStatus.BAD_REQUEST.getReasonPhrase());
    assertThat(response.getBody().getDetail()).isEqualTo("error.");
    assertThat(response.getBody().getType()).isNull();
  }

  @Test
  @DisplayName("I want to search for a user on page two")
  public void searchUserOnPageTwo() throws IOException {
    ResponseEntity<UserResponseDto[]> response =
        request.exchange(
            "/users?page=2&limit=3", HttpMethod.GET, HttpEntity.EMPTY, UserResponseDto[].class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    UserResponseDto[] usersResponse = response.getBody();

    assertThat(usersResponse).hasSize(2);

    assertThat(response.getHeaders().get(DefaultHeader.HEADER_CONTENT_RANGE).get(0)).isEqualTo("5");
    assertThat(response.getHeaders().get(DefaultHeader.HEADER_ACCEPT_RANGE).get(0)).isEqualTo("10");

    assertThat(usersResponse[0].getName()).isEqualTo("usuário04");
    assertThat(usersResponse[1].getName()).isEqualTo("usuário05");
  }

  @Test
  @DisplayName("I want to search for a user and receive a database error")
  public void searchUserAndReceiveDatabaseError() throws IOException {}
}
