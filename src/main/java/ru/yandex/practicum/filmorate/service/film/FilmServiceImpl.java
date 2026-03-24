package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.dal.film.FilmStorage;
import ru.yandex.practicum.filmorate.dal.genres.GenresStorage;
import ru.yandex.practicum.filmorate.dal.genresByFilms.GenresByFilmsDbStorage;
import ru.yandex.practicum.filmorate.dal.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.dal.user.UserStorage;
import ru.yandex.practicum.filmorate.dto.film.request.FilmRequestCreateDto;
import ru.yandex.practicum.filmorate.dto.film.request.FilmRequestUpdateDto;
import ru.yandex.practicum.filmorate.dto.film.response.FilmResponseDto;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.exception.notFound.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.notFound.GenreNotFoundException;
import ru.yandex.practicum.filmorate.exception.notFound.MpaNotFoundException;
import ru.yandex.practicum.filmorate.exception.notFound.UserNotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

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
    private final UserStorage userStorage;

    @Override
    @Transactional
    public FilmResponseDto createFilm(FilmRequestCreateDto dto) {

        Film req = filmMapper.toEntity(dto);

        if (req.getMpa() != null) {
            req.setMpa(mpaStorage.getById(req.getMpa().id()).orElseThrow(
                    () -> new MpaNotFoundException("Не корректно заданный rating"))
            );
        }

        if (!req.getGenres().isEmpty()) {
            req.setGenres(genresStorage.getByIds(req.getGenres().keySet())
            );

            if (req.getGenres().size() != dto.genres().size()) {
                throw new GenreNotFoundException("Не все заданные жанры были найдены");
            }
        }

        Film r = filmStorage.save(req);
        if (req.getMpa() != null) {
            r.setMpa(req.getMpa());
        }
        if (!req.getGenres().isEmpty()) {
            r.setGenres(req.getGenres());
        }

        if (!req.getGenres().isEmpty()) {
            genresByFilmsDbStorage.save(r.getId(), r.getGenres().keySet()
            );
        }
        log.info("save() – id={}, name={}, description={}, releaseDate={}, duration={}",
                r.getId(), r.getName(), r.getDescription(),
                r.getReleaseDate(), r.getDuration());

        return filmMapper.toResponseDto(r);
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


        if (req.getMpa() != null) {
            existing.setMpa(mpaStorage.getById(req.getMpa().id()).orElseThrow(
                    () -> new MpaNotFoundException("Не корректно заданный rating"))
            );
        }
        if (!req.getGenres().isEmpty()) {
            existing.setGenres(genresStorage.getByIds(req.getGenres().keySet()));

            if (existing.getGenres().size() != req.getGenres().size()) {
                throw new ValidationException("Не все заданные жанры были найдены");
            }
        }

        filmStorage.update(existing);
        genresByFilmsDbStorage.update(filmId, existing.getGenres().keySet());

        log.info("update() – id={}, name={}, description={}, releaseDate={}, duration={}",
                existing.getId(), existing.getName(), existing.getDescription(), existing.getReleaseDate(), existing.getDuration());

        return filmMapper.toResponseDto(existing);
    }

    @Override
    public List<FilmResponseDto> getAllFilms() {

        List<FilmResponseDto> result = prepareFilmsWithGenresAndMpa(filmStorage.getAll());

        log.debug("getAll() – total={}", result.size());
        return result;
    }

    @Override
    public List<FilmResponseDto> getPopularFilms(int count) {

        List<FilmResponseDto> result = prepareFilmsWithGenresAndMpa(filmStorage.getPopularFilms(count));

        log.debug("getPopularFilms() – total={}", result.size());
        return result;
    }

    @Override
    public List<FilmResponseDto> getCommonFilms(long userId, long friendId) {
        if (!userStorage.existsById(userId)) {
            throw new UserNotFoundException("User c id=" + userId + " не найден.");
        }

        if (!userStorage.existsById(friendId)) {
            throw new UserNotFoundException("User c id=" + friendId + " не найден.");
        }

        List<FilmResponseDto> result = prepareFilmsWithGenresAndMpa(filmStorage.getCommonFilms(userId, friendId));

        log.debug("getCommonFilms() – total={}", result.size());
        return result;
    }

    @Override
    public FilmResponseDto getById(Long filmId) {
        Film film = filmStorage.getById(filmId).orElseThrow(
                () -> new FilmNotFoundException("Film c id=" + filmId + " не найден."));

        if (film.getMpa() != null) {
            film.setMpa(mpaStorage.getById(film.getMpa().id()).orElseThrow(
                    () -> new MpaNotFoundException("Не корректно заданный rating"))
            );
        }

        film.setGenres(genresStorage.getByIds(genresByFilmsDbStorage.getByFilmId(filmId)));

        log.info("update() – id={}, name={}, description={}, releaseDate={}, duration={}",
                film.getId(), film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration());
        return filmMapper.toResponseDto(film);
    }

    @Override
    public void deleteById(long id) {
        if (filmStorage.deleteById(id) == 0) {
            throw new FilmNotFoundException("Фильм с id = " + id + " не найден");
        }
    }


    public List<FilmResponseDto> prepareFilmsWithGenresAndMpa(List<Film> films) {

        Map<Long, Genre> genres = genresStorage.getAll();
        Map<Long, Mpa> mpa = mpaStorage.getAll();

        Map<Long, Set<Long>> filmGenresMap = genresByFilmsDbStorage.getByFilmIds(
                films.stream()
                        .map(Film::getId)
                        .collect(Collectors.toSet())
        );

        if (filmGenresMap == null || genres == null) {
            return films.stream()
                    .map(filmMapper::toResponseDto)
                    .toList();
        }

        return films.stream()
                .map(f -> {
                    Long filmId = f.getId();

                    f.setGenres(filmGenresMap.getOrDefault(filmId, Set.of()).stream()
                            .map(genres::get)
                            .collect(Collectors.toMap(
                                    Genre::id,
                                    Function.identity()))
                    );

                    if (f.getMpa() != null) {
                        f.setMpa(mpa.get(f.getMpa().id()));
                    }

                    return filmMapper.toResponseDto(f);
                })
                .filter(Objects::nonNull)
                .toList();
    }

    public List<FilmResponseDto> getMostPopularFilms(long count, long genreId, long year) {
        List<Film> films = filmStorage.getMostPopularFilms(count, genreId, year);

        if (films.isEmpty()) {
            throw new FilmNotFoundException("Фильмы не найдены");
        }

        return films.stream().map(filmMapper::toResponseDto).toList();
    }
}
