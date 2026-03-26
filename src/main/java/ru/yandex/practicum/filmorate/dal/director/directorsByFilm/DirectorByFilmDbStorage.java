package ru.yandex.practicum.filmorate.dal.director.directorsByFilm;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Set;

@Repository
@Slf4j
@RequiredArgsConstructor
public class DirectorByFilmDbStorage implements DirectorByFilmStorage {

    @Override //TODO
    public void save(long filmId, Set<Long> directorIds) {

    }

    @Override //TODO
    public void update(long filmId, Set<Long> directorIds) {

    }

    @Override //TODO
    public Map<Long, Set<Long>> getByFilmIds(Set<Long> filmIds) {
        return Map.of();
    }

    @Override //TODO
    public Set<Long> getByDirectorId(Long directorId) {
        return Set.of();
    }

    @Override //TODO
    public Set<Long> getUniqueDirectorIdsByFilmsIds(Set<Long> filmIds) {
        return Set.of();
    }
}
