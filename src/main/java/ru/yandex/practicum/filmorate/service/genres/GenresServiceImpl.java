package ru.yandex.practicum.filmorate.service.genres;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.genres.GenresStorage;
import ru.yandex.practicum.filmorate.dto.genre.response.GenreResponseDto;
import ru.yandex.practicum.filmorate.exception.notFound.GenreNotFoundException;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class GenresServiceImpl implements GenresService {
    private final GenresStorage genresStorage;

    public Set<GenreResponseDto> getAll() {
        return new HashSet<>(genresStorage.getAll().values());
    }

    public GenreResponseDto getById(long id) {
        return genresStorage.getById(id).orElseThrow(() -> new GenreNotFoundException("Genre c id=" + id + " не найден."));
    }
}
