package ru.yandex.practicum.filmorate.dal.user;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.user.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.model.user.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;


@Repository
@Primary
@RequiredArgsConstructor
@Slf4j
public class UserDbStorage implements UserStorage {
    // users
    private static final String SELECT_USER_BY_ID = """
            SELECT id, email, login, name, birthday
            FROM users
            WHERE id = :id""";
    private static final String SELECT_USER_BY_IDS = """
            SELECT id, email, login, name, birthday
            FROM users
            WHERE id IN (:ids)""";
    private static final String SELECT_ALL_USERS = """
            SELECT id, email, login, name, birthday
            FROM users
            ORDER BY id""";

    private static final String INSERT_USER = """
            INSERT INTO users (email, login, name, birthday)
            Values (:email, :login, :name, :birthday)""";

    private static final String UPDATE_USER = """
            UPDATE users
            SET
                email = :email,
                login = :login,
                name = :name,
                birthday = :birthday
            WHERE id = :id;""";

    private static final String EXISTS_BY_ID = """
            SELECT count(id)
            FROM users
            WHERE id = :id""";


    private final NamedParameterJdbcTemplate jdbcTemplate;


    @Override
    public User save(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(INSERT_USER, new MapSqlParameterSource()
                        .addValue("email", user.getEmail())
                        .addValue("login", user.getLogin())
                        .addValue("name", user.getName())
                        .addValue("birthday", user.getBirthday()),
                keyHolder,
                new String[]{"id"}
        );
        log.info("Save a value in a table 'users' by ID={} with fields: email, login, name, birthday", user.getId());

        Number key = keyHolder.getKey();
        if (key == null) {
            throw new IllegalStateException("БД не вернула сгенерированный id");
        }

        user.setId(key.longValue());
        return user;
    }

    @Override
    public User update(User user) {
        log.info("update() - request id={}, email={}, login={}, name={}, birthday={}",
                user.getId(), user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());

        // Добавление в таблицу users

        jdbcTemplate.update(UPDATE_USER, new MapSqlParameterSource()
                .addValue("id", user.getId())
                .addValue("email", user.getEmail())
                .addValue("login", user.getLogin())
                .addValue("name", user.getName())
                .addValue("birthday", user.getBirthday())
        );
        log.info("Updating a value in the table 'users' by ID={} with fields: email, login, name, birthday", user.getId());


        return user;
    }

    @Override
    public List<User> getAll() {
        List<User> users = jdbcTemplate.queryForStream(SELECT_ALL_USERS,
                new MapSqlParameterSource(),
                new UserRowMapper()).toList();


        log.debug("getAll() – total={}", users.size());
        return users;
    }

    @Override
    public Optional<User> getById(long id) {
        Optional<User> user = jdbcTemplate.query(
                SELECT_USER_BY_ID,
                new MapSqlParameterSource("id", id),
                new UserRowMapper()
        ).stream().findFirst();
        log.info("getById() – request UserId={}", id);
        return user;
    }


    @Override
    public boolean existsById(long id) {
        Long count = jdbcTemplate.queryForObject(
                EXISTS_BY_ID,
                new MapSqlParameterSource("id", id),
                Long.class
        );
        return (count != null) && (count != 0);
    }

    @Override
    public List<User> getByIds(Set<Long> ids) {
        List<User> user = jdbcTemplate.query(
                SELECT_USER_BY_IDS,
                new MapSqlParameterSource("ids", ids),
                new UserRowMapper()
        );
        log.info("getByIds() – request UserIds={}", ids);
        return user;
    }
}
