package ru.yandex.practicum.filmorate.model;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;

import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {
    private Long id;

    @NotEmpty(message = "email не может быть пустым")
    @NotBlank(message = "email не должно состоять из пробелов")
    @Email(message = "E-mail  is incorrect")
    private String email;

    @NotEmpty(message = "login не может быть пустым")
    @NotBlank(message = "login не должно состоять из пробелов")
    @Pattern(regexp = "\\S+", message = "login не должен содержать пробелы")
    private String login;


    private String name;

    @NotNull(message = "birthday не может быть пустым")
    @Past(message = "birthday должен быть раньше текущего момента времени")
    private LocalDate birthday;

    private Set<Long> friends = new HashSet<>();


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

}
