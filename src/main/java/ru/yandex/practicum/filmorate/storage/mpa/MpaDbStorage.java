package ru.yandex.practicum.filmorate.storage.mpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Component
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Mpa getMpaById(Integer id) {
        return jdbcTemplate.queryForObject("SELECT * FROM mpa_rating WHERE mpa_id = ?",
                this::mapRowToMpa, id);
    }
    @Override
    public Collection<Mpa> allMpa() { // NOTHING SPECIAL
        return jdbcTemplate.query("SELECT * FROM mpa_rating", this::mapRowToMpa);
    }

    private Mpa mapRowToMpa(ResultSet resultSet, int rowNum) throws SQLException {
        return Mpa.builder()
                .id(resultSet.getShort("mpa_id"))
                .name(resultSet.getString("mpa"))
                .build();
    }
}
