package ru.yandex.practicum.filmorate.service.genres;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.genre.genres.GenresStorage;
import ru.yandex.practicum.filmorate.dto.genre.response.GenreResponseDto;
import ru.yandex.practicum.filmorate.exception.notFound.GenreNotFoundException;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GenresServiceImpl implements GenresService {
    private final GenresStorage genresStorage;

    public List<GenreResponseDto> getAll() {
        return genresStorage.getAll().values().stream()
                .sorted(Comparator.comparingLong(Genre::id))
                .map(GenreMapper::toResponseDto)
                .toList();
    }

    public GenreResponseDto getById(long id) {
        return GenreMapper.toResponseDto(genresStorage.getById(id).orElseThrow(
                () -> new GenreNotFoundException("Genre c id=" + id + " не найден.")
        ));
    }
}
