package ru.yandex.practicum.filmorate.dal.genresByFilms;

import java.util.Map;
import java.util.Set;

public interface GenresByFilmsStorage {
    void save(long filmId, Set<Long> genres);

    void update(long filmId, Set<Long> genres);

    Map<Long, Set<Long>> getAll();

    Map<Long, Set<Long>> getByfilmIds(Set<Long> filmIds);

    Set<Long> getByFilmId(Long filmId);
}
