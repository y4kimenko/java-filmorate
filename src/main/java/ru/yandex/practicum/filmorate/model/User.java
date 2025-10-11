package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.validation.annotation.Validated;


import java.time.ZonedDateTime;

@Data
public class User {
    long id;

    @NotBlank(message = "email must not be empty")
    @Email(message = "E-mail  is incorrect")
    String email;

    @NotBlank(message = "login must not be empty")
    @Pattern(regexp = "\\S+", message = "login не должен содержать пробелы")
    String login;

    String name;

    @NotNull(message = "birthday must not be empty")
    @Past(message = "birthday must be earlier than the current time point")
    ZonedDateTime birthday;
}
