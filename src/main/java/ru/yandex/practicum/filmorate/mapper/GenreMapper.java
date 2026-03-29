package ru.yandex.practicum.filmorate.mapper;

import ru.yandex.practicum.filmorate.dto.genre.request.GenreRequestDto;
import ru.yandex.practicum.filmorate.dto.genre.response.GenreResponseDto;
import ru.yandex.practicum.filmorate.model.Genre;

public class GenreMapper {
    public static GenreResponseDto toResponseDto(Genre genre) {
        return new GenreResponseDto(genre.id(), genre.name());
    }

    public static Genre toEntity(GenreRequestDto dto) {
        if (dto == null) return null;

        return new Genre(dto.id(), null);
    }
}
