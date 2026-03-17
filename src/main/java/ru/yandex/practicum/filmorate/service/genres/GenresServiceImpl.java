package ru.yandex.practicum.filmorate.service.genres;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.genres.GenresStorage;
import ru.yandex.practicum.filmorate.dto.genre.response.GenreResponseDto;
import ru.yandex.practicum.filmorate.exception.notFound.GenreNotFoundException;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GenresServiceImpl implements GenresService {
    private final GenresStorage genresStorage;

    public Set<GenreResponseDto> getAll() {
        return genresStorage.getAll().values().stream()
                .map(GenreMapper::toResponseDto)
                .collect(Collectors.toSet());
    }

    public GenreResponseDto getById(long id) {
        return GenreMapper.toResponseDto(genresStorage.getById(id).orElseThrow(
                () -> new GenreNotFoundException("Genre c id=" + id + " не найден.")
        ));
    }
}
