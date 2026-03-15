package ru.yandex.practicum.filmorate.service.genres;

import ru.yandex.practicum.filmorate.dto.genre.response.GenreResponseDto;

import java.util.List;

public interface GenresService {
    List<GenreResponseDto> getAll();

    GenreResponseDto getById(long id);
}
