package ru.yandex.practicum.filmorate.mapper;


import org.springframework.stereotype.Component;

import ru.yandex.practicum.filmorate.dal.user.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.dto.user.request.UserRequestCreateDto;
import ru.yandex.practicum.filmorate.dto.user.request.UserRequestData;
import ru.yandex.practicum.filmorate.dto.user.request.UserRequestUpdateDto;
import ru.yandex.practicum.filmorate.dto.user.response.UserResponseDto;


import ru.yandex.practicum.filmorate.model.user.User;



@Component
public class UserMapper {


    public static UserResponseDto toResponseDto(
            User user
    ) {

        return new UserResponseDto(
                user.getId(),
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday()
        );
    }

    public static User toEntity(UserRequestUpdateDto dto) {
        if (dto == null) return null;

        User user = new User();

        user.setId(dto.id());
        applyToEntity(dto, user);
        return user;
    }

    public static User toEntity(UserRequestCreateDto dto) {
        if (dto == null) return null;

        User user = new User();

        applyToEntity(dto, user);
        return user;
    }


    public static void applyToEntity(UserRequestData dto, User target) {
        if (dto == null || target == null) return;

        target.setEmail(dto.email());
        target.setLogin(dto.login());
        target.setName(dto.name());
        target.setBirthday(dto.birthday());

    }
}

