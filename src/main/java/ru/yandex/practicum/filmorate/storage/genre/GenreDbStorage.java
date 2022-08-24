package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.WrongParameterException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class GenreDbStorage implements GenreStorage{

    private final JdbcTemplate jdbcTemplate;
    @Autowired
    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    @Override
    public Genre getGenreById(Integer id) {
        return jdbcTemplate.queryForObject("SELECT GENRE_ID, GENRE FROM genres WHERE genre_id = ?",
                this::mapRowToGenre, id);
    }
    @Override
    public List<Genre> allGenres() {
        return jdbcTemplate.query("SELECT GENRE_ID, GENRE FROM genres", this::mapRowToGenre);
    }
    @Override
    public void checkGenreExistence(Integer id) {
        final String sqlQuery = "SELECT genre_id, genre FROM GENRES WHERE genre_id = ?";
        try {
            jdbcTemplate.queryForObject(sqlQuery, this::mapRowToGenre, id);
        } catch (DataAccessException e) {
            throw new WrongParameterException("Неверный genre.id");
        }
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.builder()
                .id(resultSet.getInt("genre_id"))
                .name(resultSet.getString("genre"))
                .build();
    }
}
