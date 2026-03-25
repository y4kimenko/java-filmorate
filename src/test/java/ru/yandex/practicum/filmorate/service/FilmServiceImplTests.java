package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.filmorate.dal.film.FilmStorage;
import ru.yandex.practicum.filmorate.dal.genre.genres.GenresStorage;
import ru.yandex.practicum.filmorate.dal.genre.genresByFilms.GenresByFilmsDbStorage;
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
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.film.FilmServiceImpl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FilmServiceImplTests {

    @Mock
    private FilmStorage filmStorage;
    @Mock
    private GenresByFilmsDbStorage genresByFilmsDbStorage;
    @Mock
    private GenresStorage genresStorage;
    @Mock
    private MpaStorage mpaStorage;
    @Mock
    private FilmMapper filmMapper;

    @InjectMocks
    private FilmServiceImpl filmService;

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


        Mpa mpaFromDb = new Mpa(1L, "R");
        Map<Long, Genre> genresFromDb = new HashMap<>(Map.of(
                1L, new Genre(1L, "Комедия"),
                2L, new Genre(2L, "Драма")
        ));

        Film saved = new Film();
        saved.setId(42L);
        saved.setName(dto.name());
        saved.setDescription(dto.description());
        saved.setReleaseDate(dto.releaseDate());
        saved.setDuration(dto.duration());
        // важно: сервис сам поставит сюда mpa и genres после save

        FilmResponseDto expected = new FilmResponseDto(
                42L,
                "Test Film",
                List.of(new GenreResponseDto(1, "Комедия"), new GenreResponseDto(2, "Драма")),
                new MpaResponseDto(1, "R"),
                "Description",
                LocalDate.of(2000, 1, 1),
                120
        );


        when(mpaStorage.getById(1L)).thenReturn(Optional.of(mpaFromDb));
        when(genresStorage.getByIds(Set.of(1L, 2L))).thenReturn(genresFromDb);
        when(filmStorage.save(any(Film.class))).thenReturn(saved);

        FilmResponseDto actual = filmService.createFilm(dto);

        assertEquals(expected, actual);

        ArgumentCaptor<Film> saveCaptor = ArgumentCaptor.forClass(Film.class);
        verify(filmStorage).save(saveCaptor.capture());
        Film toSave = saveCaptor.getValue();

        assertNotNull(toSave.getMpa());
        assertEquals(1L, toSave.getMpa().id());
        assertEquals("R", toSave.getMpa().name());

        assertNotNull(toSave.getGenres());
        assertEquals(2, toSave.getGenres().size());
        assertEquals("Комедия", toSave.getGenres().get(1L).name());
        assertEquals("Драма", toSave.getGenres().get(2L).name());

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

        Mpa mpaFromDb = new Mpa(1L, "R");

        Film saved = new Film();
        saved.setId(42L);
        saved.setName(dto.name());
        saved.setDescription(dto.description());
        saved.setReleaseDate(dto.releaseDate());
        saved.setDuration(dto.duration());


        FilmResponseDto expected = new FilmResponseDto(
                42L,
                "Test Film",
                List.of(),
                new MpaResponseDto(1, "R"),
                "Description",
                LocalDate.of(2000, 1, 1),
                120
        );


        when(mpaStorage.getById(1L)).thenReturn(Optional.of(mpaFromDb));
        when(filmStorage.save(any(Film.class))).thenReturn(saved);

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

        when(mpaStorage.getById(999L)).thenReturn(Optional.empty());

        assertThrows(MpaNotFoundException.class, () -> filmService.createFilm(dto));

        verify(filmStorage, never()).save(any());
        verify(genresByFilmsDbStorage, never()).save(anyLong(), anySet());
    }

    @Test
    @DisplayName("createFilm – ошибка: не все жанры найдены (GenreNotFoundException)")
    void createFilm_throws_whenGenresNotFound() {
        FilmRequestCreateDto dto = new FilmRequestCreateDto(
                "Test Film",
                Set.of(new GenreRequestDto(1L), new GenreRequestDto(2L)),
                null,
                "Description",
                LocalDate.of(2000, 1, 1),
                120
        );


        when(genresStorage.getByIds(Set.of(1L, 2L)))
                .thenReturn(new HashMap<>(Map.of(1L, new Genre(1L, "Комедия")))); // меньше, чем dto.genres().size()

        assertThrows(GenreNotFoundException.class, () -> filmService.createFilm(dto));

        verify(filmStorage, never()).save(any());
        verify(genresByFilmsDbStorage, never()).save(anyLong(), anySet());
    }

    @Test
    @DisplayName("updateFilm – успех: фильм существует, поля обновляются, mpa и жанры валидны")
    void updateFilm_success_withGenresAndMpa() {
        FilmRequestUpdateDto dto = new FilmRequestUpdateDto(
                10L,
                "New Name",
                Set.of(new GenreRequestDto(1L)),
                new MpaRequestDto(2L),
                "New Desc",
                LocalDate.of(2001, 2, 3),
                90
        );


        Film existing = new Film();
        existing.setId(10L);
        existing.setName("Old Name");
        existing.setDescription("Old Desc");
        existing.setReleaseDate(LocalDate.of(1999, 1, 1));
        existing.setDuration(120);
        existing.setMpa(new Mpa(1L, "G"));
        existing.setGenres(new HashMap<>());

        Mpa mpaFromDb = new Mpa(2L, "PG-13");
        Map<Long, Genre> genresFromDb = new HashMap<>(Map.of(
                1L, new Genre(1L, "Комедия")
        ));

        FilmResponseDto expected = new FilmResponseDto(
                10L,
                "New Name",
                List.of(new GenreResponseDto(1, "Комедия")),
                new MpaResponseDto(2, "PG-13"),
                "New Desc",
                LocalDate.of(2001, 2, 3),
                90
        );

        when(filmStorage.getById(10L)).thenReturn(Optional.of(existing));
        when(mpaStorage.getById(2L)).thenReturn(Optional.of(mpaFromDb));
        when(genresStorage.getByIds(Set.of(1L))).thenReturn(genresFromDb);

        FilmResponseDto actual = filmService.updateFilm(dto);

        assertEquals(expected, actual);

        ArgumentCaptor<Film> updateCaptor = ArgumentCaptor.forClass(Film.class);
        verify(filmStorage).update(updateCaptor.capture());
        Film updated = updateCaptor.getValue();

        assertEquals(10L, updated.getId());
        assertEquals("New Name", updated.getName());
        assertEquals("New Desc", updated.getDescription());
        assertEquals(LocalDate.of(2001, 2, 3), updated.getReleaseDate());
        assertEquals(90, updated.getDuration());

        assertNotNull(updated.getMpa());
        assertEquals(2L, updated.getMpa().id());
        assertEquals("PG-13", updated.getMpa().name());

        assertEquals(1, updated.getGenres().size());
        assertEquals("Комедия", updated.getGenres().get(1L).name());

        verify(genresByFilmsDbStorage).update(10L, Set.of(1L));
    }

    @Test
    @DisplayName("updateFilm – ошибка: фильм не найден")
    void updateFilm_throws_whenFilmNotFound() {
        FilmRequestUpdateDto dto = new FilmRequestUpdateDto(
                10L, "New Name", Set.of(), null,
                "New Desc", LocalDate.of(2001, 2, 3), 90
        );

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

        Film existing = new Film();
        existing.setId(10L);
        existing.setGenres(new HashMap<>());


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
                10L, "New Name",
                Set.of(new GenreRequestDto(1L), new GenreRequestDto(2L)),
                null,
                "New Desc", LocalDate.of(2001, 2, 3), 90
        );

        Film existing = new Film();
        existing.setId(10L);
        existing.setGenres(new HashMap<>());

        when(filmStorage.getById(10L)).thenReturn(Optional.of(existing));

        when(genresStorage.getByIds(Set.of(1L, 2L)))
                .thenReturn(new HashMap<>(Map.of(1L, new Genre(1L, "Комедия")))); // меньше, чем req.getGenres().size()

        assertThrows(ValidationException.class, () -> filmService.updateFilm(dto));

        verify(filmStorage, never()).update(any());
        verify(genresByFilmsDbStorage, never()).update(anyLong(), anySet());
    }

    @Test
    @DisplayName("getAllFilms – успех: обогащает жанры и mpa")
    void getAllFilms_success_enrichesAndFiltersNull() {
        Film f1 = new Film();
        f1.setId(1L);
        f1.setName("A");
        f1.setDescription("DA");
        f1.setReleaseDate(LocalDate.of(2000, 1, 1));
        f1.setDuration(100);
        f1.setMpa(new Mpa(1L, null));
        f1.setGenres(new HashMap<>());

        Film f2 = new Film();
        f2.setId(2L);
        f2.setName("B");
        f2.setDescription("DB");
        f2.setReleaseDate(LocalDate.of(2001, 1, 1));
        f2.setDuration(110);
        f2.setMpa(new Mpa(2L, null));
        f2.setGenres(new HashMap<>());

        List<Film> films = new ArrayList<>();
        films.add(f1);
        films.add(f2);

        Map<Long, Genre> genres = new HashMap<>(Map.of(
                1L, new Genre(1L, "Комедия"),
                2L, new Genre(2L, "Драма")
        ));

        Map<Long, Mpa> mpas = new HashMap<>(Map.of(
                1L, new Mpa(1L, "G"),
                2L, new Mpa(2L, "PG")
        ));

        Map<Long, Set<Long>> filmGenresMap = new HashMap<>(Map.of(
                1L, Set.of(1L, 2L),
                2L, Set.of(2L)
        ));

        when(filmStorage.getAll()).thenReturn(films);
        when(genresStorage.getAll()).thenReturn(genres);
        when(mpaStorage.getAll()).thenReturn(mpas);
        when(genresByFilmsDbStorage.getByFilmIds(Set.of(1L, 2L))).thenReturn(filmGenresMap);

        FilmResponseDto r1 = new FilmResponseDto(
                1L, "A",
                List.of(new GenreResponseDto(1, "Комедия"), new GenreResponseDto(2, "Драма")),
                new MpaResponseDto(1, "G"),
                "DA",
                LocalDate.of(2000, 1, 1), 100
        );


        List<FilmResponseDto> result = filmService.getAllFilms();

        assertEquals(2, result.size());
        assertEquals(r1, result.get(0));
    }

    @Test
    @DisplayName("getAllFilms – ветка: genres null или filmGenresMap null – маппит как есть")
    void getAllFilms_branch_whenGenresNull() {
        Film f1 = new Film();
        f1.setId(1L);
        f1.setName("A");
        f1.setMpa(new Mpa(1L, null));
        f1.setGenres(new HashMap<>());

        when(filmStorage.getAll()).thenReturn(List.of(f1));
        when(mpaStorage.getAll()).thenReturn(new HashMap<>(Map.of(1L, new Mpa(1L, "G"))));

        FilmResponseDto expected = new FilmResponseDto(
                1L, "A",
                List.of(),
                new MpaResponseDto(1, "G"),
                null,
                null,
                null
        );

        List<FilmResponseDto> result = filmService.getAllFilms();

        assertEquals(1, result.size());
        assertEquals(expected, result.get(0));
    }

    @Test
    @DisplayName("getPopularFilms – успех: собирает жанры по filmIds и фильтрует null из mapper")
    void getPopularFilms_success_filtersNull() {
        Film f1 = new Film();
        f1.setId(1L);
        f1.setName("A");
        f1.setMpa(new Mpa(1L, null));
        f1.setGenres(new HashMap<>());

        Film f2 = new Film();
        f2.setId(2L);
        f2.setName("B");
        f2.setMpa(new Mpa(1L, null));
        f2.setGenres(new HashMap<>());

        when(filmStorage.getPopularFilms(10)).thenReturn(List.of(f1, f2));

        when(genresStorage.getAll()).thenReturn(new HashMap<>(Map.of(
                1L, new Genre(1L, "Комедия")
        )));

        when(mpaStorage.getAll()).thenReturn(new HashMap<>(Map.of(
                1L, new Mpa(1L, "G")
        )));

        when(genresByFilmsDbStorage.getByFilmIds(Set.of(1L, 2L)))
                .thenReturn(new HashMap<>(Map.of(
                        1L, Set.of(1L),
                        2L, Set.of(1L)
                )));

        FilmResponseDto r1 = new FilmResponseDto(
                1L, "A",
                List.of(new GenreResponseDto(1, "Комедия")),
                new MpaResponseDto(1, "G"),
                null, null, null
        );


        List<FilmResponseDto> result = filmService.getPopularFilms(10);

        assertEquals(2, result.size());
        assertEquals(r1, result.get(0));
        verify(genresByFilmsDbStorage).getByFilmIds(Set.of(1L, 2L));
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
        film.setMpa(new Mpa(1L, null));
        film.setGenres(new HashMap<>());

        when(filmStorage.getById(10L)).thenReturn(Optional.of(film));
        when(mpaStorage.getById(1L)).thenReturn(Optional.of(new Mpa(1L, "G")));

        when(genresByFilmsDbStorage.getByFilmId(10L)).thenReturn(Set.of(1L));
        when(genresStorage.getByIds(Set.of(1L))).thenReturn(new HashMap<>(Map.of(
                1L, new Genre(1L, "Комедия")
        )));

        FilmResponseDto expected = new FilmResponseDto(
                10L, "A",
                List.of(new GenreResponseDto(1, "Комедия")),
                new MpaResponseDto(1, "G"),
                "DA", LocalDate.of(2000, 1, 1), 100
        );


        FilmResponseDto actual = filmService.getById(10L);

        assertEquals(expected, actual);
        verify(genresByFilmsDbStorage).getByFilmId(10L);
        verify(genresStorage).getByIds(Set.of(1L));
    }

    @Test
    @DisplayName("getById – ошибка: фильм не найден")
    void getById_throws_whenFilmNotFound() {
        when(filmStorage.getById(10L)).thenReturn(Optional.empty());

        assertThrows(FilmNotFoundException.class, () -> filmService.getById(10L));

        verifyNoInteractions(mpaStorage, genresByFilmsDbStorage, genresStorage);
    }

    @Test
    @DisplayName("getById – ошибка: mpa не найден")
    void getById_throws_whenMpaNotFound() {
        Film film = new Film();
        film.setId(10L);
        film.setMpa(new Mpa(999L, null));
        film.setGenres(new HashMap<>());

        when(filmStorage.getById(10L)).thenReturn(Optional.of(film));
        when(mpaStorage.getById(999L)).thenReturn(Optional.empty());

        assertThrows(MpaNotFoundException.class, () -> filmService.getById(10L));

        verify(genresByFilmsDbStorage, never()).getByFilmId(anyLong());
        verify(genresStorage, never()).getByIds(anySet());
    }
}
