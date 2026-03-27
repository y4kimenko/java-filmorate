package ru.yandex.practicum.filmorate.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.film.request.FilmRequestCreateDto;
import ru.yandex.practicum.filmorate.dto.film.request.FilmRequestData;
import ru.yandex.practicum.filmorate.dto.film.request.FilmRequestUpdateDto;
import ru.yandex.practicum.filmorate.dto.film.response.FilmResponseDto;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FilmMapper {
    public static FilmResponseDto toResponseDto(Film film) {
        return new FilmResponseDto(
                film.getId(),
                film.getName(),
                film.getGenres().entrySet().stream()
                        .sorted(Map.Entry.comparingByKey())
                        .map(entry -> GenreMapper.toResponseDto(entry.getValue()))
                        .toList(),
                MpaMapper.toResponseDto(film.getMpa()),

                film.getDirectors().entrySet().stream()
                        .sorted(Map.Entry.comparingByKey())
                        .map(entry -> DirectorMapper.toResponseDto(entry.getValue()))
                        .toList(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration()
        );
    }

    public static Film toEntity(FilmRequestUpdateDto dto) {
        if (dto == null) return null;

        Film film = new Film();

        film.setId(dto.id());
        applyToEntity(dto, film);
        return film;
    }

    public static Film toEntity(FilmRequestCreateDto dto) {
        if (dto == null) return null;

        Film film = new Film();

        applyToEntity(dto, film);
        return film;
    }


    public static void applyToEntity(FilmRequestData dto, Film film) {
        film.setName(dto.name());
        film.setDescription(dto.description());
        film.setReleaseDate(dto.releaseDate());
        film.setDuration(dto.duration());


        film.setMpa(MpaMapper.toEntity(dto.mpa()));

        // genres чаще всего приходят как Set<Long> или Set<Integer> id
        if (dto.genres() != null) {
            film.setGenres(dto.genres().stream()
                    .map(GenreMapper::toEntity)
                    .collect(Collectors.toMap(
                            Genre::id,
                            Function.identity())
                    )
            );
        } else {
            film.setGenres(new HashMap<>());
        }

        if (dto.directors() != null) {
            film.setDirectors(dto.directors().stream()
                    .map(DirectorMapper::toEntity)
                    .collect(Collectors.toMap(
                            Director::getId,
                            Function.identity())
                    )
            );
        } else {
            film.setDirectors(new HashMap<>());
        }
    }
}
