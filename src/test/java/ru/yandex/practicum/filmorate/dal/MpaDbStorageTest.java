package ru.yandex.practicum.filmorate.dal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import ru.yandex.practicum.filmorate.dal.mpa.MpaDbStorage;
import ru.yandex.practicum.filmorate.dto.mpa.response.MpaResponseDto;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@JdbcTest
@AutoConfigureTestDatabase
@Import(MpaDbStorage.class)
class MpaDbStorageTest {

    @Autowired
    private MpaDbStorage mpaStorage;

    @Autowired
    private NamedParameterJdbcTemplate jdbc;

    @BeforeEach
    void setUp() {
        var jt = jdbc.getJdbcTemplate();

        jt.execute("DELETE FROM film_genres");
        jt.execute("DELETE FROM user_film_likes");
        jt.execute("DELETE FROM film");
        jt.execute("DELETE FROM mpa");

        jt.execute("INSERT INTO mpa (id, name) VALUES (1, 'G')");
        jt.execute("INSERT INTO mpa (id, name) VALUES (2, 'PG')");
        jt.execute("INSERT INTO mpa (id, name) VALUES (3, 'PG-13')");
    }

    @Test
    @DisplayName("getAll –– возвращает все рейтинги")
    void getAll_returnsAll() {
        Set<MpaResponseDto> result = mpaStorage.getAll();

        assertEquals(3, result.size());
        assertTrue(result.contains(new MpaResponseDto(1, "G")));
        assertTrue(result.contains(new MpaResponseDto(2, "PG")));
        assertTrue(result.contains(new MpaResponseDto(3, "PG-13")));
    }

    @Test
    @DisplayName("getById –– возвращает рейтинг если найден")
    void getById_returnsWhenFound() {
        Optional<MpaResponseDto> res = mpaStorage.getById(2L);

        assertTrue(res.isPresent());
        assertEquals(new MpaResponseDto(2, "PG"), res.get());
    }

    @Test
    @DisplayName("getById –– возвращает empty если не найден")
    void getById_returnsEmptyWhenMissing() {
        Optional<MpaResponseDto> res = mpaStorage.getById(999L);

        assertTrue(res.isEmpty());
    }
}
