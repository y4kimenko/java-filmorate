package ru.yandex.practicum.filmorate.dal.genre.genresByFilms;

import java.util.Map;
import java.util.Set;

public interface GenresByFilmsStorage {
    void save(long filmId, Set<Long> genres);

    void update(long filmId, Set<Long> genres);

    Map<Long, Set<Long>> getByFilmIds(Set<Long> filmIds);

    Set<Long> getByFilmId(Long filmId);
}
