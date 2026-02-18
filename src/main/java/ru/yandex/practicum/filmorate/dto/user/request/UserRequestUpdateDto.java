package ru.yandex.practicum.filmorate.dto.user.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDate;

public record UserRequestUpdateDto(
        @NotNull(message = "id не может быть пустым")
        @PositiveOrZero(message = "id не может быть меньше нуля")
        Long id,

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
