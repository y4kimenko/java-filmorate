package ru.yandex.practicum.filmorate.service.user;

import ru.yandex.practicum.filmorate.dto.user.request.UserRequestCreateDto;
import ru.yandex.practicum.filmorate.dto.user.request.UserRequestUpdateDto;
import ru.yandex.practicum.filmorate.dto.user.response.UserResponseDto;

import java.util.List;


public interface UserService {
    UserResponseDto create(UserRequestCreateDto dto);

    UserResponseDto update(UserRequestUpdateDto dto);

    List<UserResponseDto> getAll();

    void deleteById(long id);

    UserResponseDto getUserById(long id);
}
