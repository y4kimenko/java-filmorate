package ru.yandex.practicum.filmorate.dal.mpa;


import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Map;
import java.util.Optional;


public interface MpaStorage {
    Map<Long, Mpa> getAll();

    Optional<Mpa> findById(Long id);
}
