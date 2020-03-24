package com.sensedia.demo.domains;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Valid
@Document(collection = "users")
public class User {

  @Id private String id = UUID.randomUUID().toString();

  @NotNull private String name;

  @NotNull @Email private String email;

  private UserStatus status;

  private Instant creationDate;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public UserStatus getStatus() {
    return status;
  }

  public void setStatus(UserStatus status) {
    this.status = status;
  }

  public Instant getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(Instant creationDate) {
    this.creationDate = creationDate;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    User user = (User) o;
    return Objects.equals(id, user.id)
        && Objects.equals(name, user.name)
        && Objects.equals(email, user.email)
        && Objects.equals(status, user.status)
        && Objects.equals(creationDate, user.creationDate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, email, status, creationDate);
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("id", id)
        .append("name", name)
        .append("email", email)
        .append("status", status)
        .append("creationDate", creationDate)
        .toString();
  }
}
