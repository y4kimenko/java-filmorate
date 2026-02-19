package ru.yandex.practicum.filmorate.service;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ru.yandex.practicum.filmorate.dal.film.FilmStorage;
import ru.yandex.practicum.filmorate.dal.genres.GenresStorage;
import ru.yandex.practicum.filmorate.dal.genresByFilms.GenresByFilmsDbStorage;

import ru.yandex.practicum.filmorate.dal.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.dto.film.request.FilmRequestCreateDto;
import ru.yandex.practicum.filmorate.dto.film.request.FilmRequestUpdateDto;
import ru.yandex.practicum.filmorate.dto.film.response.FilmResponseDto;
import ru.yandex.practicum.filmorate.dto.genre.request.GenreRequestDto;
import ru.yandex.practicum.filmorate.dto.genre.response.GenreResponseDto;
import ru.yandex.practicum.filmorate.dto.mpa.request.MpaRequestDto;
import ru.yandex.practicum.filmorate.dto.mpa.response.MpaResponseDto;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.exception.notFound.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.notFound.GenreNotFoundException;
import ru.yandex.practicum.filmorate.exception.notFound.MpaNotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.service.film.FilmServiceImpl;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FilmServiceImplTests {

    @Mock private FilmStorage filmStorage;
    @Mock private GenresByFilmsDbStorage genresByFilmsDbStorage;
    @Mock private GenresStorage genresStorage;
    @Mock private MpaStorage mpaStorage;
    @Mock private FilmMapper filmMapper;

    @InjectMocks private FilmServiceImpl filmService;



    @Test
    @DisplayName("createFilm – успех: валидные mpa и жанры, жанры сохраняются в связующую таблицу")
    void createFilm_success_withGenresAndMpa() {
        FilmRequestCreateDto dto = new FilmRequestCreateDto(
                "Test Film",
                Set.of(new GenreRequestDto(1L), new GenreRequestDto(2L)),
                new MpaRequestDto(1L),
                "Description",
                LocalDate.of(2000, 1, 1),
                120
        );

        Film req = new Film();
        req.setName("Test Film");
        req.setDescription("Description");
        req.setReleaseDate(LocalDate.of(2000, 1, 1));
        req.setDuration(120);
        req.setMpa(1L);
        req.setGenres(Set.of(1L, 2L));

        Film saved = new Film();
        saved.setId(42L);
        saved.setName(req.getName());
        saved.setDescription(req.getDescription());
        saved.setReleaseDate(req.getReleaseDate());
        saved.setDuration(req.getDuration());
        saved.setMpa(req.getMpa());
        saved.setGenres(req.getGenres());

        MpaResponseDto mpaDto = new MpaResponseDto(1, "R");
        LinkedHashSet<GenreResponseDto> genreDtos = new LinkedHashSet<>(Set.of(
                new GenreResponseDto(1, "Комедия"),
                new GenreResponseDto(2, "Драма")
        ));

        FilmResponseDto expected = new FilmResponseDto(
                42L,
                "Test Film",
                genreDtos,
                mpaDto,
                "Description",
                LocalDate.of(2000, 1, 1),
                120
        );

        when(filmMapper.toEntity(any(FilmRequestCreateDto.class))).thenReturn(req);
        when(mpaStorage.getById(1L)).thenReturn(Optional.of(mpaDto));
        when(genresStorage.getByIds(Set.of(1L, 2L))).thenReturn(genreDtos);
        when(filmStorage.save(any(Film.class))).thenReturn(saved);
        when(filmMapper.toResponseDto(saved, genreDtos, mpaDto)).thenReturn(expected);

        FilmResponseDto actual = filmService.createFilm(dto);

        assertEquals(expected, actual);
        verify(filmStorage).save(any(Film.class));
        verify(genresByFilmsDbStorage).save(42L, Set.of(1L, 2L));
        verify(genresByFilmsDbStorage, never()).update(anyLong(), anySet());
    }

    @Test
    @DisplayName("createFilm – успех: жанров нет, связующая таблица не трогается")
    void createFilm_success_withoutGenres() {
        FilmRequestCreateDto dto = new FilmRequestCreateDto(
                "Test Film",
                Set.of(),
                new MpaRequestDto(1L),
                "Description",
                LocalDate.of(2000, 1, 1),
                120
        );

        Film req = new Film();
        req.setName("Test Film");
        req.setDescription("Description");
        req.setReleaseDate(LocalDate.of(2000, 1, 1));
        req.setDuration(120);
        req.setMpa(1L);
        req.setGenres(Set.of());

        Film saved = new Film();
        saved.setId(42L);
        saved.setName(req.getName());
        saved.setDescription(req.getDescription());
        saved.setReleaseDate(req.getReleaseDate());
        saved.setDuration(req.getDuration());
        saved.setMpa(req.getMpa());
        saved.setGenres(req.getGenres());

        MpaResponseDto mpaDto = new MpaResponseDto(1, "R");

        FilmResponseDto expected = new FilmResponseDto(
                42L,
                "Test Film",
                null,
                mpaDto,
                "Description",
                LocalDate.of(2000, 1, 1),
                120
        );

        when(filmMapper.toEntity(any(FilmRequestCreateDto.class))).thenReturn(req);
        when(mpaStorage.getById(1L)).thenReturn(Optional.of(mpaDto));
        when(filmStorage.save(any(Film.class))).thenReturn(saved);
        when(filmMapper.toResponseDto(saved, null, mpaDto)).thenReturn(expected);

        FilmResponseDto actual = filmService.createFilm(dto);

        assertEquals(expected, actual);
        verify(genresStorage, never()).getByIds(anySet());
        verify(genresByFilmsDbStorage, never()).save(anyLong(), anySet());
    }

    @Test
    @DisplayName("createFilm – ошибка: mpa не найден")
    void createFilm_throws_whenMpaNotFound() {
        FilmRequestCreateDto dto = new FilmRequestCreateDto(
                "Test Film",
                Set.of(),
                new MpaRequestDto(999L),
                "Description",
                LocalDate.of(2000, 1, 1),
                120
        );

        Film req = new Film();
        req.setMpa(999L);
        req.setGenres(Set.of());

        when(filmMapper.toEntity(any(FilmRequestCreateDto.class))).thenReturn(req);
        when(mpaStorage.getById(999L)).thenReturn(Optional.empty());

        assertThrows(MpaNotFoundException.class, () -> filmService.createFilm(dto));

        verify(filmStorage, never()).save(any());
        verify(genresByFilmsDbStorage, never()).save(anyLong(), anySet());
    }

    @Test
    @DisplayName("createFilm – ошибка: не все жанры найдены")
    void createFilm_throws_whenGenresNotFound() {
        FilmRequestCreateDto dto = new FilmRequestCreateDto(
                "Test Film",
                Set.of(new GenreRequestDto(1L), new GenreRequestDto(2L)),
                null,
                "Description",
                LocalDate.of(2000, 1, 1),
                120
        );

        Film req = new Film();
        req.setGenres(Set.of(1L, 2L));

        when(filmMapper.toEntity(any(FilmRequestCreateDto.class))).thenReturn(req);
        when(genresStorage.getByIds(Set.of(1L, 2L))).thenReturn(new LinkedHashSet<>(Set.of(new GenreResponseDto(1, "Комедия")))); // меньше

        assertThrows(GenreNotFoundException.class, () -> filmService.createFilm(dto));

        verify(filmStorage, never()).save(any());
        verify(genresByFilmsDbStorage, never()).save(anyLong(), anySet());
    }

    @Test
    @DisplayName("updateFilm – успех: фильм существует, поля обновляются, жанры обновляются, mpa валиден")
    void updateFilm_success() {
        FilmRequestUpdateDto dto = new FilmRequestUpdateDto(
                10L,
                "New Name",
                Set.of(new GenreRequestDto(1L)),
                new MpaRequestDto(2L),
                "New Desc",
                LocalDate.of(2001, 2, 3),
                90
        );

        Film req = new Film();
        req.setId(10L);
        req.setName("New Name");
        req.setDescription("New Desc");
        req.setReleaseDate(LocalDate.of(2001, 2, 3));
        req.setDuration(90);
        req.setMpa(2L);
        req.setGenres(Set.of(1L));

        Film existing = new Film();
        existing.setId(10L);
        existing.setName("Old Name");
        existing.setDescription("Old Desc");
        existing.setReleaseDate(LocalDate.of(1999, 1, 1));
        existing.setDuration(120);
        existing.setMpa(1L);
        existing.setGenres(Set.of(2L));

        MpaResponseDto mpaDto = new MpaResponseDto(2, "PG-13");
        LinkedHashSet<GenreResponseDto> genreDtos = new LinkedHashSet<>(Set.of(new GenreResponseDto(1, "Комедия")));

        FilmResponseDto expected = new FilmResponseDto(
                10L,
                "New Name",
                genreDtos,
                mpaDto,
                "New Desc",
                LocalDate.of(2001, 2, 3),
                90
        );

        when(filmMapper.toEntity(any(FilmRequestUpdateDto.class))).thenReturn(req);
        when(filmStorage.getById(10L)).thenReturn(Optional.of(existing));
        when(mpaStorage.getById(2L)).thenReturn(Optional.of(mpaDto));
        when(genresStorage.getByIds(Set.of(1L))).thenReturn(genreDtos);
        when(filmMapper.toResponseDto(existing, genreDtos, mpaDto)).thenReturn(expected);

        FilmResponseDto actual = filmService.updateFilm(dto);

        assertEquals(expected, actual);

        ArgumentCaptor<Film> captor = ArgumentCaptor.forClass(Film.class);
        verify(filmStorage).update(captor.capture());
        Film updated = captor.getValue();

        assertEquals(10L, updated.getId());
        assertEquals("New Name", updated.getName());
        assertEquals("New Desc", updated.getDescription());
        assertEquals(LocalDate.of(2001, 2, 3), updated.getReleaseDate());
        assertEquals(90, updated.getDuration());
        assertEquals(2L, updated.getMpa());

        verify(genresByFilmsDbStorage).update(10L, Set.of(1L));
    }

    @Test
    @DisplayName("updateFilm – ошибка: фильм не найден")
    void updateFilm_throws_whenFilmNotFound() {
        FilmRequestUpdateDto dto = new FilmRequestUpdateDto(
                10L, "New Name", Set.of(), null, "New Desc",
                LocalDate.of(2001, 2, 3), 90
        );

        Film req = new Film();
        req.setId(10L);
        req.setGenres(Set.of());

        when(filmMapper.toEntity(any(FilmRequestUpdateDto.class))).thenReturn(req);
        when(filmStorage.getById(10L)).thenReturn(Optional.empty());

        assertThrows(FilmNotFoundException.class, () -> filmService.updateFilm(dto));

        verify(filmStorage, never()).update(any());
        verify(genresByFilmsDbStorage, never()).update(anyLong(), anySet());
    }

    @Test
    @DisplayName("updateFilm – ошибка: mpa не найден")
    void updateFilm_throws_whenMpaNotFound() {
        FilmRequestUpdateDto dto = new FilmRequestUpdateDto(
                10L, "New Name", Set.of(), new MpaRequestDto(999L),
                "New Desc", LocalDate.of(2001, 2, 3), 90
        );

        Film req = new Film();
        req.setId(10L);
        req.setGenres(Set.of());
        req.setMpa(999L);

        Film existing = new Film();
        existing.setId(10L);

        when(filmMapper.toEntity(any(FilmRequestUpdateDto.class))).thenReturn(req);
        when(filmStorage.getById(10L)).thenReturn(Optional.of(existing));
        when(mpaStorage.getById(999L)).thenReturn(Optional.empty());

        assertThrows(MpaNotFoundException.class, () -> filmService.updateFilm(dto));

        verify(filmStorage, never()).update(any());
        verify(genresByFilmsDbStorage, never()).update(anyLong(), anySet());
    }

    @Test
    @DisplayName("updateFilm – ошибка: не все жанры найдены (ValidationException)")
    void updateFilm_throws_whenGenresNotFound() {
        FilmRequestUpdateDto dto = new FilmRequestUpdateDto(
                10L, "New Name", Set.of(new GenreRequestDto(1L), new GenreRequestDto(2L)),
                null, "New Desc", LocalDate.of(2001, 2, 3), 90
        );

        Film req = new Film();
        req.setId(10L);
        req.setGenres(Set.of(1L, 2L));

        Film existing = new Film();
        existing.setId(10L);

        when(filmMapper.toEntity(any(FilmRequestUpdateDto.class))).thenReturn(req);
        when(filmStorage.getById(10L)).thenReturn(Optional.of(existing));
        when(genresStorage.getByIds(Set.of(1L, 2L))).thenReturn(new LinkedHashSet<>(Set.of(new GenreResponseDto(1, "Комедия")))); // меньше

        assertThrows(ValidationException.class, () -> filmService.updateFilm(dto));

        verify(filmStorage, never()).update(any());
        verify(genresByFilmsDbStorage, never()).update(anyLong(), anySet());
    }

    @Test
    @DisplayName("getAllFilms – успех: собирает жанры по фильмам и mpa по id")
    void getAllFilms_success() {
        Film f1 = new Film();
        f1.setId(1L);
        f1.setName("A");
        f1.setDescription("DA");
        f1.setReleaseDate(LocalDate.of(2000, 1, 1));
        f1.setDuration(100);
        f1.setMpa(1L);

        Film f2 = new Film();
        f2.setId(2L);
        f2.setName("B");
        f2.setDescription("DB");
        f2.setReleaseDate(LocalDate.of(2001, 1, 1));
        f2.setDuration(110);
        f2.setMpa(2L);

        LinkedHashMap<Long, Film> films = new LinkedHashMap<>();
        films.put(1L, f1);
        films.put(2L, f2);

        Map<Long, GenreResponseDto> allGenres = new HashMap<>();
        GenreResponseDto g1 = new GenreResponseDto(1, "Комедия");
        GenreResponseDto g2 = new GenreResponseDto(2, "Драма");
        allGenres.put(1L, g1);
        allGenres.put(2L, g2);

        Set<MpaResponseDto> allMpas = Set.of(
                new MpaResponseDto(1, "G"),
                new MpaResponseDto(2, "PG")
        );

        Map<Long, Set<Long>> filmGenresMap = new HashMap<>();
        filmGenresMap.put(1L, Set.of(1L, 2L));
        filmGenresMap.put(2L, Set.of(2L));

        when(filmStorage.getAll()).thenReturn(films);
        when(genresStorage.getAll()).thenReturn(allGenres);
        when(mpaStorage.getAll()).thenReturn(allMpas);
        when(genresByFilmsDbStorage.getAll()).thenReturn(filmGenresMap);

        when(filmMapper.toResponseDto(any(Film.class), anySet(), any(MpaResponseDto.class)))
                .thenAnswer(inv -> {
                    Film film = inv.getArgument(0);
                    Set<GenreResponseDto> gs = inv.getArgument(1);
                    MpaResponseDto mpa = inv.getArgument(2);
                    return new FilmResponseDto(
                            film.getId(),
                            film.getName(),
                            gs,
                            mpa,
                            film.getDescription(),
                            film.getReleaseDate(),
                            film.getDuration()
                    );
                });

        List<FilmResponseDto> result = filmService.getAllFilms();

        assertEquals(2, result.size());

        FilmResponseDto r1 = result.get(0);
        assertEquals(1L, r1.id());
        assertEquals(Set.of(g1, g2), r1.genres());
        assertEquals(1, r1.mpa().id());

        FilmResponseDto r2 = result.get(1);
        assertEquals(2L, r2.id());
        assertEquals(Set.of(g2), r2.genres());
        assertEquals(2, r2.mpa().id());
    }

    @Test
    @DisplayName("getAllFilms – ветка: genres или filmGenresMap null, жанры в ответе null")
    void getAllFilms_branch_whenGenresNull() {
        Film f1 = new Film();
        f1.setId(1L);
        f1.setName("A");
        f1.setDescription("DA");
        f1.setReleaseDate(LocalDate.of(2000, 1, 1));
        f1.setDuration(100);
        f1.setMpa(1L);

        when(filmStorage.getAll()).thenReturn(new LinkedHashMap<>(Map.of(1L, f1)));
        when(genresStorage.getAll()).thenReturn(null);
        when(mpaStorage.getAll()).thenReturn(Set.of(new MpaResponseDto(1, "G")));
        when(genresByFilmsDbStorage.getAll()).thenReturn(Map.of(1L, Set.of(1L)));

        when(filmMapper.toResponseDto(eq(f1), isNull(), any(MpaResponseDto.class)))
                .thenReturn(new FilmResponseDto(
                        1L, "A", null, new MpaResponseDto(1, "G"),
                        "DA", LocalDate.of(2000, 1, 1), 100
                ));

        List<FilmResponseDto> result = filmService.getAllFilms();

        assertEquals(1, result.size());
        assertNull(result.get(0).genres());
    }

    @Test
    @DisplayName("getPopularFilms – успех: маппит жанры по filmIds и фильтрует null из mapper")
    void getPopularFilms_success_filtersNull() {
        Film f1 = new Film();
        f1.setId(1L);
        f1.setName("A");
        f1.setDescription("DA");
        f1.setReleaseDate(LocalDate.of(2000, 1, 1));
        f1.setDuration(100);
        f1.setMpa(1L);

        Film f2 = new Film();
        f2.setId(2L);
        f2.setName("B");
        f2.setDescription("DB");
        f2.setReleaseDate(LocalDate.of(2001, 1, 1));
        f2.setDuration(110);
        f2.setMpa(1L);

        when(filmStorage.getPopularFilms(10)).thenReturn(List.of(f1, f2));

        Map<Long, GenreResponseDto> allGenres = Map.of(
                1L, new GenreResponseDto(1, "Комедия")
        );
        when(genresStorage.getAll()).thenReturn(allGenres);

        when(mpaStorage.getAll()).thenReturn(Set.of(new MpaResponseDto(1, "G")));

        when(genresByFilmsDbStorage.getByfilmIds(Set.of(1L, 2L)))
                .thenReturn(Map.of(
                        1L, Set.of(1L),
                        2L, Set.of(1L)
                ));

        when(filmMapper.toResponseDto(eq(f1), anySet(), any(MpaResponseDto.class)))
                .thenReturn(new FilmResponseDto(
                        1L, "A",
                        Set.of(allGenres.get(1L)),
                        new MpaResponseDto(1, "G"),
                        "DA", LocalDate.of(2000, 1, 1), 100
                ));

        when(filmMapper.toResponseDto(eq(f2), anySet(), any(MpaResponseDto.class)))
                .thenReturn(null);

        List<FilmResponseDto> result = filmService.getPopularFilms(10);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).id());
        verify(genresByFilmsDbStorage).getByfilmIds(Set.of(1L, 2L));
    }

    @Test
    @DisplayName("getById – успех")
    void getById_success() {
        Film film = new Film();
        film.setId(10L);
        film.setName("A");
        film.setDescription("DA");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(100);
        film.setMpa(1L);

        MpaResponseDto mpaDto = new MpaResponseDto(1, "G");
        Set<Long> genreIds = Set.of(1L);
        LinkedHashSet<GenreResponseDto> genreDtos = new LinkedHashSet<>(Set.of(new GenreResponseDto(1, "Комедия")));

        FilmResponseDto expected = new FilmResponseDto(
                10L, "A", genreDtos, mpaDto,
                "DA", LocalDate.of(2000, 1, 1), 100
        );

        when(filmStorage.getById(10L)).thenReturn(Optional.of(film));
        when(mpaStorage.getById(1L)).thenReturn(Optional.of(mpaDto));
        when(genresByFilmsDbStorage.getByFilmId(10L)).thenReturn(genreIds);
        when(genresStorage.getByIds(genreIds)).thenReturn(genreDtos);
        when(filmMapper.toResponseDto(film, genreDtos, mpaDto)).thenReturn(expected);

        FilmResponseDto actual = filmService.getById(10L);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("getById – ошибка: фильм не найден")
    void getById_throws_whenFilmNotFound() {
        when(filmStorage.getById(10L)).thenReturn(Optional.empty());
        assertThrows(FilmNotFoundException.class, () -> filmService.getById(10L));
        verifyNoInteractions(mpaStorage, genresByFilmsDbStorage, genresStorage, filmMapper);
    }

    @Test
    @DisplayName("getById – ошибка: mpa не найден")
    void getById_throws_whenMpaNotFound() {
        Film film = new Film();
        film.setId(10L);
        film.setMpa(999L);

        when(filmStorage.getById(10L)).thenReturn(Optional.of(film));
        when(mpaStorage.getById(999L)).thenReturn(Optional.empty());

        assertThrows(MpaNotFoundException.class, () -> filmService.getById(10L));
        verify(genresByFilmsDbStorage, never()).getByFilmId(anyLong());
        verify(genresStorage, never()).getByIds(anySet());
    }
}
