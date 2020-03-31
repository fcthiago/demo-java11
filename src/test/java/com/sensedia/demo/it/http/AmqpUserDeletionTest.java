package com.sensedia.demo.it.http;

import com.fasterxml.jackson.core.type.TypeReference;
import com.sensedia.commons.errors.domains.DefaultErrorResponse;
import com.sensedia.demo.adapters.dtos.UserDeletionDto;
import com.sensedia.demo.adapters.dtos.UserDto;
import com.sensedia.demo.commons.BrokerResponse;
import com.sensedia.demo.domains.User;
import com.sensedia.demo.domains.UserStatus;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;

import java.io.IOException;
import java.util.Optional;

import static com.sensedia.commons.headers.DefaultHeader.APP_ID_HEADER_NAME;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
public class AmqpUserDeletionTest extends AbstractUserTest {

  @BeforeEach
  public void setup() throws IOException {
    repository.deleteAll();
    loadDatabase();
  }

  @Test
  @DisplayName("I want to delete a user with success")
  public void deleteUserSuccessfully() throws IOException {
    Message<UserDeletionDto> message =
        MessageBuilder.withPayload(new UserDeletionDto(USER_ID_VALID))
            .setHeader(APP_ID_HEADER_NAME, "app-test")
            .build();

    brokerInput.subscribeUserDeletionRequested().send(message);

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
  public void deleteUserThatDoesNotExist() throws IOException {
    UserDeletionDto userDeletionDto = new UserDeletionDto(USER_ID_NOT_FOUND);

    Message<UserDeletionDto> message =
        MessageBuilder.withPayload(userDeletionDto)
            .setHeader(APP_ID_HEADER_NAME, "app-test")
            .build();

    brokerInput.subscribeUserDeletionRequested().send(message);

    // RESPONSE VALIDATION
    BrokerResponse brokerResponse = collector.forChannel(brokerOutput.publishUserOperationError());

    DefaultErrorResponse<UserDeletionDto> response =
        brokerResponse.getPayload(new TypeReference<>() {});

    assertThat(response.getOriginalMessage()).isEqualTo(userDeletionDto);
    assertThat(response.getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    assertThat(response.getTitle()).isEqualTo(HttpStatus.NOT_FOUND.getReasonPhrase());
    assertThat(response.getDetail()).isEqualTo("User not found");
    assertThat(response.getType()).isNull();

    // DATABASE VALIDATION
    assertThat(repository.findAll()).hasSize(5);

    // NOTIFICATION VALIDATION
    assertThat(collector.forChannel(brokerOutput.publishUserDeleted())).isNull();
  }
}
