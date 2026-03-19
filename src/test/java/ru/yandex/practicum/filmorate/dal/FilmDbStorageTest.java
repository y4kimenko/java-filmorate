package ru.yandex.practicum.filmorate.dal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import ru.yandex.practicum.filmorate.dal.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@JdbcTest
@Import(FilmDbStorage.class)
class FilmDbStorageTest {

    @Autowired
    private FilmDbStorage filmStorage;

    @Autowired
    private NamedParameterJdbcTemplate jdbc;


    @BeforeEach
    void setUp() {
        var jt = jdbc.getJdbcTemplate();

        jt.update("DELETE FROM film_genres");
        jt.update("DELETE FROM user_film_likes");
        jt.update("DELETE FROM friendship");
        jt.update("DELETE FROM film");
        jt.update("DELETE FROM users");
        jt.update("DELETE FROM mpa");

        jt.update("INSERT INTO mpa (id, name) VALUES (1, 'G')");
        jt.update("INSERT INTO mpa (id, name) VALUES (2, 'PG')");
        jt.update("INSERT INTO mpa (id, name) VALUES (3, 'PG-13')");
        jt.update("INSERT INTO mpa (id, name) VALUES (4, 'R')");
        jt.update("INSERT INTO mpa (id, name) VALUES (5, 'NC-17')");

        // пользователи под лайки 10–14
        for (long id = 10; id <= 14; id++) {
            jt.update(
                    "INSERT INTO users (id, email, login, name, birthday) VALUES (?, ?, ?, ?, ?)",
                    id,
                    "u" + id + "@mail.com",
                    "login" + id,
                    "User " + id,
                    java.sql.Date.valueOf(LocalDate.of(2000, 1, 1))
            );
        }
    }

    @Test
    @DisplayName("save –– сохраняет фильм без mpa и возвращает с id")
    void save_withoutMpa_setsId_andPersists() {
        Film film = new Film();
        film.setName("A");
        film.setDescription("DA");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(100);
        film.setMpa(null);

        Film saved = filmStorage.save(film);

        assertNotNull(saved.getId());
        assertTrue(saved.getId() > 0);

        Film fromDb = filmStorage.getById(saved.getId()).orElseThrow();

        assertEquals(saved.getId(), fromDb.getId());
        assertEquals("A", fromDb.getName());
        assertEquals("DA", fromDb.getDescription());
        assertEquals(LocalDate.of(2000, 1, 1), fromDb.getReleaseDate());
        assertEquals(100, fromDb.getDuration());
        assertNull(fromDb.getMpa());
    }

    @Test
    @DisplayName("save –– сохраняет фильм с mpa")
    void save_withMpa_persistsMpa() {
        Film film = new Film();
        film.setName("B");
        film.setDescription("DB");
        film.setReleaseDate(LocalDate.of(2001, 2, 2));
        film.setDuration(120);
        film.setMpa(new Mpa(1L, null));

        Film saved = filmStorage.save(film);

        Film fromDb = filmStorage.getById(saved.getId()).orElseThrow();
        assertEquals(new Mpa(1L, null), fromDb.getMpa());
    }

    @Test
    @DisplayName("update –– обновляет фильм без mpa")
    void update_withoutMpa_updatesFields() {
        Film film = new Film();
        film.setName("Old");
        film.setDescription("OldD");
        film.setReleaseDate(LocalDate.of(1999, 1, 1));
        film.setDuration(90);
        film.setMpa(null);

        Film saved = filmStorage.save(film);

        saved.setName("New");
        saved.setDescription("NewD");
        saved.setReleaseDate(LocalDate.of(2002, 3, 3));
        saved.setDuration(150);
        saved.setMpa(null);

        filmStorage.update(saved);

        Film fromDb = filmStorage.getById(saved.getId()).orElseThrow();
        assertEquals("New", fromDb.getName());
        assertEquals("NewD", fromDb.getDescription());
        assertEquals(LocalDate.of(2002, 3, 3), fromDb.getReleaseDate());
        assertEquals(150, fromDb.getDuration());
        assertNull(fromDb.getMpa());
    }

    @Test
    @DisplayName("update –– обновляет фильм с mpa")
    void update_withMpa_updatesFieldsAndMpa() {
        Film film = new Film();
        film.setName("Old");
        film.setDescription("OldD");
        film.setReleaseDate(LocalDate.of(1999, 1, 1));
        film.setDuration(90);
        film.setMpa(new Mpa(1L, null));

        Film saved = filmStorage.save(film);

        saved.setName("New");
        saved.setDescription("NewD");
        saved.setReleaseDate(LocalDate.of(2002, 3, 3));
        saved.setDuration(150);
        saved.setMpa(new Mpa(2L, null));

        filmStorage.update(saved);

        Film fromDb = filmStorage.getById(saved.getId()).orElseThrow();
        assertEquals("New", fromDb.getName());
        assertEquals(new Mpa(2L, null), fromDb.getMpa());
    }

    @Test
    @DisplayName("existsById –– true если фильм есть, false если нет")
    void existsById_works() {
        Film film = new Film();
        film.setName("A");
        film.setDescription("DA");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(100);

        Film saved = filmStorage.save(film);

        assertTrue(filmStorage.existsById(saved.getId()));
        assertFalse(filmStorage.existsById(999_999L));
    }

    @Test
    @DisplayName("getAll –– возвращает фильмы в порядке id")
    void getAll_returnsOrderedMap() {
        Film f1 = new Film();
        f1.setName("A");
        f1.setDescription("DA");
        f1.setReleaseDate(LocalDate.of(2000, 1, 1));
        f1.setDuration(100);

        Film f2 = new Film();
        f2.setName("B");
        f2.setDescription("DB");
        f2.setReleaseDate(LocalDate.of(2001, 1, 1));
        f2.setDuration(110);

        Film s1 = filmStorage.save(f1);
        Film s2 = filmStorage.save(f2);

        LinkedHashMap<Long, Film> all = filmStorage.getAll();

        assertEquals(2, all.size());

        List<Long> ids = new ArrayList<>(all.keySet());
        assertEquals(List.of(s1.getId(), s2.getId()), ids);
    }

    @Test
    @DisplayName("getPopularFilms –– сортирует по кол-ву лайков desc, затем по id asc, и применяет limit")
    void getPopularFilms_ordersByLikesThenId_andLimits() {
        Film f1 = filmStorage.save(film("F1", 1L));
        Film f2 = filmStorage.save(film("F2", 1L));
        Film f3 = filmStorage.save(film("F3", 1L));

        // f1 –– 2 лайка
        like(10L, f1.getId());
        like(11L, f1.getId());

        // f2 –– 1 лайк
        like(12L, f2.getId());

        // f3 –– 2 лайка (тайбрейк по id)
        like(13L, f3.getId());
        like(14L, f3.getId());

        List<Film> popular2 = filmStorage.getPopularFilms(2);

        assertEquals(2, popular2.size());
        assertEquals(f1.getId(), popular2.get(0).getId());
        assertEquals(f3.getId(), popular2.get(1).getId());
    }

    private Film film(String name, Long mpa) {
        Film f = new Film();
        f.setName(name);
        f.setDescription("D");
        f.setReleaseDate(LocalDate.of(2000, 1, 1));
        f.setDuration(100);
        f.setMpa(new Mpa(mpa, null));
        return f;
    }

    private void like(long userId, long filmId) {
        jdbc.getJdbcTemplate().update(
                "INSERT INTO user_film_likes (user_id, film_id) VALUES (?, ?)",
                userId, filmId
        );
    }
}