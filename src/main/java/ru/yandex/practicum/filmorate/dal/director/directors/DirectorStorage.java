package ru.yandex.practicum.filmorate.dal.director.directors;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Map;
import java.util.Optional;

public interface DirectorStorage {
    Director save(Director dir);

    Director update(Director dir);

    Map<Long, Director> getAll();

    Optional<Director> getById(Long id);

    int deleteById(long id);

}
