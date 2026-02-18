package ru.yandex.practicum.filmorate.dal.genres;

import ru.yandex.practicum.filmorate.dto.genre.response.GenreResponseDto;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface GenresStorage {
    Map<Long, GenreResponseDto> getAll();

    Optional<GenreResponseDto> getById(long id);

    LinkedHashSet<GenreResponseDto> getByIds(Set<Long> ids);
}
