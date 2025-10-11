package ru.yandex.practicum.filmorate.model;


import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

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
    LocalDate birthday;


    public void setLogin(String email) {
        this.login = trimToNull(email);
        // Если name пуст – подставляем свежий email
        if (isBlank(this.name)) {
            this.name = this.login;
        }
    }

    public void setName(String name) {
        String n = trimToNull(name);
        // Если прислали пустое name – берём текущий email
        this.name = (n == null) ? this.login : n;
    }

    // ==== УТИЛИТЫ ====

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private static String trimToNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }


    public interface OnCreate {
    }

    public interface OnUpdate {
    }
}
