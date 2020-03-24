package com.sensedia.demo.adapters.mappers;

import com.sensedia.demo.adapters.dtos.UserCreationDto;
import com.sensedia.demo.adapters.dtos.UserResponseDto;
import com.sensedia.demo.domains.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
  User toUser(UserCreationDto userCreationDto);

  UserResponseDto toUserResponseDto(User user);
}
