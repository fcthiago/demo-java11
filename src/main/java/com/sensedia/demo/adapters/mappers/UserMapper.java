package com.sensedia.demo.adapters.mappers;

import com.sensedia.demo.adapters.dtos.UserCreationDto;
import com.sensedia.demo.adapters.dtos.UserResponseDto;
import com.sensedia.demo.adapters.dtos.UserUpdateDto;
import com.sensedia.demo.domains.User;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
@DecoratedWith(UserDecoratorMapper.class)
public interface UserMapper {
  User toUser(UserCreationDto userCreationDto);

  User toUser(UserUpdateDto userUpdateDto);

  UserResponseDto toUserResponseDto(User user);

  List<UserResponseDto> toUserResponseDtos(List<User> users);
}
