package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.dal.film.FilmStorage;
import ru.yandex.practicum.filmorate.dal.genres.GenresStorage;
import ru.yandex.practicum.filmorate.dal.genresByFilms.GenresByFilmsDbStorage;
import ru.yandex.practicum.filmorate.dal.likes.LikesStorage;
import ru.yandex.practicum.filmorate.dal.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.dto.film.request.FilmRequestCreateDto;
import ru.yandex.practicum.filmorate.dto.film.request.FilmRequestUpdateDto;
import ru.yandex.practicum.filmorate.dto.film.response.FilmResponseDto;
import ru.yandex.practicum.filmorate.dto.genre.response.GenreResponseDto;
import ru.yandex.practicum.filmorate.dto.mpa.response.MpaResponseDto;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.exception.notFound.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.notFound.GenreNotFoundException;
import ru.yandex.practicum.filmorate.exception.notFound.MpaNotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.film.Film;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmServiceImpl implements FilmService {
    private final FilmStorage filmStorage;
    private final GenresByFilmsDbStorage genresByFilmsDbStorage;
    private final GenresStorage genresStorage;
    private final MpaStorage mpaStorage;

    private final FilmMapper filmMapper;

    @Override
    @Transactional
    public FilmResponseDto createFilm(FilmRequestCreateDto dto) {

        Film req = filmMapper.toEntity(dto);

        MpaResponseDto mpaDto = null;

        if (req.getMpa() != null) {
            mpaDto = mpaStorage.getById(req.getMpa()).orElseThrow(
                    () -> new MpaNotFoundException("Не корректно заданный rating"));
        }

        Set<GenreResponseDto> genreDtos = null;
        if (!req.getGenres().isEmpty()) {
            genreDtos = genresStorage.getByIds(req.getGenres());

            if (genreDtos.size() != req.getGenres().size()) {
                throw new GenreNotFoundException("Не все заданные жанры были найдены");
            }
        }

        Film r = filmStorage.save(filmMapper.toEntity(dto));

        if (!req.getGenres().isEmpty()) {
            genresByFilmsDbStorage.save(r.getId(), req.getGenres());
        }


        log.info("save() – id={}, name={}, description={}, releaseDate={}, duration={}",
                r.getId(), r.getName(), r.getDescription(),
                r.getReleaseDate(), r.getDuration());

        return filmMapper.toResponseDto(r, genreDtos, mpaDto);
    }

    @Override
    @Transactional
    public FilmResponseDto updateFilm(FilmRequestUpdateDto dto) {
        Film req = filmMapper.toEntity(dto);

        Long filmId = req.getId();


        Film existing = filmStorage.getById(filmId).orElseThrow(
                () -> new FilmNotFoundException("Film c id=" + filmId + " не найден.")
        );

        existing.setName(req.getName());
        existing.setDescription(req.getDescription());
        existing.setReleaseDate(req.getReleaseDate());
        existing.setDuration(req.getDuration());


        MpaResponseDto mpaDto = null;
        if (req.getMpa() != null) {
            mpaDto = mpaStorage.getById(req.getMpa()).orElseThrow(
                    () -> new MpaNotFoundException("Не корректно заданный rating"));
            existing.setMpa(req.getMpa());
        }

        Set<GenreResponseDto> genreDtos = null;
        if (!req.getGenres().isEmpty()) {
            genreDtos = genresStorage.getByIds(req.getGenres());

            if (genreDtos.size() != req.getGenres().size()) {
                throw new ValidationException("Не все заданные жанры были найдены");
            }
        }

        filmStorage.update(existing);

        genresByFilmsDbStorage.update(filmId, req.getGenres());


        log.info("update() – id={}, name={}, description={}, releaseDate={}, duration={}",
                dto.id(), dto.name(), dto.description(), dto.releaseDate(), dto.duration());

        return filmMapper.toResponseDto(existing, genreDtos, mpaDto);
    }

    @Override
    public List<FilmResponseDto> getAllFilms() {
        Map<Long, Film> films = filmStorage.getAll();
        Map<Long, GenreResponseDto> genres = genresStorage.getAll();
        Map<Long, MpaResponseDto> mpas = mpaStorage.getAll().stream().collect(Collectors.toMap(
                MpaResponseDto::id,
                Function.identity()
        ));

        Map<Long, Set<Long>> filmGenresMap = genresByFilmsDbStorage.getAll();

        if (genres == null || filmGenresMap == null) {
            return films.values().stream()
                    .map(f -> filmMapper.toResponseDto(f, null, mpas.get(f.getMpa())))
                    .toList();
        }

        List<FilmResponseDto> result = films.entrySet().stream()
                .map(entry -> {
                            Long filmId = entry.getKey();
                            Film film = entry.getValue();

                            Set<GenreResponseDto> filmGenres =
                                    filmGenresMap.getOrDefault(filmId, Set.of()).stream()
                                            .map(genres::get)
                                            .collect(Collectors.toCollection(HashSet::new));

                            MpaResponseDto mpa = mpas.get(film.getMpa());

                            return filmMapper.toResponseDto(film, filmGenres, mpa);
                        }
                ).toList();

        log.debug("getAll() – total={}", result.size());
        return result;
    }

    @Override
    public List<FilmResponseDto> getPopularFilms(int count) {
        List<Film> films = filmStorage.getPopularFilms(count);

        Map<Long, GenreResponseDto> genres = genresStorage.getAll();
        Map<Long, MpaResponseDto> mpas = mpaStorage.getAll().stream().collect(Collectors.toMap(
                MpaResponseDto::id,
                Function.identity()
        ));

        Map<Long, Set<Long>> filmGenresMap = genresByFilmsDbStorage.getByfilmIds(films.stream()
                .map(Film::getId)
                .collect(Collectors.toSet())
        );

        List<FilmResponseDto> result = films.stream()
                .map(f -> {

                    Long filmId = f.getId();
                    Set<GenreResponseDto> filmGenres =
                            filmGenresMap.getOrDefault(filmId, Set.of()).stream()
                                    .map(genres::get)
                                    .collect(Collectors.toCollection(HashSet::new));

                    MpaResponseDto mpa = mpas.get(f.getMpa());

                    return filmMapper.toResponseDto(f, filmGenres, mpa);
                })
                .filter(Objects::nonNull)
                .toList();

        log.debug("getPopularFilms() – total={}", result.size());
        return result;
    }

    @Override
    public FilmResponseDto getById(Long filmId) {
        Film film = filmStorage.getById(filmId).orElseThrow(
                () -> new FilmNotFoundException("Film c id=" + filmId + " не найден."));

        MpaResponseDto mpaDto = null;
        if (film.getMpa() != null) {
            mpaDto = mpaStorage.getById(film.getMpa()).orElseThrow(
                    () -> new MpaNotFoundException("Не корректно заданный rating"));
        }


        Set<GenreResponseDto> genreDtos = genresStorage.getByIds(genresByFilmsDbStorage.getByFilmId(filmId));

        log.info("update() – id={}, name={}, description={}, releaseDate={}, duration={}",
                film.getId(), film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration());
        return filmMapper.toResponseDto(film, genreDtos, mpaDto);
    }

}
