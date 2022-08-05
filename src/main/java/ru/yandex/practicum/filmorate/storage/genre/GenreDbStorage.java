package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Primary
public class GenreDbStorage implements GenreStorage{


    private final JdbcTemplate jdbcTemplate;
    @Autowired
    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    @Override
    public Genre getGenreById(Integer id) { // TODO
        return jdbcTemplate.queryForObject("SELECT * FROM genres WHERE genre_id = ?",
                this::mapRowToGenre, id);
    }
    @Override
    public List<Genre> allGenres() { // TODO
        return jdbcTemplate.query("SELECT * FROM genres", this::mapRowToGenre);
    }
    @Override
    public List<Genre> loadFilmGenre(Long id) {
        List<Integer> genreIds = jdbcTemplate.queryForList("SELECT GENRE_ID FROM FILM_GENRE WHERE FILM_ID = ?",
                Integer.class, id);
        return genreIds.stream().map(this::getGenreById).collect(Collectors.toList());
    }
    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.builder()
                .id(resultSet.getInt("genre_id"))
                .name(resultSet.getString("genre"))
                .build();
    }
}
