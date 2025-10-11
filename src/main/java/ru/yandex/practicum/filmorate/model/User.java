package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class User {
    // @Null(groups = User.OnCreate.class)
    // @NotNull(groups = User.OnUpdate.class)
    Long id;

    @NotEmpty(message = "email must not be empty")
    @NotBlank(message = "email must not consist of spaces")
    @Email(message = "E-mail  is incorrect")
    String email;

    @NotEmpty(message = "login must not be empty")
    @NotBlank(message = "login must not consist of spaces")
    @Pattern(regexp = "\\S+", message = "login не должен содержать пробелы")
    String login;

    String name;

    @NotNull(message = "birthday must not be empty")
    @Past(message = "birthday must be earlier than the current time point")
    ZonedDateTime birthday;


    public interface OnCreate {
    }

    public interface OnUpdate {
    }
}
