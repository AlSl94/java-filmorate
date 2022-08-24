package ru.yandex.practicum.filmorate.storage.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.WrongParameterException;
import ru.yandex.practicum.filmorate.model.Event;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Collection;

@Component
public class EventDbStorage implements EventStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public EventDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void createEvent(long userId, long entityId, int eventTypeId, int operationId) {
        final String sqlQuery = "INSERT INTO events (user_id, entity_id, type_id, operation_id, event_timestamp) " +
                "VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sqlQuery, userId, entityId, eventTypeId, operationId, LocalDateTime.now());
    }

    @Override
    public Collection<Event> getFeedByUserId(long userId) {
        final String sqlQuery = "SELECT event_id, user_id, entity_id, type_id, operation_id, event_timestamp " +
                "FROM events " +
                "WHERE user_id = ? " +
                "ORDER BY event_id";
        Collection<Event> events = jdbcTemplate.query(sqlQuery, this::mapRowToEvent, userId);
        if (events.isEmpty()) {
            throw new WrongParameterException("user.id не найден или для него нет событий");
        }
        return events;
    }

    private String getTypeNameById(int typeId) {
        return jdbcTemplate.queryForObject("SELECT type_name FROM event_types " +
                                               "WHERE type_id = ?", String.class, typeId);
    }

    private String getOperationNameById(int operationId) {
        return jdbcTemplate.queryForObject("SELECT operation_name FROM event_operations " +
                                               "WHERE operation_id = ?", String.class, operationId);
    }

    private Event mapRowToEvent(ResultSet resultSet, int rowNum) throws SQLException {
        return Event.builder()
                .eventId(resultSet.getLong("event_id"))
                .userId(resultSet.getLong("user_id"))
                .entityId(resultSet.getLong("entity_id"))
                .eventType(getTypeNameById(resultSet.getInt("type_id")))
                .operation(getOperationNameById(resultSet.getInt("operation_id")))
                .timestamp(resultSet.getTimestamp("event_timestamp").getTime())
                .build();
    }
}