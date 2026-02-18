package ru.yandex.practicum.filmorate.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.film.request.FilmRequestCreateDto;
import ru.yandex.practicum.filmorate.dto.film.request.FilmRequestData;
import ru.yandex.practicum.filmorate.dto.film.request.FilmRequestUpdateDto;
import ru.yandex.practicum.filmorate.dto.genre.request.GenreRequestDto;
import ru.yandex.practicum.filmorate.dto.film.response.FilmResponseDto;
import ru.yandex.practicum.filmorate.dto.genre.response.GenreResponseDto;
import ru.yandex.practicum.filmorate.dto.mpa.response.MpaResponseDto;
import ru.yandex.practicum.filmorate.model.film.Film;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class FilmMapper {

    public FilmResponseDto toResponseDto(
            Film film,
            Set<GenreResponseDto> genres,
            MpaResponseDto rating
    ) {

        return new FilmResponseDto(
                film.getId(),
                film.getName(),
                genres,
                rating,
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration()
        );
    }


    public Film toEntity(FilmRequestUpdateDto dto) {
        if (dto == null) return null;

        Film film = new Film();

        film.setId(dto.id());
        applyToEntity(dto, film);
        return film;
    }

    public Film toEntity(FilmRequestCreateDto dto) {
        if (dto == null) return null;

        Film film = new Film();

        applyToEntity(dto, film);
        return film;
    }


    public void applyToEntity(FilmRequestData dto, Film target) {
        if (dto == null || target == null) return;

        target.setName(dto.name());
        target.setDescription(dto.description());
        target.setReleaseDate(dto.releaseDate());
        target.setDuration(dto.duration());
        if (dto.mpa() != null) {
            target.setMpa(dto.mpa().id());
        }

        if (dto.genres() != null) {
            target.setGenres((HashSet<Long>) dto.genres().stream()
                    .map(GenreRequestDto::id)
                    .collect(Collectors.toSet()));
        } else {
            target.setGenres(new LinkedHashSet<>());
        }

    }
}
