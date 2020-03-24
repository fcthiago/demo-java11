package com.sensedia.demo.it.http;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import java.io.IOException;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class HttpUserSearchTest extends AbstractUserTest {

  @BeforeEach
  public void setup() throws IOException {
    repository.deleteAll();
    loadDatabase();
  }

  @Test
  @DisplayName("I want to search for a user with an empty result list")
  public void searchUserWithEmptyResultList() throws IOException {}

  @Test
  @DisplayName("I want to search for a user by name")
  public void searchUserByName() throws IOException {}

  @Test
  @DisplayName("I want to search for a user by email")
  public void searchUserByEmail() throws IOException {}

  @Test
  @DisplayName("I want to search for a user by status")
  public void searchUserByStatus() throws IOException {}

  @Test
  @DisplayName("I want to search for a user by creation date with an invalid date")
  public void searchUserByCreationDateWithInvalidDate() throws IOException {}

  @Test
  @DisplayName("I want to search for a user sorting by name")
  public void searchUserSortingByName() throws IOException {}

  @Test
  @DisplayName("I want to search for a user sorting by email")
  public void searchUserSortingByEmail() throws IOException {}

  @Test
  @DisplayName("I want to search for a user sorting by status")
  public void searchUserSortingByStatus() throws IOException {}

  @Test
  @DisplayName("I want to search for a user sorting by creation date")
  public void searchUserSortingByCreationDate() throws IOException {}

  @Test
  @DisplayName("I want to search for a user in descending order")
  public void searchUserDescendingOrder() throws IOException {}

  @Test
  @DisplayName("I want to search for a user in ascending order")
  public void searchUserAscendingOrder() throws IOException {}

  @Test
  @DisplayName("I want to search for a user on page two")
  public void searchUserOnPageTwo() throws IOException {}

  @Test
  @DisplayName("I want to search for a user and receive a database error")
  public void searchUserAndReceiveDatabaseError() throws IOException {}
}
