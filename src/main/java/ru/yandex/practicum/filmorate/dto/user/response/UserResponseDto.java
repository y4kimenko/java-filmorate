package ru.yandex.practicum.filmorate.dto.user.response;


import java.time.LocalDate;

public record UserResponseDto(
        Long id,
        String email,
        String login,
        String name,
        LocalDate birthday
) {
}
