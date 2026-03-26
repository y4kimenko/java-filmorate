package ru.yandex.practicum.filmorate.dal.director.directorsByFilm;

import java.util.Map;
import java.util.Set;

public interface DirectorByFilmStorage {
    void save(long filmId, Set<Long> directorIds);

    void update(long filmId, Set<Long> directorIds);

    Map<Long, Set<Long>> getByFilmIds(Set<Long> filmIds);

    Set<Long> getByDirectorId(Long directorId);

    Set<Long> getUniqueDirectorIdsByFilmsIds(Set<Long> filmIds);
}
