package ru.yandex.practicum.filmorate.dto.user.request;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record UserRequestCreateDto(
        @NotEmpty(message = "email не может быть пустым")
        @NotBlank(message = "email не должно состоять из пробелов")
        @Email(message = "E-mail  is incorrect")
         String email,

        @NotBlank(message = "login не должно состоять из пробелов")
        @Pattern(regexp = "\\S+", message = "login не должен содержать пробелы")
        String login,


        String name,

        @NotNull(message = "birthday не может быть пустым")
        @Past(message = "birthday должен быть раньше текущего момента времени")
        LocalDate birthday
) implements UserRequestData {
}
