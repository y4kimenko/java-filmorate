package ru.yandex.practicum.filmorate.dal.film.mappers;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.film.Film;


import java.sql.ResultSet;
import java.sql.SQLException;

public class FilmRowMapper implements RowMapper<Film> {
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(rs.getLong("id"));
        film.setName(rs.getString("title"));

        film.setMpa( (rs.getLong("mpa_id") == 0 && rs.wasNull()) ? null : rs.getLong("mpa_id"));

        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        film.setDuration(rs.getInt("duration"));
        return film;
    }
}
