package ru.yandex.practicum.filmorate.dal.director.directors;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface DirectorStorage {
    Director save(Director dir);

    Director update(Director dir);

    Map<Long, Director> getAll();

    Optional<Director> getById(Long id);

    Map<Long, Director> getByIDs(Set<Long> ids);

    int deleteById(long id);

}
