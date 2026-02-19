package ru.yandex.practicum.filmorate.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.film.request.FilmRequestCreateDto;
import ru.yandex.practicum.filmorate.dto.film.request.FilmRequestData;
import ru.yandex.practicum.filmorate.dto.film.request.FilmRequestUpdateDto;
import ru.yandex.practicum.filmorate.dto.film.response.FilmResponseDto;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FilmMapper {
    public FilmResponseDto toResponseDto(Film film) {
        return new FilmResponseDto(
                film.getId(),
                film.getName(),
                film.getGenres().entrySet().stream()
                        .sorted(Map.Entry.comparingByKey())
                        .map(entry -> GenreMapper.toResponseDto(entry.getValue()))
                        .toList(),
                MpaMapper.toResponseDto(film.getMpa()),
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


    public void applyToEntity(FilmRequestData dto, Film film) {
        film.setName(dto.name());
        film.setDescription(dto.description());
        film.setReleaseDate(dto.releaseDate());
        film.setDuration(dto.duration());

        // mpa приходит как MpaRequestDto(long id)
        if (dto.mpa() != null) {
            film.setMpa(new Mpa(dto.mpa().id(), null));
        } else {
            film.setMpa(null);
        }

        // genres чаще всего приходят как Set<Long> или Set<Integer> id
        if (dto.genres() != null) {
            Map<Long, Genre> genres = dto.genres().stream()
                    .map(req -> new Genre(req.id(), null))
                    .collect(Collectors.toMap(
                            Genre::id,
                            Function.identity())
                    );
            film.setGenres(genres);
        } else {
            film.setGenres(new HashMap<>());
        }
    }
}
