package com.sensedia.demo.adapters.http;

import com.sensedia.commons.converters.InstantConverter;
import com.sensedia.demo.adapters.dtos.UserCreationDto;
import com.sensedia.demo.adapters.dtos.UserDto;
import com.sensedia.demo.adapters.dtos.UserUpdateDto;
import com.sensedia.demo.adapters.mappers.UserMapper;
import com.sensedia.demo.domains.User;
import com.sensedia.demo.domains.search.UserSearch;
import com.sensedia.demo.domains.search.UserSearchBuild;
import com.sensedia.demo.domains.search.UserSearchResponse;
import com.sensedia.demo.ports.ApplicationPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static com.sensedia.commons.headers.DefaultHeader.HEADER_ACCEPT_RANGE;
import static com.sensedia.commons.headers.DefaultHeader.HEADER_CONTENT_RANGE;
import static java.lang.String.valueOf;

@RestController
@RequestMapping("/users")
public class HttpUserAdapter {

  private final ApplicationPort userApplication;
  private final UserMapper userMapper;
  private final InstantConverter instantConverter;

  @Autowired
  public HttpUserAdapter(
      ApplicationPort userApplication, UserMapper userMapper, InstantConverter instantConverter) {
    this.userApplication = userApplication;
    this.userMapper = userMapper;
    this.instantConverter = instantConverter;
  }

  @PostMapping
  public ResponseEntity<UserDto> create(@RequestBody UserCreationDto userCreation) {
    User user = userApplication.create(userMapper.toUser(userCreation));

    UserDto userDto = userMapper.toUserDto(user);

    return ResponseEntity.status(HttpStatus.CREATED).body(userDto);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable String id) {
    userApplication.delete(id);
  }

  @PutMapping("/{id}")
  public ResponseEntity<UserDto> update(
      @PathVariable String id, @Valid @RequestBody UserUpdateDto userUpdateDto) {
    User user = userApplication.update(userMapper.toUser(userUpdateDto), id);

    UserDto userDto = userMapper.toUserDto(user);

    return ResponseEntity.ok(userDto);
  }

  @GetMapping("/{id}")
  public ResponseEntity<UserDto> get(@PathVariable String id) {
    User user = userApplication.findById(id);

    UserDto userResponse = userMapper.toUserDto(user);

    return ResponseEntity.ok(userResponse);
  }

  @GetMapping
  public ResponseEntity<List<UserDto>> getAll(
      @RequestParam(value = "status", required = false) final String status,
      @RequestParam(value = "name", required = false) final String name,
      @RequestParam(value = "email", required = false) final String email,
      @RequestParam(value = "creation_date_start", required = false) final String creationDateStart,
      @RequestParam(value = "creation_date_end", required = false) final String creationDateEnd,
      @RequestParam(value = "sort", required = false, defaultValue = "name") String sort,
      @RequestParam(value = "sort_type", required = false, defaultValue = "asc") String sortType,
      @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
      @RequestParam(value = "limit", required = false) Integer limit) {

    UserSearch userSearch =
        UserSearchBuild.builder()
            .status(status)
            .name(name)
            .email(email)
            .createDateStart(instantConverter.toInstant(creationDateStart))
            .createDateEnd(instantConverter.toInstant(creationDateEnd))
            .sort(sort)
            .sortType(sortType)
            .page(page)
            .limit(limit)
            .build();

    UserSearchResponse userSearchResponse = userApplication.findAll(userSearch);

    List<UserDto> response = userMapper.toUserDtos(userSearchResponse.getUsers());

    return ResponseEntity.ok()
        .header(HEADER_CONTENT_RANGE, valueOf(userSearchResponse.getTotal()))
        .header(HEADER_ACCEPT_RANGE, valueOf(userSearchResponse.getMaximumLimitPerPage()))
        .body(response);
  }
}
