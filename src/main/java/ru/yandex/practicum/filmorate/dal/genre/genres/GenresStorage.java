package ru.yandex.practicum.filmorate.dal.genre.genres;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface GenresStorage {
    Map<Long, Genre> getAll();

    Optional<Genre> getById(long id);

    Map<Long, Genre> getByIds(Set<Long> ids);
}
