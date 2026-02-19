package ru.yandex.practicum.filmorate.mapper;

import ru.yandex.practicum.filmorate.dto.genre.response.GenreResponseDto;
import ru.yandex.practicum.filmorate.model.Genre;

public class GenreMapper {
    public static GenreResponseDto toResponseDto(Genre genre) {
        return new GenreResponseDto(genre.id(), genre.name());
    }
}
