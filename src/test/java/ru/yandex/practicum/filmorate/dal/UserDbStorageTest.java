package ru.yandex.practicum.filmorate.dal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import ru.yandex.practicum.filmorate.dal.user.UserDbStorage;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@JdbcTest
@Import({UserDbStorage.class})
class UserDbStorageTest {

    @Autowired
    private UserDbStorage userStorage;

    @Autowired
    private NamedParameterJdbcTemplate jdbc;

    private static User user(String email, String login, String name, LocalDate birthday) {
        User u = new User();
        u.setEmail(email);
        u.setLogin(login);
        u.setName(name);
        u.setBirthday(birthday);
        return u;
    }

    @BeforeEach
    void setUp() {
        var jt = jdbc.getJdbcTemplate();

        jt.update("DELETE FROM friendship");
        jt.update("DELETE FROM user_film_likes");
        jt.update("DELETE FROM users");
    }

    @Test
    @DisplayName("save –– сохраняет пользователя и проставляет id")
    void save_setsId_andPersists() {
        User u = user("a@a.ru", "loginA", "Name A", LocalDate.of(2000, 1, 1));

        User saved = userStorage.save(u);

        assertNotNull(saved.getId());
        assertTrue(saved.getId() > 0);

        User fromDb = userStorage.findById(saved.getId()).orElseThrow();
        assertEquals(saved.getId(), fromDb.getId());
        assertEquals("a@a.ru", fromDb.getEmail());
        assertEquals("loginA", fromDb.getLogin());
        assertEquals("Name A", fromDb.getName());
        assertEquals(LocalDate.of(2000, 1, 1), fromDb.getBirthday());
    }

    @Test
    @DisplayName("getById –– возвращает empty если пользователя нет")
    void getById_returnsEmpty_whenMissing() {
        Optional<User> res = userStorage.findById(999_999L);
        assertTrue(res.isEmpty());
    }

    @Test
    @DisplayName("update –– обновляет поля пользователя")
    void update_updatesFields() {
        User saved = userStorage.save(user("a@a.ru", "loginA", "Name A", LocalDate.of(2000, 1, 1)));

        saved.setEmail("new@a.ru");
        saved.setLogin("newLogin");
        saved.setName("New Name");
        saved.setBirthday(LocalDate.of(1999, 12, 31));

        userStorage.update(saved);

        User fromDb = userStorage.findById(saved.getId()).orElseThrow();
        assertEquals("new@a.ru", fromDb.getEmail());
        assertEquals("newLogin", fromDb.getLogin());
        assertEquals("New Name", fromDb.getName());
        assertEquals(LocalDate.of(1999, 12, 31), fromDb.getBirthday());
    }

    @Test
    @DisplayName("getAll –– возвращает пользователей по возрастанию id")
    void getAll_returnsOrderedById() {
        User u1 = userStorage.save(user("a@a.ru", "a", "A", LocalDate.of(2000, 1, 1)));
        User u2 = userStorage.save(user("b@b.ru", "b", "B", LocalDate.of(2000, 1, 2)));

        List<User> all = userStorage.getAll();

        assertEquals(2, all.size());
        assertEquals(List.of(u1.getId(), u2.getId()), all.stream().map(User::getId).toList());
    }

    @Test
    @DisplayName("existsById –– true если есть, false если нет")
    void existsById_works() {
        User saved = userStorage.save(user("a@a.ru", "a", "A", LocalDate.of(2000, 1, 2)));

        assertTrue(userStorage.existsById(saved.getId()));
        assertFalse(userStorage.existsById(999_999L));
    }

    @Test
    @DisplayName("getByIds –– возвращает список пользователей по набору id")
    void getByIds_returnsUsers() {
        User u1 = userStorage.save(user("a@a.ru", "a", "A", LocalDate.of(2000, 1, 1)));
        User u2 = userStorage.save(user("b@b.ru", "b", "B", LocalDate.of(2000, 1, 1)));
        userStorage.save(user("c@c.ru", "c", "C", LocalDate.of(2000, 1, 1)));

        List<User> found = userStorage.getByIds(Set.of(u1.getId(), u2.getId()));

        assertEquals(2, found.size());

        List<Long> ids = found.stream().map(User::getId).sorted().toList();
        List<Long> expected = List.of(u1.getId(), u2.getId()).stream().sorted(Comparator.naturalOrder()).toList();

        assertEquals(expected, ids);
    }
}
