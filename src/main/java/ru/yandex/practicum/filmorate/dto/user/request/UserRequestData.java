package ru.yandex.practicum.filmorate.dto.user.request;

import java.time.LocalDate;

public interface UserRequestData {
    String email();

    String login();

    String name();

    LocalDate birthday();
}
