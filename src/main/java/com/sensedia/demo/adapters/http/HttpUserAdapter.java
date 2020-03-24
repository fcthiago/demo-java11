package com.sensedia.demo.adapters.http;

import com.sensedia.demo.adapters.dtos.UserCreationDto;
import com.sensedia.demo.adapters.dtos.UserResponseDto;
import com.sensedia.demo.adapters.mappers.UserMapper;
import com.sensedia.demo.domains.User;
import com.sensedia.demo.ports.ApplicationPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class HttpUserAdapter {

  private final ApplicationPort userApplication;
  private final UserMapper userMapper;

  public HttpUserAdapter(
      @Autowired ApplicationPort userApplication, @Autowired UserMapper userMapper) {
    this.userApplication = userApplication;
    this.userMapper = userMapper;
  }

  @PostMapping
  public ResponseEntity<UserResponseDto> create(@RequestBody UserCreationDto userCreation) {
    User user = userApplication.create(userMapper.toUser(userCreation));

    UserResponseDto userResponseDto = userMapper.toUserResponseDto(user);

    return ResponseEntity.status(HttpStatus.CREATED).body(userResponseDto);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable String id) {
    userApplication.delete(id);
  }

  @GetMapping("/{id}")
  public ResponseEntity<UserResponseDto> get(@PathVariable String id) {
    User user = userApplication.findById(id);

    UserResponseDto userResponse = userMapper.toUserResponseDto(user);

    return ResponseEntity.ok(userResponse);
  }

  @GetMapping
  public ResponseEntity<List<UserResponseDto>> getAll(
      @RequestParam(value = "status", required = false) final String status,
      @RequestParam(value = "name", required = false) final String name,
      @RequestParam(value = "creation_start_date", required = false) final String creationStartDate,
      @RequestParam(value = "creation_end_date", required = false) final String creationEndDate) {
    return null;
  }
}
