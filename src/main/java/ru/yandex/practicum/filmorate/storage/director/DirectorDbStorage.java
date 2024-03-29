package ru.yandex.practicum.filmorate.storage.director;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.WrongParameterException;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class DirectorDbStorage implements DirectorStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DirectorDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    @Override
    public Collection<Director> getAllDirectors() {
        return jdbcTemplate.query("SELECT director_id, director_name FROM directors", this::mapRowToDirector);
    }
    @Override
    public Director findDirectorById(Integer id) {
        Director director;
        try {
            director = jdbcTemplate.queryForObject("SELECT director_id, director_name " +
                            "FROM directors " +
                            "WHERE director_id = ?", this::mapRowToDirector, id);
        } catch (DataAccessException e) {
            throw new WrongParameterException("director.id не найден");
        }
        return director;
    }

    @Override
    public Director create(Director director) {
        final String sqlQuery = "INSERT INTO directors (director_name) VALUES (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
                PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"director_id"});
                stmt.setString(1, director.getName());
                return stmt;
            }, keyHolder);
        director.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        return findDirectorById(director.getId());
    }
    @Override
    public Director update(Director director) {
        int updatedRows = jdbcTemplate.update("UPDATE directors SET director_name = ? WHERE director_id = ?"
                , director.getName()
                , director.getId());
        if (updatedRows == 0) {
            throw new WrongParameterException("director.id не найден");
        }
        return director;
    }
    @Override
    public void delete(Integer id) {
        if (jdbcTemplate.update("DELETE FROM DIRECTORS WHERE director_id = ?", id) == 0) {
            throw new WrongParameterException("director.id не найден");
        }
    }

    public List<Director> directorsByFilm(Long id) {
        final String sqlQuery = "SELECT director_id FROM film_director WHERE FILM_ID = ?";
        List<Integer> ids = jdbcTemplate.queryForList(sqlQuery, Integer.class, id);
        return ids.stream().map(this::findDirectorById).collect(Collectors.toList());
    }

    private Director mapRowToDirector(ResultSet rs, int rowNum) throws SQLException {
        return Director.builder()
                .id(rs.getInt("director_id"))
                .name(rs.getString("director_name"))
                .build();
    }
}