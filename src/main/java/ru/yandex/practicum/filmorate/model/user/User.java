package ru.yandex.practicum.filmorate.model.user;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {
    private Long id;

    private String email;
    private String login;
    private String name;
    private LocalDate birthday;

    private Set<Long> friends = new HashSet<>();
    private Set<Long> likedFilm = new HashSet<>();


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

